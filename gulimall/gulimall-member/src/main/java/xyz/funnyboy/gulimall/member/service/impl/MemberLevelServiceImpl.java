package xyz.funnyboy.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.member.dao.MemberLevelDao;
import xyz.funnyboy.gulimall.member.entity.MemberLevelEntity;
import xyz.funnyboy.gulimall.member.service.MemberLevelService;

import java.util.Map;

@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(new Query<MemberLevelEntity>().getPage(params), new QueryWrapper<MemberLevelEntity>());

        return new PageUtils(page);
    }

    @Override
    public Long getDefaultLevel() {
        final MemberLevelEntity memberLevelEntity = baseMapper.selectOne(new LambdaQueryWrapper<MemberLevelEntity>().eq(MemberLevelEntity::getDefaultStatus, "1"));
        return memberLevelEntity.getId();
    }

}
