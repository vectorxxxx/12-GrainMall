package xyz.funnyboy.gulimall.seckill.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.funnyboy.common.constant.AuthServerConstant;
import xyz.funnyboy.common.vo.auth.MemberRespVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 14:12:29
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor
{
    public static ThreadLocal<MemberRespVO> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这个请求直接放行: /kill
        final boolean match = new AntPathMatcher().match("/kill", request.getRequestURI());
        if (match) {
            final HttpSession session = request.getSession();
            final MemberRespVO memberRespVO = (MemberRespVO) session.getAttribute(AuthServerConstant.LOGIN_USER);
            // session中有登录用户信息
            if (memberRespVO != null) {
                loginUser.set(memberRespVO);
                return true;
            }
            // session中没有登录用户信息
            else {
                session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
                response.sendRedirect("http://auth.gulimall.com/login.html");
                return false;
            }
        }
        return true;
    }
}
