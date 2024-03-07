package xyz.funnyboy.gulimall.gulimalltestssoclient2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-07 17:14:16
 */
@Controller
public class HelloController
{
    private static final String URL = "http://ssoserver.com:8080";

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping(value = "/boss")
    public String boss(Model model, HttpSession session,
                       @RequestParam(value = "token",
                                     required = false)
                               String token) {

        if (!StringUtils.isEmpty(token)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity(URL + "/userinfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser", body);
        }

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + URL + "/login.html" + "?redirect_url=http://client2.com:8082/boss";
        }
        else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps", emps);
            return "boss";
        }
    }
}
