package xyz.funnyboy.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.funnyboy.gulimall.thirdparty.component.SmsComponent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 10:56:52
 */
@SpringBootTest
public class GulimallThirdPartyApplicationTests
{
    @Autowired
    private OSS ossClient;

    @Autowired
    private SmsComponent smsComponent;

    @Test
    public void testSendSms() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(new Random().nextInt(10));
        }
        System.out.println(code);
        smsComponent.sendSmsCode("18913212404", code.toString());
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        // // Endpoint以杭州为例，其它Region请按实际情况填写。
        // String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        // String accessKeyId = "LTAI5tJvtifEWV6R4Rjk7wnb";
        // String accessKeySecret = "5qtpEZBbkm86TYNNNiQaCLPUq47UiS";
        // final OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        final String bucketName = "gulimall-vectorx";
        final String key = "test.gif";
        final FileInputStream inputStream = new FileInputStream("D:\\workspace-mine\\12-GrainMall\\gulimall\\gulimall-third-party\\src\\main\\resources\\oss\\1.gif");
        ossClient.putObject(bucketName, key, inputStream);
        ossClient.shutdown();

        System.out.println("上传成功");
    }
}
