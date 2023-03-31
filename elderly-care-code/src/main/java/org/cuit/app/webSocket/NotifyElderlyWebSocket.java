package org.cuit.app.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AppException;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.TokenService;

import org.cuit.app.utils.R;
import org.cuit.app.utils.WebSocketUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 关系绑定与todolist通知
 *
 * @author jirafa
 * @date 2023/03/18
 */
@Component
@Slf4j
@ServerEndpoint("/notify/elderly/{token}")
public class NotifyElderlyWebSocket {

    private static TokenService tokenService;

    /**
     * 标识当前连接客户端的用户id
     */
    private User user;


    @Resource
    public void setTokenService(TokenService tokenService) {
        NotifyElderlyWebSocket.tokenService = tokenService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        user = tokenService.parseToken(token);
        if(!user.getIsElderly()){
            throw new AuthorizedException("不是老人,非法连接");
        }

        if (!WebSocketUtils.getElderlyConnection().containsKey(user.getId())) {
            WebSocketUtils.addElderlyConnection(user.getId(), session);
        }
        log.info("isOpen:=" + session.getRequestURI().toString());
    }

    @OnClose
    public void onClose() {
        WebSocketUtils.removeElderlyConnection(user.getId());
        log.info("elderly连接关闭:{}", user);
    }

}
