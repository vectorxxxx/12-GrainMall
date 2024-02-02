package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.SpuImagesDao;
import xyz.funnyboy.gulimall.product.entity.SpuImagesEntity;
import xyz.funnyboy.gulimall.product.service.SpuImagesService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(new Query<SpuImagesEntity>().getPage(params), new QueryWrapper<SpuImagesEntity>());

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long spuId, List<String> images) {
        final List<SpuImagesEntity> spuImagesEntityList = images
                .stream()
                .map(img -> {
                    final SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                    spuImagesEntity.setSpuId(spuId);
                    spuImagesEntity.setImgName(img.substring(img.lastIndexOf("/") + 1));
                    spuImagesEntity.setImgUrl(img);
                    spuImagesEntity.setImgSort(0);
                    return spuImagesEntity;
                })
                .collect(Collectors.toList());
        this.saveBatch(spuImagesEntityList);
    }

}
