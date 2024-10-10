package com.example.demo_kotak.transaction.services;

import com.example.demo_kotak.transaction.models.Transaction;

public interface TransactionManager<T> {

    String initiateTransaction(T transactionRequest); // returns transaction id

    Transaction getTransactionById(String id);
}
