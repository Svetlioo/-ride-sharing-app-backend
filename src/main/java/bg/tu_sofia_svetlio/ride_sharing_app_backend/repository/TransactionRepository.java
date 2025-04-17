package bg.tu_sofia_svetlio.ride_sharing_app_backend.repository;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Transaction;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByRideId(String rideId);

    @Aggregation(pipeline = {
            "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 }, 'status': 'COMPLETED' } }",
            "{ $group: { _id: null, totalRevenue: { $sum: '$amount' } } }"
    })
    RevenueResult getTotalRevenueInPeriod(LocalDateTime startDate, LocalDateTime endDate);

    @Aggregation(pipeline = {
            "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: '$status', count: { $sum: 1 } } }"
    })
    List<StatusCount> countTransactionsByStatus(LocalDateTime startDate, LocalDateTime endDate);

    class StatusCount {
        private String status;
        private long count;

        public String getStatus() { return status; }
        public long getCount() { return count; }
    }

    class RevenueResult {
        private BigDecimal totalRevenue;

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}