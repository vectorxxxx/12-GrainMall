package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.SkuSaleAttrValueDao;
import xyz.funnyboy.gulimall.product.entity.SkuSaleAttrValueEntity;
import xyz.funnyboy.gulimall.product.service.SkuSaleAttrValueService;
import xyz.funnyboy.gulimall.product.vo.SkuItemSaleAttrVO;

import java.util.List;
import java.util.Map;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(new Query<SkuSaleAttrValueEntity>().getPage(params), new QueryWrapper<SkuSaleAttrValueEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(Long spuId) {
        return baseMapper.getSaleAttrsBySpuId(spuId);
    }

}
