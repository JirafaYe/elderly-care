package org.cuit.app.webSocket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.TokenService;
import org.cuit.app.utils.WebSocketUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于推送求救信息
 *  @author jirafa
 *  @date 2023/03/31
 */
@Component
@Slf4j
@ServerEndpoint("/notify/sos/{token}")
public class SosNotifyWebsocket{
    /**
     * 标识当前连接客户端的用户id
     */
    private User user;

    private static TokenService tokenService;


    @Resource
    public void setTokenService(TokenService tokenService) {
        SosNotifyWebsocket.tokenService = tokenService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        user = tokenService.parseToken(token);
        if(user.getIsElderly()){
            throw new AuthorizedException("不是监护人,非法连接");
        }
        if (!WebSocketUtils.getGuardianConnection().containsKey(user.getId())) {
            WebSocketUtils.addGuardianConnection(user.getId(), session);
        }
        log.info("isOpen:=" + session.getRequestURI().toString());
    }

    @OnClose
    public void onClose() {
        WebSocketUtils.removeGuardianConnection(user.getId());
        log.info("监护人连接关闭:{}", user);
    }
}
