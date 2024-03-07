package xyz.funnyboy.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.funnyboy.common.constant.AuthServerConstant;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.auth.feign.MemberFeignService;
import xyz.funnyboy.gulimall.auth.feign.ThirdPartFeignService;
import xyz.funnyboy.gulimall.auth.vo.UserLoginVO;
import xyz.funnyboy.gulimall.auth.vo.UserRegVO;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private MemberFeignService memberFeignService;

    @PostMapping("/login")
    public String login(UserLoginVO vo, RedirectAttributes attributes) {
        final R r = memberFeignService.login(vo);
        if (r.getCode() != 0) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
            attributes.addFlashAttribute("error", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

        return "redirect:http://gulimall.com";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegVO userRegVO, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            final Map<String, String> errors = result
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        final String codeCache = stringRedisTemplate
                .opsForValue()
                .get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegVO.getPhone());
        if (StringUtils.isEmpty(codeCache)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        final String code = userRegVO.getCode();
        if (!code.equals(codeCache.split("_")[0])) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        final R r = memberFeignService.register(userRegVO);
        if (r.getCode() != 0) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegVO.getPhone());
        return "redirect:http://auth.gulimall.com/login.html";
    }

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
