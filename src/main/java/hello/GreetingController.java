package hello;

import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "world") String name){
        return  new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/alipay")
    public Message alipay(){
        System.out.println("支付宝下单请求成功");
        return  UnifiedOrder.alipayTradeAppPay();
    }

    @RequestMapping("/wxpay")
    public Message wxpay(){
        System.out.println("微信支付请求成功");
        return UnifiedOrder.wechatTradAppPay();
    }

    @RequestMapping("/alipay/notifyUrl")
    public String alipayNotifyUrl(HttpServletRequest request){
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
//        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
//        // boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        //boolean flags = AlipaySignature.rsaCheckV1(params, Alipay.ALIPAY_PUBLIC_KEY, Alipay.CHARSET, "RSA2");
        boolean flag = false;
        try{
            flag = AlipaySignature.rsaCheckV2(params, Alipay.ALIPAY_PUBLIC_KEY, Alipay.CHARSET, Alipay.SIGN_TYPE);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(params.toString());
        System.out.println("回调成功");
        return flag ? "success" : "fail";
    }

    @RequestMapping("/wxpay/notifyUrl")
    public String wxpayNotifyUrl(HttpServletRequest request) {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        System.out.println(requestParams.toString());
        System.out.println("回调成功");
        return "success";
    }
}
