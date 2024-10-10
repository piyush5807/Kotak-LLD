package com.example.demo_kotak.transaction.repositories;

import com.example.demo_kotak.transaction.models.Transaction;

import java.util.Hashtable;

public class TransactionRepository {

    private Hashtable<String, Transaction> transactions;

    public TransactionRepository(){
        this.transactions = new Hashtable<>();
    }

    public void create(Transaction t){
        this.transactions.put(t.getId(), t);
    }

    public Transaction findById(String id){
        return this.transactions.getOrDefault(id, null);
    }

    public void update(String id, Transaction transaction){
        this.transactions.put(id, transaction);
    }
}
