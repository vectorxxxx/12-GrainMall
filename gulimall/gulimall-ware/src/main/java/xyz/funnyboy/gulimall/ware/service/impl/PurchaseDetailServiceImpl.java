package xyz.funnyboy.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.ware.dao.PurchaseDetailDao;
import xyz.funnyboy.gulimall.ware.entity.PurchaseDetailEntity;
import xyz.funnyboy.gulimall.ware.service.PurchaseDetailService;

import java.util.Map;

@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        final String wareId = (String) params.get("wareId");
        final String status = (String) params.get("status");
        final String key = (String) params.get("key");
        final LambdaQueryWrapper<PurchaseDetailEntity> queryWrapper = new LambdaQueryWrapper<PurchaseDetailEntity>()
                .eq(!StringUtils.isEmpty(wareId), PurchaseDetailEntity::getWareId, wareId)
                .eq(!StringUtils.isEmpty(status), PurchaseDetailEntity::getStatus, status)
                .and(!StringUtils.isEmpty(key), w -> w
                        .eq(PurchaseDetailEntity::getPurchaseId, key)
                        .or()
                        .eq(PurchaseDetailEntity::getSkuId, key));
        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

}
