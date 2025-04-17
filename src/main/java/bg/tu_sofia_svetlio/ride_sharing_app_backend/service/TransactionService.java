package bg.tu_sofia_svetlio.ride_sharing_app_backend.service;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Ride;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Transaction;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.RideRepository;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RideRepository rideRepository;

    public void processPayment(String rideId) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);

        if (optionalRide.isEmpty()) {
            throw new RuntimeException("Ride not found with ID: " + rideId);
        }

        Ride ride = optionalRide.get();

        if (ride.getStatus() != Ride.RideStatus.COMPLETED) {
            throw new RuntimeException("Cannot process payment for a ride that is not completed");
        }

        Transaction transaction = new Transaction();
        transaction.setRide(ride);
        transaction.setAmount(ride.getFare());
        transaction.setPaymentMethod(ride.getRider().getPaymentMethod());
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionReference(generateTransactionReference());

        transactionRepository.save(transaction);
    }

    public List<Transaction> findByRideId(String rideId) {
        return transactionRepository.findByRideId(rideId);
    }

    public BigDecimal calculateRevenueInPeriod(LocalDateTime start, LocalDateTime end) {
        TransactionRepository.RevenueResult result = transactionRepository.getTotalRevenueInPeriod(start, end);
        return result != null && result.getTotalRevenue() != null ? result.getTotalRevenue() : BigDecimal.ZERO;
    }

    public Map<String, Long> getTransactionStatsByStatus(LocalDateTime start, LocalDateTime end) {
        List<TransactionRepository.StatusCount> counts = transactionRepository.countTransactionsByStatus(start, end);
        Map<String, Long> statsByStatus = new HashMap<>();

        for (TransactionRepository.StatusCount count : counts) {
            String status = count.getStatus();
            statsByStatus.put(status != null ? status : "UNKNOWN", count.getCount());
        }

        return statsByStatus;
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}