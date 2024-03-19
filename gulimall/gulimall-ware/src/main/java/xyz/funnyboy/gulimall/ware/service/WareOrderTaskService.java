package xyz.funnyboy.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:26:44
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity>
{

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按订单 SN 获取订单任务
     *
     * @param orderSn 订购 SN
     * @return {@link WareOrderTaskEntity}
     */
    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

