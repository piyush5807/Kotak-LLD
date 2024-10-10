package com.example.demo_kotak.payment.services;

import java.util.UUID;

public class PaymentService {

    public String withdraw(Long accountId, Long amount){

        // Calls the downstream bank API for reducing the amount

        return UUID.randomUUID().toString();
    }
}
