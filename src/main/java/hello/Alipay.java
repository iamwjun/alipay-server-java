package hello;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.domain.AlipayTradeAppPayModel;

public class Alipay {

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016101100661281";

    /**
     * 用于支付宝支付业务的入参网关。
     */
    public static final String SERVER_URL = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088102179091060";

    /**
     * pkcs8 格式的商户私钥。
     */
    public static final String RSA_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCGU17Ewt9RpG4Okw5pvg5QHt8sz8zVI0MqLI9Pf+AByt7ovdZnPxcVxfm0STWo8X5saNCsW3tnvPzRPmHxREBbfnLConvtmz49Y8olMBxWLWrR5S6NZlrsFx/Yn8R0VrMOA+lktIX44tc4zYguPiqAD2mT7Yf9Ko57AaSw2CgE2zgzwHkAYYJylHJOfzjg20kSDq6ch+RiaRYqkCcBpicztXbsRyXZcoCRGxqE4COp/Zz8GMTedmAujYz2IRpnxjdAgg6WMQeiD2rYR2gUhK9qVmaadDIIgvysc8nH35KzszjLD3DxiGoAmC+db0x2FFzEjfVwBsXES3LrNdP5CHl3AgMBAAECggEACzL4O79jjxL+egiYswg9eUCZqsrIcRy3hNSiPiaTLpMTcqQhNAp2ikIvR3CzIJrBx5gVjckyyLtg+LESyWK/WuF3//I8EttWnResytzv/99ZgJZzAyb9faGP7iej4W85DnoVA2hpow6Gkx3PtMtKD9M5IjQzmk/qASdp67SZ4Y2lKPKfi1UUPA/5gXEs0lqQS9HpvIECFD2Mp69eFippdWKT4Jy4Vl7BJyl1h8oCynEwS+cs2FLfPOS3309TIlCKYapn1qCV0bEk7Gx1PxQet31LVL9M2zf+Knr+5CG1EPlKj/LRICrsk3PtCjBD0hFKdhHPuKmH5td5EC3+7c8NQQKBgQD4cqcTQ4bpjeqe2JQJx5wcrf9IIsQvssBMm374o5LPc6AAbqO9hWyqAVsmv/oQio+dv1swvTcWCw8WVGPn12Ie6ZMuDQSuJCB0g2Hre5kh4iJl7gx8+8XhWUwDMXfD1wGlDYEeaerveGs/TwS4tgb4L4wOn5wI5gBOdX1iNeKufQKBgQCKaKcOIFaulEFds6NM6yxO8yMBWWbRTRmQt1vfxYBun6kU449/bA27TCkOUpndsKwWnLfr3EtN5dayae3PKSTn4oP72V0+BgpjhG73XyJhInCXQRWME6IOFyFW7tWSxAhedVbMlItD4nugjwLYm3ShVRaeu75Lp/lv0HhkdyeGAwKBgQDblGl5qxrX3KfaEEx2gEbHSjjvRlriVTeD/YIwulEMrIJjH2X0ILIzZtNq4g1fBTd9EhzEbNb80nVZJOjPhRqhe4qrDrJuPlu43k1y1GA5BCWmtMewxszTKBr4T4YsPiCkAdV2TeJoF6Oh5nKj1a0/OLauTf6YFr96mHhijoFz+QKBgB46Vum95FCCbngxaGO/JNElRqrfxJXHeeLiOL15W+BNHFdEj61rguA59Ol83r/M+viBR6XKWePsY2RZwCczPC0silixkyydkx1C6h06eL6wD9jozPTnwMfFA3IB/UDtcUp34/oQIw4zY36gXSXDZchqVeNa62eIivCBnhmK8GXTAoGBANv9OSMoo6ENxmoAvTfI2Cb3vMo44lQ2wDXTpLRGAIMvh8hepgg8NMXC1vZcoKY8pgOLv67miWg7eDC1VSLNw6ujdH9kX/PfBoplZ44rQMp9/9mxzw3kDfuXf+x6VgJTcxsj8SyHaTD+IDdxMiUB3pbRkErqMQTEac8wl4bPbRVC";

    public static final String CHARSET = "utf-8";

    public static final String SIGN_TYPE = "RSA2";

    /**
     * 支付宝公钥。
     */
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmoRoQL3wSbCmd8B1FK+Y5K5DVHj+YWMHjR6cPW5O3WG6NnFuEnsxE60fX49Egu1la9DZ3opa0ruEa4cJRh1Le1v255vFMob9BYAZOq7C8UKaj+7fErNc2XJS2EiUYGysYUoAMlHWHgnqM7KJW+AiKVFJNFk0OfhUZ55o+k3BuRxQWoLYqZD7NbKZb3Dva9F7AHluDgk1HxTVXcxoWjex6Pjs5LzwhHzZrTZqq5+KJSn+jdueSsRJTW7XGaUpXjbu6VrR4t7/NYHKqEyLEdl8GfyEmdkjAjcbqgS/Co6uPsvC7DFkLZwbYmUlwlNKL+K6YJKXjAD3f1bqb71tAkQDUwIDAQAB";

    public static Message tradeAppPay(String outTradeNO) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(SERVER_URL, APPID, RSA_PRIVATE, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo(outTradeNO);
        model.setTimeoutExpress("30m");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("http://oicq362934348.eicp.net/notifyUrl");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
            return new Message(200, "成功", response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new Message(403, "下单失败", "");
        }
    }
}
