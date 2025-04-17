package bg.tu_sofia_svetlio.ride_sharing_app_backend.repository;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Rider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiderRepository extends MongoRepository<Rider, String> {
}

