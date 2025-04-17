package bg.tu_sofia_svetlio.ride_sharing_app_backend.service;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Driver;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Location;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Ride;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Rider;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.DriverRepository;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.RideRepository;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    private static final BigDecimal BASE_FARE = BigDecimal.valueOf(2.5);
    private static final BigDecimal RATE_PER_KM = BigDecimal.valueOf(1.2);

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    public Ride requestRide(String riderId, Location pickup, Location destination) {
        Optional<Rider> optionalRider = riderRepository.findById(riderId);

        if (optionalRider.isEmpty()) {
            throw new RuntimeException("Rider not found with ID: " + riderId);
        }

        Ride ride = new Ride();
        ride.setRider(optionalRider.get());
        ride.setPickupLocation(pickup);
        ride.setDestinationLocation(destination);
        ride.setRequestTime(LocalDateTime.now());
        ride.setStatus(Ride.RideStatus.REQUESTED);

        BigDecimal distance = calculateDistance(pickup, destination);
        ride.setDistance(distance);

        BigDecimal fare = calculateFare(distance);
        ride.setFare(fare);

        return rideRepository.save(ride);
    }

    public Optional<Ride> findById(String rideId) {
        return rideRepository.findById(rideId);
    }

    public List<Ride> findRidesByDriverId(String driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> findRidesByRiderId(String riderId) {
        return rideRepository.findByRiderId(riderId);
    }

    public Ride assignDriverToRide(String rideId, String driverId) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);

        if (optionalRide.isEmpty()) {
            throw new RuntimeException("Ride not found with ID: " + rideId);
        }

        if (optionalDriver.isEmpty()) {
            throw new RuntimeException("Driver not found with ID: " + driverId);
        }

        Driver driver = optionalDriver.get();
        Ride ride = optionalRide.get();

        if (!driver.isAvailable()) {
            throw new RuntimeException("Driver is not available");
        }

        if (ride.getStatus() != Ride.RideStatus.REQUESTED) {
            throw new RuntimeException("Ride is not in REQUESTED status");
        }

        ride.setDriver(driver);
        ride.setStatus(Ride.RideStatus.ACCEPTED);

        driver.setAvailable(false);
        driverRepository.save(driver);

        return rideRepository.save(ride);
    }

    public Ride updateRideStatus(String rideId, Ride.RideStatus status) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);

        if (optionalRide.isEmpty()) {
            throw new RuntimeException("Ride not found with ID: " + rideId);
        }

        Ride ride = optionalRide.get();

        switch (status) {
            case PICKED_UP:
                if (ride.getStatus() != Ride.RideStatus.ACCEPTED) {
                    throw new RuntimeException("Ride must be ACCEPTED before it can be PICKED_UP");
                }
                ride.setPickupTime(LocalDateTime.now());
                break;
            case COMPLETED:
                if (ride.getStatus() != Ride.RideStatus.PICKED_UP) {
                    throw new RuntimeException("Ride must be PICKED_UP before it can be COMPLETED");
                }
                ride.setCompletionTime(LocalDateTime.now());

                if (ride.getDriver() != null) {
                    Driver driver = ride.getDriver();
                    driver.setAvailable(true);
                    driver.setCompletedRides(driver.getCompletedRides() + 1);
                    driverRepository.save(driver);
                }
                break;
            case CANCELLED:
                if (ride.getStatus() == Ride.RideStatus.ACCEPTED && ride.getDriver() != null) {
                    Driver driver = ride.getDriver();
                    driver.setAvailable(true);
                    driverRepository.save(driver);
                }
                break;
        }

        ride.setStatus(status);
        return rideRepository.save(ride);
    }

    public BigDecimal calculateRidePrice(String rideId) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);

        if (optionalRide.isEmpty()) {
            throw new RuntimeException("Ride not found with ID: " + rideId);
        }

        Ride ride = optionalRide.get();
        return calculateFare(ride.getDistance());
    }

    private BigDecimal calculateFare(BigDecimal distance) {
        return BASE_FARE.add(RATE_PER_KM.multiply(distance)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDistance(Location start, Location end) {
        // Haversine formula to calculate distance between two coordinates
        double startLat = start.getCoordinates().getY();
        double startLng = start.getCoordinates().getX();
        double endLat = end.getCoordinates().getY();
        double endLng = end.getCoordinates().getX();

        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(endLat - startLat);
        double lngDistance = Math.toRadians(endLng - startLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return BigDecimal.valueOf(R * c).setScale(2, RoundingMode.HALF_UP);
    }
}
