package xyz.funnyboyx.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Es 配置类
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 22:25:34
 */
@Configuration
public class GulimallElasticSearchConfig
{
    private static final String HOSTNAME = "192.168.56.10";
    private static final int PORT = 9200;
    private static final String SCHEME = "http";

    /*
     * The RequestOptions class holds parts of the request that should be shared between many requests in the same application.
     * RequestOptions类保存请求中应该在同一应用程序中的多个请求之间共享的部分。
     * You can make a singleton instance and share it between all requests:
     * 您可以制作一个单例实例，并在所有请求之间共享：
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // Add any headers needed by all requests.
        // 添加所有请求所需的任何标头。
        // builder.addHeader("Authorization", "Bearer " + TOKEN);

        // Customize the response consumer.
        // 自定义响应消费者。
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));

        COMMON_OPTIONS = builder.build();
    }

    /**
     * 返回一个 Elasticsearch-Rest-Client
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public RestHighLevelClient esRestClient() {
        // hostname – the hostname (IP or DNS name)
        // hostname – 主机名（IP 或 DNS 名称）
        // port – the port number. -1 indicates the scheme default port.
        // port – 端口号。-1 表示方案默认端口。
        // scheme – the name of the scheme. null indicates the default scheme
        // scheme – 方案的名称。null 表示默认方案
        final HttpHost httpHost = new HttpHost(HOSTNAME, PORT, SCHEME);

        // Returns a new RestClientBuilder to help with RestClient creation. Creates a new builder instance and sets the nodes that the client will send requests to.
        // 返回一个新的 RestClientBuilder 以帮助创建 RestClient。创建新的生成器实例并设置客户端将向其发送请求的节点。
        // You can use this if you do not have metadata up front about the nodes. If you do, prefer builder(Node...).
        // 如果预先没有有关节点的元数据，则可以使用它。如果这样做，请选择 builder（Node...）。
        final RestClientBuilder builder = RestClient.builder(httpHost);

        // Creates a RestHighLevelClient given the high level RestClientBuilder that allows to build the RestClient to be used to perform requests.
        // 在给定高级 RestClientBuilder 的情况下创建一个 RestHighLevelClient，该 RestClientBuilder 允许生成用于执行请求的 RestClient。
        return new RestHighLevelClient(builder);
    }
}
