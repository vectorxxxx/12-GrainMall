package xyz.funnyboyx.gulimall.search;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.funnyboyx.gulimall.search.config.GulimallElasticSearchConfig;
import xyz.funnyboyx.gulimall.search.entity.User;

import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-02-05 22:33:30
 */
@SpringBootTest
public class GulimallSearch1IndexAPITests
{
    @Autowired
    private RestHighLevelClient client;

    /**
     * 测试 Index API
     *
     * @see {@linktourl
     * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-document-index.html">https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-document-index.html</a>}
     */
    @Test
    public void testIndex() throws IOException {
        // 构造 User
        User user = new User()
                .setName("Nathan Littel")
                .setAge(18)
                .setGender("男");
        final String userJsonStr = JSON.toJSONString(user);

        // Constructs a new index request against the specific index. The type(String) source(byte[], XContentType) must be set.
        // 针对特定索引构造新的索引请求。必须设置 type（String） source（byte[]， XContentType）。
        IndexRequest indexRequest = new IndexRequest("users");

        // Sets the id of the indexed document. If not set, will be automatically generated.
        // 设置索引文档的 ID。如果未设置，将自动生成。
        indexRequest.id("1");

        // Sets the document source to index. Note, its preferable to either set it using source(XContentBuilder) or using the source(byte[], XContentType).
        // 将文档源设置到索引中。请注意，最好使用 source（XContentBuilder） 或使用 source（byte[]， XContentType） 来设置它。
        indexRequest.source(userJsonStr, XContentType.JSON);

        // Index a document using the Index API. See Index API on elastic.co
        // 使用 Index API 索引一个文档。请参阅 elastic.co 上的 Index API
        // Params:
        //      indexRequest – the request
        //      options – the request options (e.g. headers), use RequestOptions.DEFAULT if nothing needs to be customized
        //      选项-请求选项（例如 headers），如果不需要自定义任何内容则使用 RequestOptions.DEFAULT
        // Returns: the response
        final IndexResponse response = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(response);
        // IndexResponse[index=users,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
    }
}
