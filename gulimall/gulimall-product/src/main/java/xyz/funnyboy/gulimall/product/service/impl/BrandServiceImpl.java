package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.BrandDao;
import xyz.funnyboy.gulimall.product.entity.BrandEntity;
import xyz.funnyboy.gulimall.product.service.BrandService;
import xyz.funnyboy.gulimall.product.service.CategoryBrandRelationService;

import java.util.Locale;
import java.util.Map;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService
{

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 分页参数
        final IPage<BrandEntity> pageParam = new Query<BrandEntity>().getPage(params);

        // 查询条件
        final String key = (String) params.get("key");
        final LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper
                    .eq(BrandEntity::getBrandId, key)
                    .or()
                    .eq(BrandEntity::getFirstLetter, key.toUpperCase(Locale.ROOT))
                    .or()
                    .eq(BrandEntity::getFirstLetter, key.toLowerCase(Locale.ROOT))
                    .or()
                    .like(BrandEntity::getName, key)
                    .or()
                    .like(BrandEntity::getDescript, key);
        }

        // 分页查询
        IPage<BrandEntity> page = this.page(pageParam, queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 按 ID 详细信息更新
     *
     * @param brand 品牌
     */
    @Override
    public void updateByIdDetail(BrandEntity brand) {
        this.updateById(brand);
        // 要对品牌（分类）名字进行修改时，品牌分类关系表之中的名字也要进行修改
        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            // TODO 更新其他关联
        }
    }

}
