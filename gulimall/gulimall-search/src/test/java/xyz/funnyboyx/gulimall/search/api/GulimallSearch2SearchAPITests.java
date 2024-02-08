package xyz.funnyboyx.gulimall.search.api;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.funnyboyx.gulimall.search.entity.Account;

import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 22:33:30
 */
@SpringBootTest
public class GulimallSearch2SearchAPITests
{
    @Autowired
    private RestHighLevelClient client;

    /**
     * 测试 Search API
     *
     * @see {@linktourl
     * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-search.html">https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-search.html</a>}
     */
    @Test
    public void testSearch() throws IOException {
        // The SearchRequest is used for any operation that has to do with searching documents, aggregations, suggestions and also offers ways of requesting highlighting on the
        // resulting documents.
        // SearchRequest用于任何与搜索文档、聚合、建议有关的操作，并且还提供了在结果文档上请求高亮显示的方法。
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");

        // Most options controlling the search behavior can be set on the SearchSourceBuilder, which contains more or less the equivalent of the options in the search request
        // body of the Rest API.
        // 大多数控制搜索行为的选项都可以在SearchSourceBuilder上设置，它或多或少地包含了Rest API的搜索请求主体中的等价选项。
        // Here are a few examples of some common options:
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // In its most basic form, we can add a query to the request:
        // 其最基本的形式，我们可以在请求中添加一个查询：
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        // {"took":4,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":4,"relation":"eq"},"max_score":5.4032025,
        // "hits":[{"_index":"bank","_type":"account","_id":"970","_score":5.4032025,"_source":{"account_number":970,"balance":19648,"firstname":"Forbes","lastname":"Wallace",
        // "age":28,"gender":"M","address":"990 Mill Road","employer":"Pheast","email":"forbeswallace@pheast.com","city":"Lopezo","state":"AK"}},{"_index":"bank",
        // "_type":"account","_id":"136","_score":5.4032025,"_source":{"account_number":136,"balance":45801,"firstname":"Winnie","lastname":"Holland","age":38,"gender":"M",
        // "address":"198 Mill Lane","employer":"Neteria","email":"winnieholland@neteria.com","city":"Urie","state":"IL"}},{"_index":"bank","_type":"account","_id":"345",
        // "_score":5.4032025,"_source":{"account_number":345,"balance":9812,"firstname":"Parker","lastname":"Hines","age":38,"gender":"M","address":"715 Mill Avenue",
        // "employer":"Baluba","email":"parkerhines@baluba.com","city":"Blackgum","state":"KY"}},{"_index":"bank","_type":"account","_id":"472","_score":5.4032025,
        // "_source":{"account_number":472,"balance":25571,"firstname":"Lee","lastname":"Long","age":32,"gender":"F","address":"288 Mill Street","employer":"Comverges",
        // "email":"leelong@comverges.com","city":"Movico","state":"MT"}}]}}
    }

