package xyz.funnyboyx.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.to.es.SkuEsModel;
import xyz.funnyboyx.gulimall.search.config.GulimallElasticSearchConfig;
import xyz.funnyboyx.gulimall.search.constant.EsConstant;
import xyz.funnyboyx.gulimall.search.service.ProductSaveService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-21 10:43:00
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService
{
    @Autowired
    private RestHighLevelClient client;

    /**
     * 将数据保存到Es
     *
     * @param skuEsModels SKU ES 型号
     * @return boolean
     * @throws Exception 例外
     */
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws Exception {
        // 1、给ES建立一个索引 product
        // A BulkRequest can be used to execute multiple index, update and/or delete operations using a single request.
        // BulkRequest可用于使用单个请求执行多个索引、更新和/或删除操作。
        final BulkRequest bulkRequest = new BulkRequest();

        // 2、构造批量请求
        // It requires at least one operation to be added to the Bulk request:
        // 它需要至少一个操作被添加到批量请求中:
        for (SkuEsModel skuEsModel : skuEsModels) {
            final IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest
                    .id(skuEsModel
                            .getSkuId()
                            .toString())
                    .source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        // 3、执行批量请求
        // When executing a BulkRequest in the following manner, the client waits for the BulkResponse to be returned before continuing with code execution:
        // 以以下方式执行BulkRequest时，客户端将等待BulkResponse返回，然后再继续执行代码:
        final BulkResponse response = client.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // 4、判断批量请求是否成功
        // Synchronous calls may throw an IOException in case of either failing to parse the REST response in the high-level REST client, the request times out or similar cases
        // where there is no response coming back from the server.
        // 在高级REST客户端解析REST响应失败、请求超时或类似的情况下没有从服务器返回响应时，同步调用可能会抛出IOException。
        // In cases where the server returns a 4xx or 5xx error code, the high-level client tries to parse the response body error details instead and then throws a generic
        // ElasticsearchException and adds the original ResponseException as a suppressed exception to it.
        // 在服务器返回4xx或5xx错误代码的情况下，高级客户端会尝试解析响应体错误详细信息，然后抛出一个通用的ElasticsearchException，并将原始ResponseException作为隐藏的异常添加到该异常中。
        final boolean hasFailures = response.hasFailures();
        if (hasFailures) {
            final List<String> collect = Arrays
                    .stream(response.getItems())
                    .map(BulkItemResponse::getId)
                    .collect(Collectors.toList());
            log.error("商品上架失败：{}", collect);
        }
        return hasFailures;
    }
}
