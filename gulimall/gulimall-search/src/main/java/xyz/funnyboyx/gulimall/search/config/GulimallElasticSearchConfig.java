package xyz.funnyboyx.gulimall.search.config;

import org.apache.http.HttpHost;
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

    /**
     * 返回一个 Elasticsearch-Rest-Client
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public RestHighLevelClient esRestClient() {
        // hostname – the hostname (IP or DNS name)
        // port – the port number. -1 indicates the scheme default port.
        // scheme – the name of the scheme. null indicates the default scheme
        final HttpHost httpHost = new HttpHost(HOSTNAME, PORT, SCHEME);

        // Returns a new RestClientBuilder to help with RestClient creation. Creates a new builder instance and sets the nodes that the client will send requests to.
        // You can use this if you do not have metadata up front about the nodes. If you do, prefer builder(Node...).
        final RestClientBuilder builder = RestClient.builder(httpHost);

        // Creates a RestHighLevelClient given the high level RestClientBuilder that allows to build the RestClient to be used to perform requests.
        return new RestHighLevelClient(builder);
    }
}
