package xyz.funnyboy.gulimall.auth;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import xyz.funnyboy.common.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 16:54:42
 */
public class SMSTest
{
    public static void main(String[] args) {
        final String host = "https://gyytz.market.alicloudapi.com";
        final String path = "/sms/smsSend";
        final String method = "POST";
        final String appcode = "fd46d8b685624390baf9a0a37456269e";
        final String phone = "18913212404";
        final String code = "12345";
        final String minute = "5";
        final String smsSignId = "2e65b1bb3d054466b82f0c9d125465e2";
        final String templateId = "908e94ccf08b4476ba6c876d13f084ad";

        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", phone);
        querys.put("param", "**code**:" + code + ",**minute**:" + minute);

        //smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html
        querys.put("smsSignId", smsSignId);
        querys.put("templateId", templateId);
        Map<String, String> bodys = new HashMap<>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t
             * \t* 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
