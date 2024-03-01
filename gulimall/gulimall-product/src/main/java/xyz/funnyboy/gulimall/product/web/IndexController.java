package xyz.funnyboy.gulimall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.gulimall.product.entity.CategoryEntity;
import xyz.funnyboy.gulimall.product.service.CategoryService;
import xyz.funnyboy.gulimall.product.vo.Catalog2VO;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-22 22:43:56
 */
@Slf4j
@Controller
public class IndexController
{
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        final List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        // 优化前，性能压测用
        // return categoryService.getCatalogJsonBeforeOptimization();
        // return categoryService.getCatalogJson2();
        return categoryService.getCatalogJson();
    }

    /**
     * 性能压测-简单服务
     *
     * @return {@link String}
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把锁,只要锁的名字一样,就是同一把锁，"my-lock"是锁名，也是Redis的哈希模型的对外key
        final RLock lock = redissonClient.getLock("my-lock");

        // 2、加锁
        // 阻塞式等待,默认加的锁等待时间为30s。每到20s(经过三分之一看门狗时间后)就会自动续借成30s
        //      1）锁的自动续期,如果在业务执行期间业务没有执行完成,redisson会为该锁自动续期
        //      2）加锁的业务只要运行完成,就不会自动续期,即使不手动解锁,锁在默认的30s后会自动删除
        // lock.lock();
        // lock()方法的两大特点：
        //      1）会有一个看门狗机制，在我们业务运行期间，将我们的锁自动续期
        //      2）为了防止死锁，加的锁设置成30秒的过期时间，不让看门狗自动续期，如果业务宕机，没有手动调用解锁代码，30s后redis也会对他自动解锁。
        // 设置的自动解锁时间一定要稳稳地大于业务时间
        lock.lock(30, TimeUnit.SECONDS);

        try {
            log.info("加锁成功,执行业务......{}", Thread
                    .currentThread()
                    .getId());
            Thread.sleep(30000);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            // 3、解锁,假设代码没有运行,redisson不会出现死锁
            log.info("锁释放......{}", Thread
                    .currentThread()
                    .getId());
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 可重入读锁
     *
     * @return {@link String}
     */
    @GetMapping("/read")
    @ResponseBody
    public String read() {
        final RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("ReadWrite-Lock");
        final RLock rLock = readWriteLock.readLock();
        String result = "";
        try {
            rLock.lock();
            log.info("读锁加锁成功,执行业务......{}", Thread
                    .currentThread()
                    .getId());
            Thread.sleep(5000);
            result = redisTemplate
                    .opsForValue()
                    .get("lock-value");
        }
        catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            rLock.unlock();
            log.info("读锁释放......{}", Thread
                    .currentThread()
                    .getId());
        }
        return "读取完成:" + result;
    }

    /**
     * 可重入写锁
     *
     * @return {@link String}
     */
    @GetMapping("/write")
    @ResponseBody
    public String write() {
        final RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("ReadWrite-Lock");
        final RLock rLock = readWriteLock.writeLock();
        String result = UUID
                .randomUUID()
                .toString();
        try {
            rLock.lock();
            log.info("写锁加锁成功,执行业务......{}", Thread
                    .currentThread()
                    .getId());
            Thread.sleep(10000);
            redisTemplate
                    .opsForValue()
                    .set("lock-value", result);
        }
        catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            rLock.unlock();
            log.info("写锁释放......{}", Thread
                    .currentThread()
                    .getId());
        }
        return "写入完成:" + result;
    }

    /**
     * 信号量增加数量
     *
     * @return {@link String}
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() {
        final RSemaphore park = redissonClient.getSemaphore("park");
        try {
            park.acquire(1);
        }
        catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return "停车，占一个车位";
    }

    /**
     * 信号量减少数量
     *
     * @return {@link String}
     */
    @GetMapping("/go")
    @ResponseBody
    public String go() {
        final RSemaphore park = redissonClient.getSemaphore("park");
        park.release(1);
        return "开走，放出一个车位";
    }

    /**
     * 设置闩锁
     *
     * @return {@link String}
     */
    @GetMapping("/setLatch")
    @ResponseBody
    public String setLatch() {
        final RCountDownLatch latch = redissonClient.getCountDownLatch("CountDownLatch");
        try {
            latch.trySetCount(5);
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "设置门栓";
    }

    /**
     * 关闭闩锁
     *
     * @return {@link String}
     */
    @GetMapping("offLatch")
    @ResponseBody
    public String offLatch() {
        final RCountDownLatch latch = redissonClient.getCountDownLatch("CountDownLatch");
        latch.countDown();
        return "放开门栓";
    }
}
