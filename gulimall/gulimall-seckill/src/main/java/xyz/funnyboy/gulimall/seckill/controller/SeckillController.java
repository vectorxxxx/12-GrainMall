package xyz.funnyboy.gulimall.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.common.to.seckill.SeckillSkuRedisTO;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.seckill.service.SeckillService;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-21 10:37:26
 */
@Controller
public class SeckillController
{
    @Autowired
    SeckillService seckillService;

    /**
     * 返回当前时间可以参与秒杀的商品信息
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getCurrentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTO> skuRedisTOList = seckillService.getCurrentSeckillSkus();
        return R
                .ok()
                .setData(skuRedisTOList);
    }

    /**
     * 根据skuId查询商品当前时间秒杀信息
     */
    @ResponseBody
    @GetMapping(value = "/sku/seckill/{skuId}")
    public R getSkuSeckilInfo(
            @PathVariable("skuId")
                    Long skuId) {
        SeckillSkuRedisTO to = seckillService.getSkuSeckilInfo(skuId);
        return R
                .ok()
                .setData(to);
    }

    /**
     * 秒杀请求
     *
     * @return
     */
    @GetMapping("/kill")
    public String secKill(
            @RequestParam("killId")
                    String killId,
            @RequestParam("key")
                    String key,
            @RequestParam("num")
                    Integer num, Model model) throws InterruptedException {
        String orderSn = seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}
