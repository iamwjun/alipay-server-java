package hello;

import java.util.UUID;

public class WXpay {
    /*
     * 这里填开户邮件中的商户号
     */
    public static final String MCHID = "1225312702";

    /*
     * 这里填开户邮件中的（公众账号APPID或者应用APPID）
     */
    public static final String APPID = "wx426b3015555a46be";

    /*
     * 这里请使用商户平台登录账户和密码登录http://pay.weixin.qq.com 平台设置的“API密钥”，为了安全，请设置为32字符串。
     */
    public static final String  KEY = "e10adc3949ba59abbe56e057f20f883e";

    /*
     * 改参数在JSAPI支付（open平台账户不能进行JSAPI支付）的时候需要用来获取用户openid，可使用APPID对应的公众平台登录http://mp.weixin.qq.com 的开发者中心获取AppSecret。
     */
    public static final String APPSECRET = "01c6d59a3f9024db6336662ac95c8e74";

    public static UUID generateNonceStr(){
        return UUID.randomUUID();
    }
}
