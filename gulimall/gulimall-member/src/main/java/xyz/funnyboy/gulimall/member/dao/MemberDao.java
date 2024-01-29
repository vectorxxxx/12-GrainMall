package xyz.funnyboy.gulimall.member.dao;

import xyz.funnyboy.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:11:19
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
