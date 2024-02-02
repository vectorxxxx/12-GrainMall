package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.to.SkuReductionTO;
import xyz.funnyboy.common.to.SpuBoundTO;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.product.dao.SpuInfoDao;
import xyz.funnyboy.gulimall.product.entity.SkuImagesEntity;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;
import xyz.funnyboy.gulimall.product.entity.SkuSaleAttrValueEntity;
import xyz.funnyboy.gulimall.product.entity.SpuInfoEntity;
import xyz.funnyboy.gulimall.product.feign.CouponFeignService;
import xyz.funnyboy.gulimall.product.service.*;
import xyz.funnyboy.gulimall.product.vo.Images;
import xyz.funnyboy.gulimall.product.vo.Skus;
import xyz.funnyboy.gulimall.product.vo.SpuSaveVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService
{
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), new QueryWrapper<SpuInfoEntity>());

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        // 1、保存spu基本信息`pms_spu_info`
        final SpuInfoEntity spuInfoEntity = this.saveBasicInfo(spuSaveVo);
        final Long spuId = spuInfoEntity.getId();

        // 2、保存spu的描述图片`pms_spu_info_desc`
        spuInfoDescService.saveDescript(spuId, spuSaveVo.getDecript());

        // 3、保存spu的图片集`pms_spu_images`
        spuImagesService.saveImages(spuId, spuSaveVo.getImages());

        // 4、保存spu的规格参数`pms_product_attr_value`
        productAttrValueService.saveBaseAttrs(spuId, spuSaveVo.getBaseAttrs());

        // 5、保存spu的积分信息`gulimall_sms`->`sms_spu_bounds`
        this.saveSpuBoundInfo(spuSaveVo, spuId);

        // 6、保存spu对应的所有sku信息
        spuSaveVo
                .getSkus()
                .forEach(item -> saveSkuInfo(spuSaveVo, spuId, item));

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // 获取参数
        final String brandId = (String) params.get("brandId");
        final String catelogId = (String) params.get("catelogId");
        final String status = (String) params.get("status");
        final String key = (String) params.get("key");

        // 查询条件
        final LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<SpuInfoEntity>()
                .eq(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId), SpuInfoEntity::getBrandId, brandId)
                .eq(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId), SpuInfoEntity::getCatalogId, catelogId)
                .eq(!StringUtils.isEmpty(status), SpuInfoEntity::getPublishStatus, status)
                .and(!StringUtils.isEmpty(key), wrapper -> wrapper
                        .eq(SpuInfoEntity::getId, key)
                        .or()
                        .like(SpuInfoEntity::getSpuName, key));

        // 分页查询
        final IPage<SpuInfoEntity> page = baseMapper.selectPage(new Query<SpuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 保存 SKU 信息
     *
     * @param spuSaveVo SPU 保存 VO
     * @param spuId     SPU ID
     * @param item      项目
     */
    private void saveSkuInfo(SpuSaveVo spuSaveVo, Long spuId, Skus item) {
        // 默认图片
        final String defaultImgUrl = item
                .getImages()
                .stream()
                .filter(img -> img.getDefaultImg() == 1)
                .findFirst()
                .map(Images::getImgUrl)
                .orElse("");

        // 6.1、sku的基本信息`pms_sku_info`
        final SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(item, skuInfoEntity);
        skuInfoEntity.setSpuId(spuId);
        skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
        skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
        skuInfoEntity.setSkuDefaultImg(defaultImgUrl);
        skuInfoEntity.setSaleCount(0L);
        skuInfoService.save(skuInfoEntity);
        final Long skuId = skuInfoEntity.getSkuId();

        // 6.2、sku的图片信息`pms_sku_images`
        final List<SkuImagesEntity> skuImagesEntityList = item
                .getImages()
                .stream()
                .map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    BeanUtils.copyProperties(img, skuImagesEntity);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgSort(0);
                    return skuImagesEntity;
                })
                .collect(Collectors.toList());
        skuImagesService.saveBatch(skuImagesEntityList);

        // 6.3、sku的销售属性信息`pms_sku_sale_attr_value`
        final List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = item
                .getAttr()
                .stream()
                .map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrSort(0);
                    return skuSaleAttrValueEntity;
                })
                .collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

        // 6.4、sku的优惠、满减等信息`gulimall_sms`
        SkuReductionTO skuReductionTO = new SkuReductionTO();
        BeanUtils.copyProperties(item, skuReductionTO);
        skuReductionTO.setSkuId(skuId);
        if (skuReductionTO.getFullCount() > 0 || (skuReductionTO
                .getFullPrice()
                .compareTo(BigDecimal.ZERO) > 0)) {
            R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
            if (r1.getCode() != 0) {
                log.error("远程保存sku优惠信息失败");
            }
        }
    }

    /**
     * 保存 SPU 积分信息
     *
     * @param spuSaveVo SPU 保存 VO
     * @param spuId     SPU ID
     */
    private void saveSpuBoundInfo(SpuSaveVo spuSaveVo, Long spuId) {
        final SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTO);
        spuBoundTO.setSpuId(spuId);
        final R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }
    }

    /**
     * 保存基本信息
     *
     * @param spuSaveVo SPU 保存 VO
     * @return {@link SpuInfoEntity}
     */
    private SpuInfoEntity saveBasicInfo(SpuSaveVo spuSaveVo) {
        final SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        baseMapper.insert(spuInfoEntity);
        return spuInfoEntity;
    }

}
