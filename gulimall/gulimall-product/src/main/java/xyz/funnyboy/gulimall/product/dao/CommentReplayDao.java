package xyz.funnyboy.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.funnyboy.gulimall.product.entity.CommentReplayEntity;

/**
 * 商品评价回复关系
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity>
{

}
