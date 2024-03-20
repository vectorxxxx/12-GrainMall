package xyz.funnyboy.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.constant.OrderConstant;
import xyz.funnyboy.common.constant.WareOrderTaskConstant;
import xyz.funnyboy.common.exception.NoStockException;
import xyz.funnyboy.common.to.OrderTO;
import xyz.funnyboy.common.to.es.SkuHasStockVO;
import xyz.funnyboy.common.to.mq.StockDetailTO;
import xyz.funnyboy.common.to.mq.StockLockedTO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.config.MyRabbitMQConfig;
import xyz.funnyboy.gulimall.ware.dao.WareSkuDao;
import xyz.funnyboy.gulimall.ware.entity.WareOrderTaskDetailEntity;
import xyz.funnyboy.gulimall.ware.entity.WareOrderTaskEntity;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;
import xyz.funnyboy.gulimall.ware.feign.OrderFeignService;
import xyz.funnyboy.gulimall.ware.feign.ProductFeignService;
import xyz.funnyboy.gulimall.ware.service.WareOrderTaskDetailService;
import xyz.funnyboy.gulimall.ware.service.WareOrderTaskService;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;
import xyz.funnyboy.gulimall.ware.vo.OrderItemVO;
import xyz.funnyboy.gulimall.ware.vo.OrderVO;
import xyz.funnyboy.gulimall.ware.vo.WareSkuLockVO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService
{
    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        final String wareId = (String) params.get("wareId");
        final String skuId = (String) params.get("skuId");
        final LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<WareSkuEntity>()
                .eq(!StringUtils.isEmpty(wareId), WareSkuEntity::getWareId, wareId)
                .eq(!StringUtils.isEmpty(skuId), WareSkuEntity::getSkuId, skuId);
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public double addStock(Long wareId, Long skuId, Integer skuNum) {
        // 查询商品库存数据
        WareSkuEntity wareSkuEntity = baseMapper.selectOne(new LambdaQueryWrapper<WareSkuEntity>()
                .eq(WareSkuEntity::getWareId, wareId)
                .eq(WareSkuEntity::getSkuId, skuId));

        // 查询商品名称和价格
        String skuName = "";
        double price = 0;
        final R info = productFeignService.info(skuId);
        if (info.getCode() == 0) {
            final Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
            skuName = (String) data.get("skuName");
            price = (Double) data.get("price");
        }

        // 有则更新，无则新增
        if (wareSkuEntity == null) {
            wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setStock(skuNum);
        }
        else {
            wareSkuEntity.setStock(wareSkuEntity.getStock() + skuNum);
        }
        wareSkuEntity.setWareId(wareId);
        wareSkuEntity.setSkuId(skuId);
        wareSkuEntity.setSkuName(skuName);
        wareSkuEntity.setStockLocked(0);
        this.saveOrUpdate(wareSkuEntity);
        return price;
    }

    @Override
    public List<SkuHasStockVO> getSkuHasStock(List<Long> skuIds) {
        return skuIds
                .stream()
                .map(skuId -> {
                    final SkuHasStockVO skuHasStockVO = new SkuHasStockVO();
                    skuHasStockVO.setSkuId(skuId);
                    Long count = baseMapper.getSkuStock(skuId);
                    skuHasStockVO.setHasStock(count != null && count > 0);
                    return skuHasStockVO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVO vo) {
        // 当定库存之前先保存订单 以便后来消息撤回
        final WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        // 封装待锁定库存项Map
        final List<OrderItemVO> locks = vo.getLocks();
        final Map<Long, OrderItemVO> lockItemMap = locks
                .stream()
                .collect(Collectors.toMap(OrderItemVO::getSkuId, Function.identity(), (r1, r2) -> r2));

        // 查询（库存 - 库存锁定 >= 待锁定库存数）的仓库
        final Set<Long> skuIds = lockItemMap.keySet();
        final List<WareSkuEntity> wareSkuEntityList = baseMapper
                .selectListHasSkuStock(skuIds)
                .stream()
                .filter(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() >= lockItemMap
                        .get(wareSkuEntity.getSkuId())
                        .getCount())
                .collect(Collectors.toList());
        // 判断是否查询到仓库
        if (CollectionUtils.isEmpty(wareSkuEntityList)) {
            throw new NoStockException(skuIds);
        }
        // 将查询出的仓库数据封装成Map，key:skuId  val:wareId
        final Map<Long, List<WareSkuEntity>> wareSkuMap = wareSkuEntityList
                .stream()
                .collect(Collectors.groupingBy(WareSkuEntity::getSkuId));

        // 判断是否为每一个商品项至少匹配了一个仓库
        if (wareSkuMap.size() < locks.size()) {
            skuIds.removeAll(wareSkuMap.keySet());
            throw new NoStockException(skuIds);
        }

        // 所有商品都存在有库存的仓库
        // 锁定库存
        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntityList = new ArrayList<>();
        Map<Long, StockLockedTO> stockLockedTOMap = new HashMap<>();
        for (Map.Entry<Long, List<WareSkuEntity>> entry : wareSkuMap.entrySet()) {
            boolean skuStocked = false;
            final Long skuId = entry.getKey();
            final OrderItemVO orderItemVO = lockItemMap.get(skuId);
            final Integer count = orderItemVO.getCount();
            // 如果每一个商品都锁定成功 将当前商品锁定了几件的工作单记录发送给MQ
            for (WareSkuEntity wareSkuEntity : entry.getValue()) {
                final Long wareId = wareSkuEntity.getWareId();
                final Long num = baseMapper.lockSkuStock(skuId, wareId, count);
                if (num == 1) {
                    // 锁定成功，跳出循环
                    skuStocked = true;
                    // 创建库存锁定工作单详情（每一件商品锁定详情）
                    final WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, orderItemVO.getTitle(), count,
                            wareOrderTaskEntity.getId(), wareId, WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode());
                    wareOrderTaskDetailEntityList.add(wareOrderTaskDetailEntity);
                    // 告诉MQ库存锁定成功
                    final StockDetailTO detailTO = new StockDetailTO();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, detailTO);
                    final StockLockedTO lockedTO = new StockLockedTO(wareOrderTaskEntity.getId(), detailTO);
                    stockLockedTOMap.put(skuId, lockedTO);
                    break;
                }
            }
            if (!skuStocked) {
                // 匹配失败，当前商品所有仓库都未锁定成功
                throw new NoStockException(skuId);
            }
        }

        // 往库存工作单详情存储当前锁定（本地事务表）
        wareOrderTaskDetailService.saveBatch(wareOrderTaskDetailEntityList);

        // 发送消息
        for (WareOrderTaskDetailEntity taskDetailEntity : wareOrderTaskDetailEntityList) {
            final StockLockedTO stockLockedTO = stockLockedTOMap.get(taskDetailEntity.getSkuId());
            stockLockedTO
                    .getDetailTO()
                    .setId(taskDetailEntity.getId());
            rabbitTemplate.convertAndSend(MyRabbitMQConfig.EXCHANGE, MyRabbitMQConfig.LOCKED_ROUTING_KEY, stockLockedTO);
        }

        return true;
    }

    @Override
    public void unlockStock(StockLockedTO stockLockedTO) {
        final StockDetailTO detailTO = stockLockedTO.getDetailTO();
        final Long detailId = detailTO.getId();
        final WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailId);
        // 无需解锁
        if (detailEntity == null) {
            return;
        }

        final WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(detailEntity.getTaskId());
        final R r = orderFeignService.getOrderStatus(taskEntity.getOrderSn());
        if (r.getCode() != 0) {
            throw new RuntimeException("远程服务失败");
        }

        // 订单数据返回成功
        final OrderVO orderVO = r.getData(new TypeReference<OrderVO>() {});
        // 订单不存在或者已经被取消了，才能解锁库存
        if (orderVO == null || (int) orderVO.getStatus() == OrderConstant.OrderStatusEnum.CANCLED.getCode()) {
            // 订单已回滚 || 订单未回滚已取消状态
            if ((int) detailEntity.getLockStatus() == WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode()) {
                unlockStock(detailTO.getSkuId(), detailTO.getWareId(), detailTO.getSkuNum(), detailId);
            }
        }
    }

    @Override
    public void unlockStock(OrderTO orderTO) {
        log.info("orderTO={}", orderTO);
        final String orderSn = orderTO.getOrderSn();
        log.info("orderSn={}", orderSn);
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        if (taskEntity != null) {
            // 按照工作单找到所有没有解锁的库存进行解锁
            wareOrderTaskDetailService
                    .list(new LambdaQueryWrapper<WareOrderTaskDetailEntity>()
                            .eq(WareOrderTaskDetailEntity::getTaskId, taskEntity.getId())
                            .eq(WareOrderTaskDetailEntity::getLockStatus, WareOrderTaskConstant.LockStatusEnum.LOCKED.getCode()))
                    .forEach(detailEntity -> unlockStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum(), detailEntity.getId()));
        }
    }

    /**
     * 解锁库存
     *
     * @param skuId    SKU ID
     * @param wareId   Ware ID
     * @param skuNum   SKU 数量
     * @param detailId 详细信息 ID
     */
    private void unlockStock(Long skuId, Long wareId, Integer skuNum, Long detailId) {
        // 库存解锁
        baseMapper.unlockSkuStock(skuId, wareId, skuNum);
        // 更新库存工作单状态
        final WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(detailId);
        wareOrderTaskDetailEntity.setLockStatus(WareOrderTaskConstant.LockStatusEnum.UNLOCKED.getCode());
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }

}
