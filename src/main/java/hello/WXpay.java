package hello;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.*;

public class WXpay {
    public enum SignType {
        MD5, HMACSHA256
    }

    public enum PromptMessage {
        FAIL("下单失败"), SUCCESS("下单成功");

        private final String message;

        private PromptMessage(String message) {
            this.message = message;
        }
    }

    public static final String FIELD_SIGN = "sign";

    /*
     * 统一下单地址
     */
    public static final String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

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

    /*
     * 生成下单需要的nonce_str
     */
    public static String generateNonceStr(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSign(Map<String, String> data, SignType signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(KEY);
        if (SignType.MD5.equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else if (SignType.HMACSHA256.equals(signType)) {
            return HMACSHA256(sb.toString(), KEY);
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    /*
     * 生成订单Map
     */
    public static Map<String, String> generateOrderMap(String outTradeNO) throws Exception {
        final String nonceStr = generateNonceStr();
        String ipAddress = getLocalIp();
        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", APPID);
        map.put("attach", "test");
        map.put("body", "app");
        map.put("mch_id", MCHID);
        map.put("nonce_str", nonceStr);
        map.put("notify_url", "http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php");
        map.put("out_trade_no", outTradeNO);
        map.put("spbill_create_ip", ipAddress);
        map.put("total_fee", "1");
        map.put("trade_type", "APP");

        final String sign = generateSign(map, SignType.MD5);
        map.put("sign", sign);

        return map;
    }

    public static Message initiateRequest(String outTradeNO) {
        try {
            Map<String, String> map = generateOrderMap(outTradeNO);
            HttpResponse<String> response = Unirest.post("https://api.mch.weixin.qq.com/pay/unifiedorder")
                    .header("Content-Type", "application/xml")
                    .body(mapToXml(map))
                    .asString();
            Map<String, String> result = xmlToMap(response.getBody());
            // partnerid package timestamp 三个参数参见 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12
            result.put("partnerid", MCHID);
            result.put("package", "Sign=WXPay");
            result.put("timestamp", String.valueOf(System.currentTimeMillis()));
            if (result.get("return_code").equals(PromptMessage.FAIL.toString()) ) {
                return new Message(405, PromptMessage.FAIL.message, result.get("return_msg"));
            }
            return new Message(response.getStatus(), PromptMessage.SUCCESS.message, JSONObject.toJSONString(result));
        }catch (Exception e){
            return new Message(403, PromptMessage.FAIL.message, "");
        }
    }

    /*
     * 订单Map to Xml
     */

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        org.w3c.dom.Document document = WXPayXmlUtil.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilder documentBuilder = WXPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            System.out.println(String.format("Invalid XML, can not convert to map. Error message: %s. XML content: %s", ex.getMessage(), strXML));
            throw ex;
        }

    }
}
