package xyz.funnyboy.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.coupon.dao.SeckillSkuRelationDao;
import xyz.funnyboy.gulimall.coupon.entity.SeckillSkuRelationEntity;
import xyz.funnyboy.gulimall.coupon.service.SeckillSkuRelationService;

import java.util.Map;

@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String promotionSessionId = (String) params.get("promotionSessionId");

        final LambdaQueryWrapper<SeckillSkuRelationEntity> queryWrapper = new LambdaQueryWrapper<SeckillSkuRelationEntity>().eq(StringUtils.isEmpty(promotionSessionId),
                SeckillSkuRelationEntity::getPromotionSessionId, promotionSessionId);
        IPage<SeckillSkuRelationEntity> page = this.page(new Query<SeckillSkuRelationEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

}
