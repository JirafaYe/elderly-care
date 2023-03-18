package org.cuit.app.interceptor;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.constant.Constants;
import org.cuit.app.constant.TokenConstants;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AuthorizedException;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中获取Authorization
        String authorization = request.getHeader(TokenConstants.AUTHENTICATION);
        log.info("log=:" + authorization);
        log.info("登录拦截器拦截的请求路径是:{}", request.getRequestURI());

        if (!StringUtils.isEmpty(authorization) && authorization.startsWith(TokenConstants.PREFIX)) {
            User userInfo = new User();
            //获取token
            String token = authorization.substring(7);
            log.info("token==>" + token);

            Claims claims = JwtUtils.parseToken(token);
            if (claims == null)
                throw new AuthorizedException("令牌不正确！");

            String userId = JwtUtils.getUserId(claims);
            String userName = JwtUtils.getUserName(claims);
            String userIdentity = JwtUtils.getUserIdentity(claims);

            if (userId == null || userName == null || userIdentity == null)
                throw new AuthorizedException("token验证失败");
            userInfo.setId(Integer.parseInt(userId));
            userInfo.setName(userName);
            userInfo.setIsElderly(Boolean.parseBoolean(userIdentity));

            request.setAttribute(Constants.USER_ATTRIBUTE, userInfo);
            log.info("user:=" + userInfo);
            return true;
        } else {
            throw new AuthorizedException("用户未登录");
        }
    }

}
