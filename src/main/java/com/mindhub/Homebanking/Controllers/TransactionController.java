package com.mindhub.Homebanking.Controllers;

import com.itextpdf.text.DocumentException;
import com.mindhub.Homebanking.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;


@Transactional
@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;


    @PostMapping("/api/clients/current/transactions")
    public ResponseEntity<Object> transaction(Authentication authentication, @RequestParam double amount, @RequestParam String description, @RequestParam String accountOriginNumber, @RequestParam String destinationAccountNumber) {
        return transactionService.transaction(authentication, amount, description, accountOriginNumber, destinationAccountNumber);
    }

    @PostMapping("/api/clients/current/export-pdf")
    public ResponseEntity<Object> ExportingToPDF(HttpServletResponse response, Authentication authentication, @RequestParam String accNumber, @RequestParam String dateIni, @RequestParam String dateEnd) throws DocumentException, IOException {
        return transactionService.ExportingToPDF(response, authentication, accNumber, dateIni, dateEnd);
    }
}