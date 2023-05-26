package com.mindhub.Homebanking.Utils;

import java.util.Random;

public class ClientUtils {
    public ClientUtils() {
    }
    public static int getRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(99999999);
        return randomNumber;
    }
    public static String getAccountNumber(int randomNumber) {
        String accountNumber = "VIN" + String.format("%08d", randomNumber);
        return accountNumber;
    }


}
