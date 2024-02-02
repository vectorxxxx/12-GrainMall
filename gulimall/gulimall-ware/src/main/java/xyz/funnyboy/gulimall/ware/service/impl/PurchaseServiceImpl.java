package xyz.funnyboy.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import xyz.funnyboy.common.constant.WareConstant;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.ware.dao.PurchaseDao;
import xyz.funnyboy.gulimall.ware.entity.PurchaseDetailEntity;
import xyz.funnyboy.gulimall.ware.entity.PurchaseEntity;
import xyz.funnyboy.gulimall.ware.service.PurchaseDetailService;
import xyz.funnyboy.gulimall.ware.service.PurchaseService;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;
import xyz.funnyboy.gulimall.ware.vo.MergeVo;
import xyz.funnyboy.gulimall.ware.vo.PurchaseDoneVo;
import xyz.funnyboy.gulimall.ware.vo.PurchaseItemDoneVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService
{
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), new QueryWrapper<PurchaseEntity>());

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        final LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<PurchaseEntity>()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED.getCode())
                .or()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 合并采购需求
     *
     * @param mergeVo 合并 VO
     */
    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        final Long purchaseId = mergeVo.getPurchaseId();
        // 如果没有指定采购单 id，说明没选采购单
        if (purchaseId == null) {
            final PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            // 改用 mybatis-plus 的自动填充
            // purchaseEntity.setCreateTime(new Date());
            // purchaseEntity.setUpdateTime(new Date());
            baseMapper.insert(purchaseEntity);
        }

        // 合并采购需求
        final List<Long> purchaseDetailIdList = mergeVo.getItems();
        final List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService
                .listByIds(purchaseDetailIdList)
                .stream()
                // 过滤出没有采购和采购失败的
                .filter(purchaseDetail -> purchaseDetail.getStatus() < WareConstant.PurchaseDetailStatusEnum.BUYING.getCode() || purchaseDetail.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode())
                .peek(purchaseDetail -> {
                    purchaseDetail.setPurchaseId(purchaseId);
                    purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(purchaseDetailEntityList)) {
            return;
        }

        // 更新采购需求
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        // 更新采购信息中的仓库ID
        final PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setWareId(purchaseDetailEntityList
                .get(0)
                .getWareId());
        baseMapper.updateById(purchaseEntity);
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 更新采购单为“已领取”状态
        this.updateBatchById(this
                .list(new LambdaQueryWrapper<PurchaseEntity>().in(PurchaseEntity::getId, ids))
                .stream()
                // 过滤出新建和已分配的
                .filter(purchaseEntity -> purchaseEntity.getStatus() <= WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                // 更新为已领取状态
                .peek(purchaseEntity -> purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode()))
                .collect(Collectors.toList()));

        // 更新采购需求为“采购中”状态
        purchaseDetailService.updateBatchById(purchaseDetailService
                .list(new LambdaQueryWrapper<PurchaseDetailEntity>().in(PurchaseDetailEntity::getPurchaseId, ids))
                .stream()
                .peek(purchaseDetailEntity -> purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode()))
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo vo) {
        // 采购需求完成列表
        final List<PurchaseItemDoneVo> purchaseItemDoneVoList = vo.getItems();

        // 采购需求Map数据
        final Map<Long, PurchaseDetailEntity> purchaseDetailEntityMap = purchaseDetailService
                .listByIds(purchaseItemDoneVoList
                        .stream()
                        .map(PurchaseItemDoneVo::getItemId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(PurchaseDetailEntity::getId, Function.identity(), (k, v) -> v));

        // 遍历采购需求完成列表
        boolean flag = true;
        double sum = 0D;
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();
        for (PurchaseItemDoneVo purchaseItemDoneVo : purchaseItemDoneVoList) {
            final PurchaseDetailEntity purchaseDetailEntity = purchaseDetailEntityMap.getOrDefault(purchaseItemDoneVo.getItemId(), new PurchaseDetailEntity());
            if (purchaseItemDoneVo.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
            }
            else {
                // 采购成功的更新库存
                final Integer skuNum = purchaseDetailEntity.getSkuNum();
                double price = wareSkuService.addStock(purchaseDetailEntity.getWareId(), purchaseDetailEntity.getSkuId(), skuNum);
                // 采购金额 = 商品价格 * 采购数量
                final double skuPrice = price * skuNum;
                purchaseDetailEntity.setSkuPrice(BigDecimal.valueOf(skuPrice));
                // 总金额 = ∑采购金额
                sum += skuPrice;
            }
            purchaseDetailEntity.setStatus(purchaseItemDoneVo.getStatus());
            purchaseDetailEntityList.add(purchaseDetailEntity);
        }
        // 更新采购需求
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        // 更新采购单
        final PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(vo.getId());
        purchaseEntity.setAmount(BigDecimal.valueOf(sum));
        purchaseEntity.setStatus(flag ?
                                 WareConstant.PurchaseStatusEnum.FINISH.getCode() :
                                 WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);
    }

}
