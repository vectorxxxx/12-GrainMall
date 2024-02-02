package xyz.funnyboy.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.ware.dao.WareInfoDao;
import xyz.funnyboy.gulimall.ware.entity.WareInfoEntity;
import xyz.funnyboy.gulimall.ware.service.WareInfoService;

import java.util.Map;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService
{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), new QueryWrapper<WareInfoEntity>());

        return new PageUtils(page);
    }

    /**
     * 按条件查询页面
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // 获取参数
        final String key = (String) params.get("key");

        // 查询条件
        final LambdaQueryWrapper<WareInfoEntity> queryWrapper = new LambdaQueryWrapper<WareInfoEntity>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper
                    .eq(WareInfoEntity::getId, key)
                    .or()
                    .like(WareInfoEntity::getName, key)
                    .or()
                    .like(WareInfoEntity::getAddress, key)
                    .or()
                    .like(WareInfoEntity::getAreacode, key);
        }

        // 分页查询
        final IPage<WareInfoEntity> page = baseMapper.selectPage(new Query<WareInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

}
