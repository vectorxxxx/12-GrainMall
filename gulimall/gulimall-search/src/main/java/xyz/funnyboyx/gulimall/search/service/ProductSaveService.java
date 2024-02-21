package xyz.funnyboyx.gulimall.search.service;

import xyz.funnyboy.common.to.es.SkuEsModel;

import java.util.List;

/**
 * 商品保存Service
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-02-21 10:39:09
 */
public interface ProductSaveService
{
    /**
     * 将数据保存到Es
     *
     * @param skuEsModels SKU ES 型号
     * @return boolean
     * @throws Exception 例外
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws Exception;
}
