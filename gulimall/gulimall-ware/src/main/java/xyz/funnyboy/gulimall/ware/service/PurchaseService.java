package xyz.funnyboy.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.ware.entity.PurchaseEntity;
import xyz.funnyboy.gulimall.ware.vo.MergeVo;
import xyz.funnyboy.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
public interface PurchaseService extends IService<PurchaseEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单
     *
     * @param params 参数
     * @return {@link PageUtils}
     */
    PageUtils queryPageUnreceive(Map<String, Object> params);

    /**
     * 合并采购需求
     *
     * @param mergeVo 合并 VO
     */
    void mergePurchase(MergeVo mergeVo);

    /**
     * 领取采购单
     *
     * @param ids IDS
     */
    void received(List<Long> ids);

    /**
     * 完成采购
     *
     * @param vo 采购单完成 VO
     */
    void done(PurchaseDoneVo vo);
}

