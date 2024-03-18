package xyz.funnyboy.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.exception.NoStockException;
import xyz.funnyboy.common.to.es.SkuHasStockVO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.ware.dao.WareSkuDao;
import xyz.funnyboy.gulimall.ware.entity.WareOrderTaskEntity;
import xyz.funnyboy.gulimall.ware.entity.WareSkuEntity;
import xyz.funnyboy.gulimall.ware.feign.ProductFeignService;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;
import xyz.funnyboy.gulimall.ware.vo.SkuWareHasStock;
import xyz.funnyboy.gulimall.ware.vo.WareSkuLockVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService
{
    @Autowired
    private ProductFeignService productFeignService;

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
        // [理论上]1. 按照下单的收获地址 找到一个就近仓库, 锁定库存
        // [实际上]1. 找到每一个商品在那个一个仓库有库存
        final List<SkuWareHasStock> hasStockList = vo
                .getLocks()
                .stream()
                .map(itemVO -> {
                    final SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
                    final Long skuId = itemVO.getSkuId();
                    skuWareHasStock.setSkuId(skuId);
                    skuWareHasStock.setWareId(baseMapper.listWareIdHasSkuStock(skuId));
                    skuWareHasStock.setNum(itemVO.getCount());
                    return skuWareHasStock;
                })
                .collect(Collectors.toList());

        // 锁定库存
        hasStockList.forEach(stock -> {
            boolean skuStocked = false;
            final Long skuId = stock.getSkuId();
            final List<Long> wareIdList = stock.getWareId();
            // 没有任何仓库有这个库存
            if (CollectionUtils.isEmpty(wareIdList)) {
                throw new NoStockException(skuId);
            }
            // 如果每一个商品都锁定成功 将当前商品锁定了几件的工作单记录发送给MQ
            for (Long wareId : wareIdList) {
                Long count = baseMapper.lockSkuStock(skuId, wareId, stock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    break;
                }
            }
            // 如果锁定失败 前面保存的工作单信息回滚了
            if (!skuStocked) {
                throw new NoStockException(skuId);
            }
        });
        return true;
    }

}
