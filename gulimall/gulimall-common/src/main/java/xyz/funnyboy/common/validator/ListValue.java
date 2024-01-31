package xyz.funnyboy.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * JSR303自定义注解
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 16:22:57
 */
@Documented
@Target({
                ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE
        })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListValueConstraintValidator.class)
public @interface ListValue
{
    /**
     * 使用该属性去Validation.properties中取
     *
     * @return {@link String}
     */
    String message() default "{xyz.funnyboy.common.valid.listvalue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] values() default {};
}
