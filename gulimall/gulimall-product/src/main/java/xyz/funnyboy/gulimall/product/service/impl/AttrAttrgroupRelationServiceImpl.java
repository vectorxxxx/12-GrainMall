package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.AttrAttrgroupRelationDao;
import xyz.funnyboy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import xyz.funnyboy.gulimall.product.service.AttrAttrgroupRelationService;
import xyz.funnyboy.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(new Query<AttrAttrgroupRelationEntity>().getPage(params), new QueryWrapper<AttrAttrgroupRelationEntity>());

        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVo> attrGroupRelations) {
        final List<AttrAttrgroupRelationEntity> relationEntityList = attrGroupRelations
                .stream()
                .map(attrGroupRelationVo -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(attrGroupRelationVo, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                })
                .collect(Collectors.toList());
        this.saveBatch(relationEntityList);
    }

}
