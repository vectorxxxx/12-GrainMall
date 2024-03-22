package xyz.funnyboy.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-22 15:41:39
 */
@Component
public class SeckillSentinelUrlBlockHandler implements BlockExceptionHandler
{

    /**
     * 自定义限流返回信息
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        // 降级业务处理
        R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response
                .getWriter()
                .write(JSON.toJSONString(error));
    }
}
