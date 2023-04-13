package org.cuit.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket工具类
 *
 */
@Slf4j
public class WebSocketUtils{
    /**
     * 用于存所有的连接服务的 老人客户端
     */
    private static ConcurrentHashMap<Integer, Session> elderlyConnection = new ConcurrentHashMap<>();

    /**
     * 用于存所有的连接服务的监护人客户端
     */
    private static ConcurrentHashMap<Integer, Session> guardianConnection = new ConcurrentHashMap<>();


    public static void sendMsg(ConcurrentHashMap<Integer, Session> connectionMap, Integer userId, Object vo) throws IOException {
        Session id = connectionMap.get(userId);
        if (id == null) {
            log.error("websocket unconnected");
            return;
        }

        id.getBasicRemote().sendText(new ObjectMapper()
                .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                .writeValueAsString(R.ok(vo, "type:" + vo.getClass().getSimpleName())));
    }
    public static void sendErrMsg(Session session,String errMsg) throws IOException {
        session.getBasicRemote().sendText(new ObjectMapper()
                .writeValueAsString(R.fail(errMsg)));
    }

    public static void addElderlyConnection(Integer userId, Session session) {
        elderlyConnection.put(userId, session);
    }

    public static void addGuardianConnection(Integer userId, Session session) {
        guardianConnection.put(userId, session);
    }

    public static void removeElderlyConnection(Integer userId) {
        elderlyConnection.remove(userId);
    }

    public static ConcurrentHashMap<Integer, Session> getElderlyConnection() {
        return elderlyConnection;
    }

    public static ConcurrentHashMap<Integer, Session> getGuardianConnection() {
        return guardianConnection;
    }

    public static void removeGuardianConnection(Integer userId) {
        guardianConnection.remove(userId);
    }
}
