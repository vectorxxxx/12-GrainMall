package xyz.funnyboy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.product.entity.SkuInfoEntity;
import xyz.funnyboy.gulimall.product.vo.SkuItemVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2024-01-28 20:52:38
 */
public interface SkuInfoService extends IService<SkuInfoEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按条件分页查询
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 按 SPU ID 获取 SKU
     *
     * @param spuId SPU ID
     * @return {@link List}<{@link SkuInfoEntity}>
     */
    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    /**
     * 项目
     *
     * @param skuId SKU ID
     * @return {@link SkuItemVO}
     */
    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;

    /**
     * 通过 ID 获取
     *
     * @param skuIds SKU ID
     * @return {@link List}<{@link SkuInfoEntity}>
     */
    List<SkuInfoEntity> getByIds(List<Long> skuIds);
}

