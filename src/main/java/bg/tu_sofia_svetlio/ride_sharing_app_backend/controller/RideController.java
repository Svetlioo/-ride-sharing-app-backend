package bg.tu_sofia_svetlio.ride_sharing_app_backend.controller;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Location;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Ride;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.service.RideService;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Ride Management", description = "Ride requests, status updates, and completion")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Request ride", description = "Request a ride with pickup and destination locations")
    public ResponseEntity<Ride> requestRide(@RequestBody RideRequest request) {
        Location pickup = new Location();
        pickup.setCoordinates(new GeoJsonPoint(request.pickupLongitude(), request.pickupLatitude()));
        if (request.pickupAddress() != null) {
            pickup.setAddress(request.pickupAddress());
        }

        Location destination = new Location();
        destination.setCoordinates(new GeoJsonPoint(request.destLongitude(), request.destLatitude()));
        if (request.destAddress() != null) {
            destination.setAddress(request.destAddress());
        }

        Ride ride = rideService.requestRide(request.riderId(), pickup, destination);
        return ResponseEntity.status(HttpStatus.CREATED).body(ride);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride by ID", description = "Get ride details by ID")
    public ResponseEntity<Ride> getRideById(@PathVariable String id) {
        return rideService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get driver's rides", description = "Get all rides for a driver")
    public ResponseEntity<List<Ride>> getDriverRides(@PathVariable String driverId) {
        List<Ride> rides = rideService.findRidesByDriverId(driverId);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/rider/{riderId}")
    @Operation(summary = "Get rider's rides", description = "Get all rides for a rider")
    public ResponseEntity<List<Ride>> getRiderRides(@PathVariable String riderId) {
        List<Ride> rides = rideService.findRidesByRiderId(riderId);
        return ResponseEntity.ok(rides);
    }

    @PostMapping("/{id}/assign-driver")
    @Operation(summary = "Assign driver", description = "Assign a driver to a requested ride")
    public ResponseEntity<Ride> assignDriverToRide(
            @PathVariable String id,
            @RequestBody DriverAssignmentRequest request) {

        Ride ride = rideService.assignDriverToRide(id, request.driverId());
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update ride status", description = "Update ride status (PICKED_UP, COMPLETED, CANCELLED)")
    public ResponseEntity<Ride> updateRideStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request) {

        Ride ride = rideService.updateRideStatus(id, request.status());

        if (ride.getStatus() == Ride.RideStatus.COMPLETED) {
            transactionService.processPayment(id);
        }

        return ResponseEntity.ok(ride);
    }

    @GetMapping("/{id}/price")
    @Operation(summary = "Get ride price", description = "Calculate the price for a ride")
    public ResponseEntity<BigDecimal> getRidePrice(@PathVariable String id) {
        BigDecimal price = rideService.calculateRidePrice(id);
        return ResponseEntity.ok(price);
    }

    public record RideRequest(
            String riderId,
            double pickupLatitude,
            double pickupLongitude,
            String pickupAddress,
            double destLatitude,
            double destLongitude,
            String destAddress) {}

    public record StatusUpdateRequest(Ride.RideStatus status) {}

    public record DriverAssignmentRequest(String driverId) {}
}