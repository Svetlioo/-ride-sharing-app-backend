package bg.tu_sofia_svetlio.ride_sharing_app_backend.controller;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Transaction;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "Payment processing and reporting")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get ride transactions", description = "Get all transactions for a specific ride")
    public ResponseEntity<List<Transaction>> getTransactionsByRideId(@PathVariable String rideId) {
        List<Transaction> transactions = transactionService.findByRideId(rideId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/stats/revenue")
    @Operation(summary = "Get revenue stats", description = "Calculate total revenue in a given period")
    public ResponseEntity<BigDecimal> getRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        BigDecimal revenue = transactionService.calculateRevenueInPeriod(start, end);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/stats/status")
    @Operation(summary = "Get transaction status stats", description = "Get transaction counts by status")
    public ResponseEntity<Map<String, Long>> getTransactionStatusStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Map<String, Long> stats = transactionService.getTransactionStatsByStatus(start, end);
        return ResponseEntity.ok(stats);
    }
}
