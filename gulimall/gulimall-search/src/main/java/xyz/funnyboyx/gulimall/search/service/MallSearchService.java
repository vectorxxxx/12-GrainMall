package xyz.funnyboyx.gulimall.search.service;

import xyz.funnyboy.common.vo.search.SearchParam;
import xyz.funnyboy.common.vo.search.SearchResult;

/**
 * 商城搜索Service
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-28 14:03:37
 */
public interface MallSearchService
{
    /**
     * 搜索
     *
     * @param param 参数
     * @return {@link SearchResult}
     */
    SearchResult search(SearchParam param);
}
