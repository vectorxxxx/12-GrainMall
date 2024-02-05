package xyz.funnyboyx.gulimall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 22:33:30
 */
@SpringBootTest
public class GulimallSearchTests
{
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void contextLoads() {
        System.out.println(restHighLevelClient);
        // org.elasticsearch.client.RestHighLevelClient@3fc7c734
    }
}
