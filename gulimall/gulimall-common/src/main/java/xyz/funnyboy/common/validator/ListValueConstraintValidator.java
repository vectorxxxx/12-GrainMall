package xyz.funnyboy.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义校验器：规定ListValue这个注解 用于校验 Integer 类型的数据
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-31 16:32:22
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer>
{
    private final Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        // 获取java后端写好的限制
        final int[] values = constraintAnnotation.values();
        for (int val : values) {
            set.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 每次请求传过来的值是否在java后端限制的值里
        return set.contains(value);
    }
}
