package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.SkuInfoDao;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;
import xyz.funnyboy.gulimall.product.service.SkuInfoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), new QueryWrapper<SkuInfoEntity>());

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // 获取参数
        final String brandId = (String) params.get("brandId");
        final String catelogId = (String) params.get("catelogId");
        final String min = (String) params.get("min");
        final String max = (String) params.get("max");
        final String key = (String) params.get("key");

        // 查询条件
        final LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<SkuInfoEntity>()
                .eq(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId), SkuInfoEntity::getBrandId, brandId)
                .eq(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId), SkuInfoEntity::getCatalogId, catelogId)
                .ge(!StringUtils.isEmpty(min), SkuInfoEntity::getPrice, min)
                .lt(!StringUtils.isEmpty(max) && BigDecimal
                        .valueOf(Double.parseDouble(max))
                        .compareTo(BigDecimal.ZERO) > 0, SkuInfoEntity::getPrice, max)
                .and(!StringUtils.isEmpty(key), wrapper -> wrapper
                        .eq(SkuInfoEntity::getSkuId, key)
                        .or()
                        .like(SkuInfoEntity::getSkuName, key));

        // 分页查询
        final IPage<SkuInfoEntity> page = baseMapper.selectPage(new Query<SkuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        return baseMapper.selectList(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
    }
}
