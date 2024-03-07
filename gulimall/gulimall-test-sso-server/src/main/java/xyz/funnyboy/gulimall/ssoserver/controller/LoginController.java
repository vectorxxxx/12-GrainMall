package xyz.funnyboy.gulimall.ssoserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-07 17:06:53
 */
@Controller
@Slf4j
public class LoginController
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userinfo")
    public String userinfo(
            @RequestParam(value = "token")
                    String token) {
        return redisTemplate
                .opsForValue()
                .get(token);

    }

    @GetMapping("/login.html")
    public String loginPage(
            @RequestParam(value = "redirect_url",
                          required = false)
                    String url, Model model,
            @CookieValue(value = "sso_token",
                         required = false)
                    String sso_token) {
        // 有人登录过
        if (!StringUtils.isEmpty(sso_token)) {
            return "redirect:" + url + "?token=" + sso_token;
        }

        model.addAttribute("url", url);
        return "login";
    }

    @PostMapping(value = "/doLogin")
    public String doLogin(
            @RequestParam("username")
                    String username,
            @RequestParam("password")
                    String password,
            @RequestParam("redirect_url")
                    String url, HttpServletResponse response) {
        //登录成功跳转，跳回到登录页
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String uuid = UUID
                    .randomUUID()
                    .toString()
                    .replace("_", "");
            redisTemplate
                    .opsForValue()
                    .set(uuid, username);
            Cookie sso_token = new Cookie("sso_token", uuid);

            response.addCookie(sso_token);
            return "redirect:" + url + "?token=" + uuid;
        }

        // 登录失败
        return "login";
    }

}
