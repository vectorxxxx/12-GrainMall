package xyz.funnyboy.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.funnyboy.common.utils.HttpUtils;
import xyz.funnyboy.common.utils.R;
import xyz.funnyboy.gulimall.auth.config.GiteeOAuthConfig;
import xyz.funnyboy.gulimall.auth.feign.MemberFeignService;
import xyz.funnyboy.gulimall.auth.vo.MemberRespVO;
import xyz.funnyboy.gulimall.auth.vo.SocialUser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-07 11:06:49
 */
@Controller
@Slf4j
public class Oauth2Controller
{

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private GiteeOAuthConfig giteeOAuthConfig;

    @GetMapping("/authorize")
    public String authorize() {
        final String uri = String.format("%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=%s", giteeOAuthConfig.getHost(), giteeOAuthConfig.getClientId(),
                giteeOAuthConfig.getRedirectUri(), giteeOAuthConfig.getResponseType());
        return "redirect:" + uri;
    }

    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(
            @RequestParam
                    String code) throws IOException, URISyntaxException {
        // 根据code换取accessToken
        Map<String, String> headers1 = new HashMap<>();
        headers1.put("client_id", giteeOAuthConfig.getClientId());
        headers1.put("client_secret", giteeOAuthConfig.getClientSecret());
        headers1.put("redirect_uri", giteeOAuthConfig.getRedirectUri());
        headers1.put("grant_type", giteeOAuthConfig.getGrantType());
        headers1.put("code", code);
        final HttpResponse response1 = HttpUtils.doPost(giteeOAuthConfig.getHost(), "/oauth/token", headers1);
        if (response1
                .getStatusLine()
                .getStatusCode() != 200) {
            return giteeOAuthConfig.getFailPath();
        }

        // 获取 accessToken
        final String json = EntityUtils.toString(response1.getEntity());
        final SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
        final String accessToken = socialUser.getAccess_token();
        HashMap<String, String> headers2 = new HashMap<>();
        headers2.put("access_token", accessToken);
        final HttpResponse response2 = HttpUtils.doGet(giteeOAuthConfig.getHost(), "/api/v5/user", headers2);
        if (response2
                .getStatusLine()
                .getStatusCode() != 200) {
            return giteeOAuthConfig.getFailPath();
        }

        // 获取基本信息
        final String json2 = EntityUtils.toString(response2.getEntity());
        final Map<String, String> map2 = JSON.parseObject(json2, new TypeReference<Map<String, String>>() {});
        socialUser.setId(Integer.valueOf(map2.get("id")));
        socialUser.setName(map2.get("name"));
        log.info("socialUser:{}", socialUser);

        // 进行社交登录
        final R r = memberFeignService.oauthLogin(socialUser);
        if (r.getCode() != 0) {
            return giteeOAuthConfig.getFailPath();
        }

        // 登录成功
        final MemberRespVO memberRespVO = r.getData("data", new TypeReference<MemberRespVO>() {});
        log.info("memberRespVO:{}", memberRespVO);
        return giteeOAuthConfig.getSuccessPath();
    }
}
