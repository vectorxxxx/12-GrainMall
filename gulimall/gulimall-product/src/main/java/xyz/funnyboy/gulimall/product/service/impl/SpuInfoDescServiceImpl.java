package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.SpuInfoDescDao;
import xyz.funnyboy.gulimall.product.entity.SpuInfoDescEntity;
import xyz.funnyboy.gulimall.product.service.SpuInfoDescService;

import java.util.List;
import java.util.Map;

@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(new Query<SpuInfoDescEntity>().getPage(params), new QueryWrapper<SpuInfoDescEntity>());

        return new PageUtils(page);
    }

    @Override
    public void saveDescript(Long spuId, List<String> descript) {
        final SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", descript));
        baseMapper.insert(spuInfoDescEntity);
    }

}
