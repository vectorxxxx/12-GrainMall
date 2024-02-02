package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.ProductAttrValueDao;
import xyz.funnyboy.gulimall.product.entity.ProductAttrValueEntity;
import xyz.funnyboy.gulimall.product.service.AttrService;
import xyz.funnyboy.gulimall.product.service.ProductAttrValueService;
import xyz.funnyboy.gulimall.product.vo.BaseAttrs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService
{

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(new Query<ProductAttrValueEntity>().getPage(params), new QueryWrapper<ProductAttrValueEntity>());

        return new PageUtils(page);
    }

    @Override
    public void saveBaseAttrs(Long spuId, List<BaseAttrs> baseAttrs) {
        final List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs
                .stream()
                .map(baseAttr -> {
                    final Long attrId = baseAttr.getAttrId();
                    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                    productAttrValueEntity.setSpuId(spuId);
                    productAttrValueEntity.setAttrId(attrId);
                    productAttrValueEntity.setAttrName(attrService
                            .getById(attrId)
                            .getAttrName());
                    productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                    productAttrValueEntity.setAttrSort(0);
                    productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                    return productAttrValueEntity;
                })
                .collect(Collectors.toList());
        this.saveBatch(productAttrValueEntityList);
    }

}
