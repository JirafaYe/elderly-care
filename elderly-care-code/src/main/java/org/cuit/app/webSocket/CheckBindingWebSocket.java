package org.cuit.app.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.CheckBindingVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.utils.JwtUtils;

import org.cuit.app.utils.R;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 关系绑定通知
 *
 * @author jirafa
 * @date 2023/03/18
 */
@Component
@Slf4j
@ServerEndpoint("/notify/bindings/{token}")
public class CheckBindingWebSocket {

    /**
     * 标识当前连接客户端的用户id
     */
    private User user;

    /**
     * 用于存所有的连接服务的客户端
     */
    private static ConcurrentHashMap<Integer, Session> connectionMap = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        log.info("isOpen:=" + session.getRequestURI().toString());
        isValidToken(token);
        if (!connectionMap.containsKey(user.getId())) {
            connectionMap.put(user.getId(), session);
        }
    }

    @OnClose
    public void onClose() {
        connectionMap.remove(user);
        log.info("连接关闭:{}", user);
    }


    private void isValidToken(String token) {
        if (!StringUtils.isEmpty(token)) {
            user = new User();

            Claims claims = JwtUtils.parseToken(token);
            if (claims == null)
                throw new AuthorizedException("令牌不正确！");

            String userId = JwtUtils.getUserId(claims);
            String userName = JwtUtils.getUserName(claims);
            String userIdentity = JwtUtils.getUserIdentity(claims);

            if (userId == null || userName == null || userIdentity == null)
                throw new AuthorizedException("token验证失败");
            user.setId(Integer.parseInt(userId));
            user.setName(userName);
            user.setIsElderly(Boolean.parseBoolean(userIdentity));

            log.info("user:={}webSocket连接",user);
        } else {
            throw new AuthorizedException("用户未登录");
        }
    }

    public static void sendMsg(Integer userId, CheckBindingVO vo) throws IOException {
        Session id = connectionMap.get(userId);
        if (id == null)
            throw new AppException("websocket unconnected");
        id.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(R.ok(vo)));
    }
}
