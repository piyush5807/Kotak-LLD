package com.example.demo_kotak;

import com.example.demo_kotak.account.models.Account;
import com.example.demo_kotak.account.services.AccountService;
import com.example.demo_kotak.auth.AuthenticationService;
import com.example.demo_kotak.payment.services.PaymentService;
import com.example.demo_kotak.transaction.dtos.CashWithdrawlRequest;
import com.example.demo_kotak.transaction.models.Transaction;
import com.example.demo_kotak.transaction.models.TransactionType;
import com.example.demo_kotak.transaction.repositories.TransactionRepository;
import com.example.demo_kotak.transaction.services.CashWithdrawlManager;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CashWithdrawlManagerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CashWithdrawlManager cashWithdrawalManager;

    @Test
    public void testInitiateTransaction_Success() {
        CashWithdrawlRequest request = new CashWithdrawlRequest();
        Account account = new Account();

        account.setId(12345l);
        request.setAccount(account);
        request.setAmount(1000L);
        request.setSecurityKey("secure-key");

        when(accountService.getBalance(anyLong())).thenReturn(5000L);
        when(paymentService.withdraw(anyLong(), anyLong())).thenReturn("payment-id");

        String result = cashWithdrawalManager.initiateTransaction(request);
        verify(transactionRepository, times(1)).create(any(Transaction.class));
        verify(transactionRepository, times(1)).update(anyString(), any(Transaction.class));
    }

    @Test(expected = Exception.class)
    public void testInitiateTransaction_InsufficientBalance() {
        // Arrange
        CashWithdrawlRequest request = new CashWithdrawlRequest();
        Account account = new Account();
        account.setId(12345L);
        request.setAccount(account);
        request.setAmount(10000L);
        request.setSecurityKey("secure-key");

        when(accountService.getBalance(anyLong())).thenReturn(5000L);
        cashWithdrawalManager.initiateTransaction(request);

        verify(transactionRepository, times(1)).update(anyString(), any(Transaction.class));

    }
    
    @Test(expected = Exception.class)
    public void testInitiateTransaction_AuthenticationFailure() {
        // Arrange
        CashWithdrawlRequest request = new CashWithdrawlRequest();
        Account account = new Account();
        account.setId(12345L);
        request.setAccount(account);
        request.setAmount(1000L);
        request.setSecurityKey("secure-key");

        doThrow(new RuntimeException("Authentication failed"))
                .when(authenticationService).authenticate(anyLong(), anyString());

        cashWithdrawalManager.initiateTransaction(request);

        verify(transactionRepository, times(1)).update(anyString(), any(Transaction.class));

    }
}
