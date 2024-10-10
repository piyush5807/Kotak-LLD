package com.example.demo_kotak.transaction.controllers;

import com.example.demo_kotak.transaction.dtos.CashWithdrawlRequest;
import com.example.demo_kotak.transaction.models.Transaction;
import com.example.demo_kotak.transaction.services.CashWithdrawlManager;
import com.example.demo_kotak.transaction.services.TransactionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionManager transactionManager;

    public TransactionController() {
        this.transactionManager = new CashWithdrawlManager();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody CashWithdrawlRequest request) {
        try {
            String transactionId = this.transactionManager.initiateTransaction(request);
            return ResponseEntity.ok(transactionId);
        } catch (Exception e) {
            //TODO: Map e to corresponding status code
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("transactionId") String transactionId){
        try {
            return ResponseEntity.ok(this.transactionManager.getTransactionById(transactionId));
        } catch (Exception e) {
            //TODO: Map e to corresponding status code
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
