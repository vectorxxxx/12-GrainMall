package xyz.funnyboy.gulimall.product.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.funnyboy.common.exception.BizCodeEnum;
import xyz.funnyboy.common.utils.R;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 15:43:24
 */
@Slf4j
@RestControllerAdvice(basePackages = "xyz.funnyboy.gulimall.product.controller")
public class GulimallExceptionControllerAdvice
{
    /**
     * 处理方法参数无效异常
     *
     * @param exception 例外
     * @return {@link R}
     */
    @ExceptionHandler(value = Exception.class)
    public R handleException(MethodArgumentNotValidException exception) {
        final Map<String, String> map = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(fieldError -> !StringUtils.isEmpty(fieldError.getDefaultMessage()) && !StringUtils.isEmpty(fieldError.getField()))
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        log.error("数据校验出现问题{}, 异常类型{}", exception.getMessage(), exception.getClass());
        return R
                .error(BizCodeEnum.VAILD_EXCEPTION.getCode(), BizCodeEnum.VAILD_EXCEPTION.getMsg())
                .put("data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("未知异常{}, 异常类型{}", throwable.getMessage(), throwable.getClass());
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
