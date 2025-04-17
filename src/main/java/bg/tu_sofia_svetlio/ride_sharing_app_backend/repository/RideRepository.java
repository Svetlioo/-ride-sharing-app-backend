package bg.tu_sofia_svetlio.ride_sharing_app_backend.repository;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Driver;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Ride;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Rider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    
    List<Ride> findByDriverId(String driverId);
    
    List<Ride> findByRiderId(String riderId);
}
