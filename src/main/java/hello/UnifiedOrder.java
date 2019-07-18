package hello;

import java.security.SecureRandom;
import java.util.Random;

public class UnifiedOrder {
    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static  final Random rng = new SecureRandom();

    static char randomChar(){
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    static String randomUUID(int length, int spacing, char spacerChar){
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while(length > 0){
            if(spacer == spacing){
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb.toString();
    }

    public static Message tradeAppPay(){
        System.out.println(randomUUID(16, 4, '-'));
        return Alipay.tradeAppPay(randomUUID(16, 4, '-'));
    }
}
