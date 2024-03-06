package xyz.funnyboy.gulimall.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.common.constant.AuthServerConstant;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.auth.feign.ThirdPartFeignService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 19:30:19
 */
@Controller
public class LoginController
{
    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(
            @RequestParam("phone")
                    String phone) {
        // 查询redis中是否存在缓存
        final String redisCache = stringRedisTemplate
                .opsForValue()
                .get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);

        // 存在，则判断是否在60s之内
        if (redisCache != null) {
            final long time = Long.parseLong(redisCache.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        // 不存在，则发送验证码
        final String code = generateCode();
        thirdPartFeignService.sendCode(phone, code);
        // 缓存验证码
        stringRedisTemplate
                .opsForValue()
                .set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
        return R.ok();
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(new Random().nextInt(10));
        }
        return code.toString();
    }
}
