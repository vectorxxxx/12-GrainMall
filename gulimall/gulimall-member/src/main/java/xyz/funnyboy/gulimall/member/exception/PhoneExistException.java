package xyz.funnyboy.gulimall.member.exception;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 20:59:32
 */
public class PhoneExistException extends RuntimeException
{
    public PhoneExistException() {
        super("手机号已存在");
    }
}
