package xyz.funnyboy.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-22 16:42:45
 */
@Configuration
public class SentinelGatewayConfig
{
    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler()
        {
            /**
             * 网关限制了请求，就会调用此回调 Mono Flux
             *
             * @param serverWebExchange serverWebExchange
             * @param throwable         throwable
             * @return {@link Mono}<{@link ServerResponse}>
             */
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
                String errJson = JSON.toJSONString(error);

                return ServerResponse
                        .ok()
                        .body(Mono.just(errJson), String.class);
            }
        });
    }
}