    /**
     * 测试 Search API
     *
     * @see {@linktourl
     * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-search.html">https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-search.html</a>}
     */
    @Test
    public void testAggregation() throws IOException {
        // 1、创建检索请求
        SearchRequest searchRequest = new SearchRequest("bank");

        // 1.1、查询条件
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        searchRequest.source(sourceBuilder);

        // 1.2、聚合条件：
        // **搜索address中包含mill的所有人的年龄分布以及平均年龄，平均薪资**
        // Aggregations can be added to the search by first creating the appropriate AggregationBuilder and then setting it on the SearchSourceBuilder.
        // 可以先建立适当的AggregationBuilder，然后在SearchSourceBuilder上进行设定，将聚合加入搜索。
        final TermsAggregationBuilder agg1 = AggregationBuilders
                .terms("agg1")
                .field("age")
                .size(10);
        sourceBuilder.aggregation(agg1);
        final AvgAggregationBuilder agg2 = AggregationBuilders
                .avg("agg2")
                .field("balance");
        sourceBuilder.aggregation(agg2);

        // 2、执行检索请求
        final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        // {"took":4,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":4,"relation":"eq"},"max_score":5.4032025,
        // "hits":[{"_index":"bank","_type":"account","_id":"970","_score":5.4032025,"_source":{"account_number":970,"balance":19648,"firstname":"Forbes","lastname":"Wallace",
        // "age":28,"gender":"M","address":"990 Mill Road","employer":"Pheast","email":"forbeswallace@pheast.com","city":"Lopezo","state":"AK"}},{"_index":"bank",
        // "_type":"account","_id":"136","_score":5.4032025,"_source":{"account_number":136,"balance":45801,"firstname":"Winnie","lastname":"Holland","age":38,"gender":"M",
        // "address":"198 Mill Lane","employer":"Neteria","email":"winnieholland@neteria.com","city":"Urie","state":"IL"}},{"_index":"bank","_type":"account","_id":"345",
        // "_score":5.4032025,"_source":{"account_number":345,"balance":9812,"firstname":"Parker","lastname":"Hines","age":38,"gender":"M","address":"715 Mill Avenue",
        // "employer":"Baluba","email":"parkerhines@baluba.com","city":"Blackgum","state":"KY"}},{"_index":"bank","_type":"account","_id":"472","_score":5.4032025,
        // "_source":{"account_number":472,"balance":25571,"firstname":"Lee","lastname":"Long","age":32,"gender":"F","address":"288 Mill Street","employer":"Comverges",
        // "email":"leelong@comverges.com","city":"Movico","state":"MT"}}]},"aggregations":{"avg#agg2":{"value":25208.0},"lterms#agg1":{"doc_count_error_upper_bound":0,
        // "sum_other_doc_count":0,"buckets":[{"key":38,"doc_count":2},{"key":28,"doc_count":1},{"key":32,"doc_count":1}]}}}

        // 3、解析检索响应
        // 3.1、获取 java bean
        // Nested inside the SearchHits are the individual search results that can be iterated over:
        // 嵌套在SearchHits中的是可以迭代的单个搜索结果:
        for (SearchHit searchHit : response.getHits()) {
            final String source = searchHit.getSourceAsString();
            final Account account = JSON.parseObject(source, Account.class);
            System.out.println(account);
        }
        // Account(accountNumber=970, balance=19648, firstname=Forbes, lastname=Wallace, age=28, gender=M, address=990 Mill Road, employer=Pheast, email=forbeswallace@pheast
        // .com, city=Lopezo, state=AK)
        // Account(accountNumber=136, balance=45801, firstname=Winnie, lastname=Holland, age=38, gender=M, address=198 Mill Lane, employer=Neteria, email=winnieholland@neteria
        // .com, city=Urie, state=IL)
        // Account(accountNumber=345, balance=9812, firstname=Parker, lastname=Hines, age=38, gender=M, address=715 Mill Avenue, employer=Baluba, email=parkerhines@baluba.com,
        // city=Blackgum, state=KY)
        // Account(accountNumber=472, balance=25571, firstname=Lee, lastname=Long, age=32, gender=F, address=288 Mill Street, employer=Comverges, email=leelong@comverges.com,
        // city=Movico, state=MT)

        // 3.2、获取检索到的分析信息
        // Retrieving Aggregations
        // Aggregations can be retrieved from the SearchResponse by first getting the root of the aggregation tree, the Aggregations object, and then getting the aggregation by
        // name.
        // 可以从 SearchResponse 中检索聚合，方法是首先获取聚合树的根目录（即 Aggregations 对象），然后按名称获取聚合。
        final Aggregations aggregations = response.getAggregations();
        final Terms agg22 = aggregations.get("agg1");
        agg22
                .getBuckets()
                .forEach(bucket -> System.out.println(bucket.getKeyAsString()));
    }
}
