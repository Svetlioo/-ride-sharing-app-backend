package bg.tu_sofia_svetlio.ride_sharing_app_backend.service;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Driver;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Location;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> findNearbyAvailableDrivers(Location location, double maxDistanceInMeters) {
        GeoJsonPoint point = location.getCoordinates();
        return driverRepository.findAvailableDriversNearLocation(point, maxDistanceInMeters);
    }

    public Driver updateDriverLocation(String driverId, Location location) {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);

        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setCurrentLocation(location);
            return driverRepository.save(driver);
        }

        throw new RuntimeException("Driver not found with ID: " + driverId);
    }

    public Driver updateDriverAvailability(String driverId, boolean available) {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);

        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setAvailable(available);
            return driverRepository.save(driver);
        }

        throw new RuntimeException("Driver not found with ID: " + driverId);
    }

    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}

