package xyz.funnyboy.gulimall.thirdparty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.thirdparty.component.SmsComponent;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 19:25:22
 */
@RestController
@RequestMapping("/sms")
public class SmsController
{
    @Autowired
    private SmsComponent smsComponent;
    
    @GetMapping("sendCode")
    public R sendCode(
            @RequestParam("phone")
                    String phone,
            @RequestParam("code")
                    String code) {
        smsComponent.sendSmsCode(phone, code);
        return R.ok();
    }
}
