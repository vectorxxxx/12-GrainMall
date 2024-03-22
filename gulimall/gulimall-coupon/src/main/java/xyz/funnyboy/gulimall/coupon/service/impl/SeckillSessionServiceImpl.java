package xyz.funnyboy.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.funnyboy.common.utils.DateUtils;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.Query;
import xyz.funnyboy.gulimall.coupon.dao.SeckillSessionDao;
import xyz.funnyboy.gulimall.coupon.entity.SeckillSessionEntity;
import xyz.funnyboy.gulimall.coupon.entity.SeckillSkuRelationEntity;
import xyz.funnyboy.gulimall.coupon.service.SeckillSessionService;
import xyz.funnyboy.gulimall.coupon.service.SeckillSkuRelationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService
{

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(new Query<SeckillSessionEntity>().getPage(params), new QueryWrapper<SeckillSessionEntity>());

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {
        // 计算最近三天起止时间
        String startTime = DateUtils.currentStartTime();// 当天00:00:00
        String endTime = DateUtils.getTimeByOfferset(2);// 后天23:59:59

        // 查询起止时间内的秒杀场次
        List<SeckillSessionEntity> sessionEntityList = this.list(new LambdaQueryWrapper<SeckillSessionEntity>().between(SeckillSessionEntity::getStartTime, startTime, endTime));
        if (CollectionUtils.isEmpty(sessionEntityList)) {
            return null;
        }

        // 组合秒杀关联的商品信息
        final List<Long> sessionIds = sessionEntityList
                .stream()
                .map(SeckillSessionEntity::getId)
                .collect(Collectors.toList());
        final Map<Long, List<SeckillSkuRelationEntity>> skuMap = seckillSkuRelationService
                .list(new LambdaQueryWrapper<SeckillSkuRelationEntity>().in(SeckillSkuRelationEntity::getPromotionSessionId, sessionIds))
                .stream()
                .collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
        sessionEntityList.forEach(sessionEntity -> sessionEntity.setRelationSkus(skuMap.getOrDefault(sessionEntity.getId(), Collections.emptyList())));

        return sessionEntityList;
    }

}
