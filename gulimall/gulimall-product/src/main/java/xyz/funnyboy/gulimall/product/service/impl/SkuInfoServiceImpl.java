package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.SkuInfoDao;
import xyz.funnyboy.gulimall.product.entity.SkuImagesEntity;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;
import xyz.funnyboy.gulimall.product.entity.SpuInfoDescEntity;
import xyz.funnyboy.gulimall.product.service.*;
import xyz.funnyboy.gulimall.product.vo.SkuItemSaleAttrVO;
import xyz.funnyboy.gulimall.product.vo.SkuItemVO;
import xyz.funnyboy.gulimall.product.vo.SpuItemAttrGroupVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService
{
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

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

    /**
     * 项目
     *
     * @param skuId SKU ID
     * @return {@link SkuItemVO}
     */
    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO skuItemVO = new SkuItemVO();
        // 1、sku基本信息 pms_sku_info
        final SkuInfoEntity info = getById(skuId);
        final Long spuId = info.getSpuId();
        final Long catalogId = info.getCatalogId();
        skuItemVO.setInfo(info);

        // 2、sku图片信息 pms_sku_images
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVO.setImages(images);

        // 3、获取 spu 的销售属性组合 pms_sku_info + pms_sku_sale_attr_value
        List<SkuItemSaleAttrVO> saleAttrVOList = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVO.setSaleAttr(saleAttrVOList);

        // 4、获取 spu 的介绍 pms_spu_info_desc
        final SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVO.setDesc(spuInfoDescEntity);

        // 5、获取 spu 的规格参数信息
        List<SpuItemAttrGroupVO> attrGroupVOList = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        skuItemVO.setGroupAttrs(attrGroupVOList);
        return skuItemVO;
    }
}
