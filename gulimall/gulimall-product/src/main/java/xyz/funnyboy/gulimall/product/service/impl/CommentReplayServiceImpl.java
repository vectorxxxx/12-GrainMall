package xyz.funnyboy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.product.dao.CommentReplayDao;
import xyz.funnyboy.gulimall.product.entity.CommentReplayEntity;
import xyz.funnyboy.gulimall.product.service.CommentReplayService;

import java.util.Map;

@Service("commentReplayService")
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayDao, CommentReplayEntity> implements CommentReplayService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentReplayEntity> page = this.page(new Query<CommentReplayEntity>().getPage(params), new QueryWrapper<CommentReplayEntity>());

        return new PageUtils(page);
    }

}
