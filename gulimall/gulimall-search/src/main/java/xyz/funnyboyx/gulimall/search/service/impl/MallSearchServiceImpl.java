package xyz.funnyboyx.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.common.to.es.SkuEsModel;
import xyz.funnyboy.common.vo.search.SearchParam;
import xyz.funnyboy.common.vo.search.SearchResult;
import xyz.funnyboyx.gulimall.search.config.GulimallElasticSearchConfig;
import xyz.funnyboyx.gulimall.search.constant.EsConstant;
import xyz.funnyboyx.gulimall.search.feign.ProductFeignService;
import xyz.funnyboyx.gulimall.search.service.MallSearchService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 商城搜索Service实现类
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-28 14:04:24
 */
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService
{
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 搜索
     *
     * @param param 参数
     * @return {@link SearchResult}
     */
    @Override
    public SearchResult search(SearchParam param) {
        // 准备检索请求
        org.elasticsearch.action.search.SearchRequest searchResult = buildSearchRequest(param);
        log.info("构建的DSL语句：{}", searchResult.source());

        SearchResult result = null;
        try {
            // 执行检索请求
            final SearchResponse response = restHighLevelClient.search(searchResult, GulimallElasticSearchConfig.COMMON_OPTIONS);

            // 封装响应数据
            result = buildSearchResult(response, param);
            log.info("响应结果：{}", JSON.toJSONString(result));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    private SearchRequest buildSearchRequest(SearchParam param) {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /**
         * 模糊匹配，过滤（按照属性、分类、品牌、价格区间、库存）
         */
        // 1、构建bool-query
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 1.1、bool-must
        final String keyword = param.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }

        // 1.2、bool-filter
        // 1.2.1、catalogId
        final Long catalog3Id = param.getCatalog3Id();
        if (catalog3Id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", catalog3Id));
        }

        // 1.2.2、brandId
        final List<Long> brandId = param.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        // 1.2.3、attrs
        final List<String> attrs = param.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            attrs.forEach(item -> {
                final List<String> s = Arrays.asList(item.split("_"));
                final String attrId = s.get(0);
                final String[] attrValues = s
                        .get(1)
                        .split(":");
                final BoolQueryBuilder boolQuery = QueryBuilders
                        .boolQuery()
                        .must(QueryBuilders.termQuery("attrs.attrId", attrId))
                        .must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None));
            });
        }

        // 1.2.4、hasStock
        final Integer hasStock = param.getHasStock();
        if (hasStock != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));
        }

        // 1.2.5、skuPrice
        final String skuPrice = param.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            final RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            final List<String> s = Arrays.asList(skuPrice.split("_"));
            if (s.size() == 2) {
                rangeQuery
                        .gte(s.get(0))
                        .lte(s.get(1));
            }
            else if (s.size() == 1) {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.lte(s.get(0));
                }
                else if (skuPrice.endsWith("_")) {
                    rangeQuery.gte(s.get(0));
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        /**
         * 排序，分页，高亮
         */
        // 排序
        final String sort = param.getSort();
        if (!StringUtils.isEmpty(sort)) {
            final String[] sortFields = sort.split("_");
            SortOrder sortOrder = SortOrder.fromString(sortFields[1]);
            searchSourceBuilder.sort(sortFields[0], sortOrder);
        }

        // 分页
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 高亮
        if (!StringUtils.isEmpty(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    .field("skuTitle")
                    .preTags("<b style='color:red'>")
                    .postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        // 1、按照品牌进行聚合
        final TermsAggregationBuilder brandAgg = AggregationBuilders
                .terms("brand_agg")
                .field("brandId")
                .size(50);
        // 1.1、品牌的子聚合-品牌名称聚合
        final TermsAggregationBuilder brandNameAgg = AggregationBuilders
                .terms("brand_name_agg")
                .field("brandName")
                .size(1);
        brandAgg.subAggregation(brandNameAgg);
        // 1.2、品牌的子聚合-品牌图片聚合
        final TermsAggregationBuilder brandImgAgg = AggregationBuilders
                .terms("brand_img_agg")
                .field("brandImg")
                .size(1);
        brandAgg.subAggregation(brandImgAgg);
        searchSourceBuilder.aggregation(brandAgg);

        // 2、按照分类信息进行聚合
        final TermsAggregationBuilder catalogAgg = AggregationBuilders
                .terms("catalog_agg")
                .field("catalogId")
                .size(20);
        // 2.1、分类的子聚合-分类名称聚合
        final TermsAggregationBuilder catalogNameAgg = AggregationBuilders
                .terms("catalog_name_agg")
                .field("catalogName.keyword")
                .size(1);
        catalogAgg.subAggregation(catalogNameAgg);
        searchSourceBuilder.aggregation(catalogAgg);

        // 3、按照属性信息进行聚合
        final NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.1、按照属性ID进行聚合
        final TermsAggregationBuilder attrIdAgg = AggregationBuilders
                .terms("attr_id_agg")
                .field("attrs.attrId")
                .size(10);
        // 3.1.1、在每个属性ID下，按照属性名进行聚合
        final TermsAggregationBuilder attrNameAgg = AggregationBuilders
                .terms("attr_name_agg")
                .field("attrs.attrName")
                .size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        // 3.1.2、在每个属性ID下，按照属性值进行聚合
        final TermsAggregationBuilder attrValueAgg = AggregationBuilders
                .terms("attr_value_agg")
                .field("attrs.attrValue")
                .size(50);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAgg);

        return new SearchRequest(new String[] {EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }

    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        final SearchResult result = new SearchResult();

        // 1、返回的所有查询到的商品
        List<SkuEsModel> skuEsModelList = new ArrayList<>();
        final SearchHits hits = response.getHits();
        final SearchHit[] hitArr = hits.getHits();
        if (hitArr != null && hitArr.length > 0) {
            skuEsModelList = Arrays
                    .stream(hitArr)
                    .map(hit -> {
                        final String sourceAsString = hit.getSourceAsString();
                        final SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);

                        // 判断是否按关键字检索，若是就显示高亮，否则不显示
                        if (!StringUtils.isEmpty(param.getKeyword())) {
                            final HighlightField skuTitle = hit
                                    .getHighlightFields()
                                    .get("skuTitle");
                            final String skuTitleValue = skuTitle.getFragments()[0].string();
                            skuEsModel.setSkuTitle(skuTitleValue);
                        }

                        return skuEsModel;
                    })
                    .collect(Collectors.toList());
        }
        result.setProducts(skuEsModelList);

        // 2、当前商品涉及到的所有属性信息
        // 获取属性信息的聚合
        final ParsedNested attrAgg = response
                .getAggregations()
                .get("attr_agg");
        final ParsedLongTerms attrIdAgg = attrAgg
                .getAggregations()
                .get("attr_id_agg");
        final List<SearchResult.AttrVo> attrVoList = attrIdAgg
                .getBuckets()
                .stream()
                .map(bucket -> {
                    final SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                    // 1、得到属性的id
                    final long attrId = bucket
                            .getKeyAsNumber()
                            .longValue();
                    attrVo.setAttrId(attrId);

                    // 2、得到属性的名字
                    final ParsedStringTerms attrNameAgg = bucket
                            .getAggregations()
                            .get("attr_name_agg");
                    final String attrName = attrNameAgg
                            .getBuckets()
                            .get(0)
                            .getKeyAsString();
                    attrVo.setAttrName(attrName);

                    // 3、得到属性的所有值
                    final ParsedStringTerms attrValueAgg = bucket
                            .getAggregations()
                            .get("attr_value_agg");
                    final List<String> attrValues = attrValueAgg
                            .getBuckets()
                            .stream()
                            .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                            .collect(Collectors.toList());
                    attrVo.setAttrValue(attrValues);

                    return attrVo;
                })
                .collect(Collectors.toList());
        final Map<Long, String> attrMap = attrVoList
                .stream()
                .collect(Collectors.toMap(SearchResult.AttrVo::getAttrId, SearchResult.AttrVo::getAttrName));
        result.setAttrs(attrVoList);

        // 3、当前商品涉及到的所有品牌信息
        final ParsedLongTerms brandAgg = response
                .getAggregations()
                .get("brand_agg");
        final List<SearchResult.BrandVo> brandVoList = brandAgg
                .getBuckets()
                .stream()
                .map(bucket -> {
                    final SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

                    // 1、得到品牌的id
                    final long brandId = bucket
                            .getKeyAsNumber()
                            .longValue();
                    brandVo.setBrandId(brandId);

                    // 2、得到品牌的名字
                    final ParsedStringTerms brandNameAgg = bucket
                            .getAggregations()
                            .get("brand_name_agg");
                    final String brandName = brandNameAgg
                            .getBuckets()
                            .get(0)
                            .getKeyAsString();
                    brandVo.setBrandName(brandName);

                    // 3、得到品牌的图片
                    final ParsedStringTerms brandImgAgg = bucket
                            .getAggregations()
                            .get("brand_img_agg");
                    final String brandImg = brandImgAgg
                            .getBuckets()
                            .get(0)
                            .getKeyAsString();
                    brandVo.setBrandImg(brandImg);

                    return brandVo;
                })
                .collect(Collectors.toList());
        final Map<Long, String> brandMap = brandVoList
                .stream()
                .collect(Collectors.toMap(SearchResult.BrandVo::getBrandId, SearchResult.BrandVo::getBrandName));
        result.setBrands(brandVoList);

        // 4、当前商品涉及到的所有分类信息
        // 获取到分类的聚合
        final ParsedLongTerms catalogAgg = response
                .getAggregations()
                .get("catalog_agg");
        final List<SearchResult.CatalogVo> catalogVoList = catalogAgg
                .getBuckets()
                .stream()
                .map(bucket -> {
                    final SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();

                    // 得到分类id
                    final String catalogId = bucket.getKeyAsString();
                    catalogVo.setCatalogId(Long.parseLong(catalogId));

                    // 得到分类名
                    final ParsedStringTerms catalogNameAgg = bucket
                            .getAggregations()
                            .get("catalog_name_agg");
                    final String catalogName = catalogNameAgg
                            .getBuckets()
                            .get(0)
                            .getKeyAsString();
                    catalogVo.setCatalogName(catalogName);

                    return catalogVo;
                })
                .collect(Collectors.toList());
        result.setCatalogs(catalogVoList);
        //===============以上可以从聚合信息中获取====================//

        // 5、分页信息-页码
        result.setPageNum(param.getPageNum());

        // 5.1、分页信息-总记录数
        final long total = hits.getTotalHits().value;
        result.setTotal(total);

        // 5.2、分页信息-总页码-计算
        final int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ?
                               (int) total / EsConstant.PRODUCT_PAGESIZE :
                               (int) total / EsConstant.PRODUCT_PAGESIZE + 1;
        result.setTotalPages(totalPages);

        // 5.3、分页信息-导航页码
        final List<Integer> pageNavList = IntStream
                .rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        result.setPageNavs(pageNavList);

        // 6、面包屑导航-属性
        final List<String> attrs = param.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            final List<SearchResult.NavVo> navVoList = attrs
                    .stream()
                    .map(attr -> {
                        // 1、分析每一个attrs传过来的参数值
                        final SearchResult.NavVo navVo = new SearchResult.NavVo();
                        final String[] s = attr.split("_");
                        // 封装属性值
                        navVo.setNavValue(s[1]);
                        // 封装属性名
                        final long attrId = Long.parseLong(s[0]);
                        result
                                .getAttrIds()
                                .add(attrId);
                        navVo.setNavName(attrMap.get(attrId));

                        // 2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                        // 拿到所有的查询条件，去掉当前
                        final String replace = replaceQueryString(param, "attrs", attr);
                        navVo.setLink("http://search.gulimall.com/list.html?" + replace);
                        return navVo;
                    })
                    .collect(Collectors.toList());
            result.setNavs(navVoList);
        }

        // 7、面包屑导航-品牌
        final List<Long> brandIdList = param.getBrandId();
        if (!CollectionUtils.isEmpty(brandIdList)) {
            final SearchResult.NavVo nav = new SearchResult.NavVo();
            nav.setNavName("品牌");

            final String navValue = brandIdList
                    .stream()
                    .map(brandMap::get)
                    .collect(Collectors.joining(";"));
            nav.setNavValue(navValue);

            final AtomicReference<String> replace = new AtomicReference<>("");
            brandIdList.forEach(brandId -> replace.set(replaceQueryString(param, "brandId", brandId.toString())));
            nav.setLink("http://search.gulimall.com/list.html?" + replace.get());

            result
                    .getNavs()
                    .add(nav);
        }
        return result;
    }

    private String replaceQueryString(SearchParam param, String key, String value) {
        String encode = null;
        try {
            encode = URLEncoder
                    .encode(value, StandardCharsets.UTF_8.name())
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return param
                .get_queryString()
                .replace("&" + key + "=" + encode, "");
    }

}
