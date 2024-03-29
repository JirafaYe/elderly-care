package org.cuit.app.constant;

/**
 * Token的Key常量
 *
 * @author jiaraf
 */
public class TokenConstants {
    /**
     * 令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 用户id
     */
    public final static String USER_ID ="id";

    /**
     * 用户名
     */
    public final static String USER_NAME="name";

    /**
     * 用户身份
     */
    public final static String USER_IDENTITY ="is_elderly";

    /**
     * 过期时间
     */
    public final static Integer EXPIRATION =36000*1000;
}