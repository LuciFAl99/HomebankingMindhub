package com.mindhub.Homebanking.Utils;

import com.mindhub.Homebanking.Models.ClientLoan;

import java.util.Optional;

public class LoanUtils {
    public LoanUtils() {
    }
    public static int getPayments(Optional<ClientLoan> clientLoan) {
        int payments = (int) Math.ceil(clientLoan.get().getFinalAmount() / clientLoan.get().getPayments());
        return payments;
    }
    public static int getRoundedAmount(double amount) {
        int roundedAmount = (int) Math.ceil(amount);
        return roundedAmount;
    }
}
