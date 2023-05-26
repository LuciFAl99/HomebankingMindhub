package com.mindhub.Homebanking.Utils;

import java.util.Random;

public final class CardUtils {
    private CardUtils() {
    }
    public static int getCVV() {
        Random random = new Random();
        int cardCvv = random.nextInt(899) + 100;
        return cardCvv;
    }

    public static String getCardNumber() {
        String cardNumber = "";
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                cardNumber += " ";
            }
            cardNumber += (int) (Math.random() * 10);
        }
        return cardNumber;
    }

}
