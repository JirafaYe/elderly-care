package org.cuit.app.interceptor;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.constant.Constants;
import org.cuit.app.constant.TokenConstants;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.TokenService;
import org.cuit.app.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中获取Authorization
        String authorization = request.getHeader(TokenConstants.AUTHENTICATION);
        log.info("log=:" + authorization);
        log.info("登录拦截器拦截的请求路径是:{}", request.getRequestURI());

        if (!StringUtils.isEmpty(authorization) && authorization.startsWith(TokenConstants.PREFIX)) {
            //获取token
            String token = authorization.substring(7);
            log.info("token==>" + token);

            User userInfo = tokenService.parseToken(token);

            request.setAttribute(Constants.USER_ATTRIBUTE, userInfo);
            log.info("user:=" + userInfo);
            return true;
        } else {
            throw new AuthorizedException("用户未登录");
        }
    }

}
