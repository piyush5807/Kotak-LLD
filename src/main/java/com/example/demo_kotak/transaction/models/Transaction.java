package com.example.demo_kotak.transaction.models;

import com.example.demo_kotak.account.models.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
public class Transaction {

    private String id;
    private TransactionType type;
    private Date createdAt;
    private Date updatedAt;
    private Map<Object, Object> meta;
    private Account initiatedBy;
    private TransactionStatus transactionStatus;

}
