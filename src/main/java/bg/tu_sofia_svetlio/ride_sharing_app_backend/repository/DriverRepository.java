package bg.tu_sofia_svetlio.ride_sharing_app_backend.repository;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Driver;
import com.mongodb.client.model.geojson.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    
    @Query("{'available': true, 'currentLocation.coordinates': {$near: {$geometry: ?0, $maxDistance: ?1}}}")
    List<Driver> findAvailableDriversNearLocation(GeoJsonPoint location, double maxDistanceInMeters);
}
