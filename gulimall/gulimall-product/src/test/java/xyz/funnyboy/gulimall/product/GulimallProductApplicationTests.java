package xyz.funnyboy.gulimall.product;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.funnyboy.gulimall.product.entity.BrandEntity;
import xyz.funnyboy.gulimall.product.service.BrandService;

@SpringBootTest
class GulimallProductApplicationTests
{
    @Autowired
    private BrandService brandService;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void testRedissonClient() {
        System.out.println(redissonClient); // org.redisson.Redisson@68c4db77
    }

    @Test
    void testSave() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
    }

    @Test
    void testUpdate() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("修改");
        brandService.updateById(brandEntity);
    }
}
