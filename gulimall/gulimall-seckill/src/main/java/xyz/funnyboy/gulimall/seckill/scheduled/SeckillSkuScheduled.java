package xyz.funnyboy.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.constant.SeckillConstant;
import xyz.funnyboy.gulimall.seckill.service.SeckillService;

import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-21 10:23:25
 */
@Slf4j
@Service
public class SeckillSkuScheduled
{
    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "*/10 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        log.info("上架秒杀商品信息...");

        // 分布式锁（幂等性）
        final RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        }
        catch (Exception e) {
            log.error("上架秒杀商品信息失败：" + e.getMessage(), e);
        }
        finally {
            lock.unlock();
        }
    }
}
