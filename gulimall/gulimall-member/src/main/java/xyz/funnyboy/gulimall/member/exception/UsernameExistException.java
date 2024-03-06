package xyz.funnyboy.gulimall.member.exception;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-06 20:59:32
 */
public class UsernameExistException extends RuntimeException
{
    public UsernameExistException() {
        super("用户已存在");
    }
}
