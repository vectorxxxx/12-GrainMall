package xyz.funnyboy.gulimall.order.interceptor;

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
    public static ThreadLocal<MemberRespVO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String uri = request.getRequestURI();
        // 这个请求直接放行: /order/order/status/**
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        final boolean match = antPathMatcher.match("/order/order/status/**", uri);
        final boolean match1 = antPathMatcher.match("/payed/notify", uri);
        if (match || match1) {
            return true;
        }

        final HttpSession session = request.getSession();
        final MemberRespVO memberRespVO = (MemberRespVO) session.getAttribute(AuthServerConstant.LOGIN_USER);
        // session中有登录用户信息
        if (memberRespVO != null) {
            threadLocal.set(memberRespVO);
            return true;
        }
        // session中没有登录用户信息
        else {
            session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
