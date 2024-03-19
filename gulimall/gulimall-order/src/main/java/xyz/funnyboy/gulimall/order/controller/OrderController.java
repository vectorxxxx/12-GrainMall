package xyz.funnyboy.gulimall.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.common.utils.PageUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;
import xyz.funnyboy.gulimall.order.service.OrderService;

import java.util.Arrays;
import java.util.Map;



/**
 * 订单
 *
 * @author VectorX
 * @email uxiahnan@outlook.com
 * @date 2024-01-29 10:19:36
 */
@RestController
@RequestMapping("order/order")
public class OrderController
{
    @Autowired
    private OrderService orderService;

    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(
            @PathVariable("orderSn")
                    String orderSn) {
        OrderEntity orderEntity = orderService.getOrderByOrderSn(orderSn);
        return R
                .ok()
                .setData(orderEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:order:list")
    public R list(
            @RequestParam
                    Map<String, Object> params) {
        PageUtils page = orderService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
