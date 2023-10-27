package com.mindhub.Homebanking.Services;

import com.itextpdf.text.DocumentException;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<Transaction> findByCreatedBetweenDates(Client client, String string, LocalDateTime date, LocalDateTime date2);
    ResponseEntity<Object> transaction(Authentication authentication, @RequestParam double amount, @RequestParam String description, @RequestParam String accountOriginNumber, @RequestParam String destinationAccountNumber);
    ResponseEntity<Object> ExportingToPDF(HttpServletResponse response, Authentication authentication, @RequestParam String accNumber, @RequestParam String dateIni, @RequestParam String dateEnd) throws DocumentException, IOException;
}
