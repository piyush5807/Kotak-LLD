package com.example.demo_kotak.transaction.services;

import com.example.demo_kotak.account.models.Account;
import com.example.demo_kotak.account.services.AccountService;
import com.example.demo_kotak.auth.AuthenticationService;
import com.example.demo_kotak.payment.services.PaymentService;
import com.example.demo_kotak.transaction.dtos.CashWithdrawlRequest;
import com.example.demo_kotak.transaction.models.Transaction;
import com.example.demo_kotak.transaction.models.TransactionStatus;
import com.example.demo_kotak.transaction.models.TransactionType;
import com.example.demo_kotak.transaction.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.http.HTTPException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CashWithdrawlManager implements TransactionManager<CashWithdrawlRequest> {

    private static Logger logger = LoggerFactory.getLogger(CashWithdrawlManager.class);

    private AuthenticationService authenticationService;
    private AccountService accountService;
    private PaymentService paymentService;

    private TransactionRepository transactionRepository;

    public CashWithdrawlManager(){
        this.accountService = new AccountService();
        this.paymentService = new PaymentService();
        this.authenticationService = new AuthenticationService();
        this.transactionRepository = new TransactionRepository();
    }

    @Override
    public String initiateTransaction(CashWithdrawlRequest transactionRequest) {

        // 1. authenticate user before checking further
        try {
            authenticationService.authenticate(transactionRequest.getAccount().getId(), transactionRequest.getSecurityKey());
        }catch (Exception e){
            logger.warn("Authentication failed: error - {}", e);
            throw e;
        }

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .initiatedBy(transactionRequest.getAccount())
                .type(TransactionType.CASH_WITHDRAWAL)
                .transactionStatus(TransactionStatus.PENDING)
                .meta(new HashMap<>())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        this.transactionRepository.create(transaction);

        // 2. user should have balance in the account
        try {
            Account account = transactionRequest.getAccount();
            Long balance = this.accountService.getBalance(account.getId());
            if(balance < transactionRequest.getAmount()){
                logger.warn("Insufficient balance: balance - {}, amountRequested - {}", balance, transactionRequest.getAmount());
                throw new HTTPException(400);
            }
        }catch (Exception e){
            logger.warn("Balance retrieval failed: error - {}", e);
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setUpdatedAt(new Date());
            this.transactionRepository.update(transaction.getId(), transaction);
            throw e;
        }

        // 3. downstream payment service call
        try {
            String paymentId = this.paymentService.withdraw(transactionRequest.getAccount().getId(), transactionRequest.getAmount());
            Map<Object, Object> meta = transaction.getMeta();
            meta.put("paymentId", paymentId);
        }catch(Exception e){
            logger.warn("payment failed: error - {}", e);
            transaction.setUpdatedAt(new Date());
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            this.transactionRepository.update(transaction.getId(), transaction);
            throw e;
        }

        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setUpdatedAt(new Date());
        this.transactionRepository.update(transaction.getId(), transaction);

        return transaction.getId();
    }

    public Transaction getTransactionById(String id){
        return this.transactionRepository.findById(id);
    }
}
