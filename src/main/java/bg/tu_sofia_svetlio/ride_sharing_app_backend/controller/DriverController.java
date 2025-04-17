package bg.tu_sofia_svetlio.ride_sharing_app_backend.controller;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Driver;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Location;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Driver Management", description = "Location tracking and driver availability")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    @Operation(summary = "Create driver", description = "Register a new driver in the system")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        driver.setAvailable(true);
        driver.setRating(5.0);
        driver.setCompletedRides(0);

        Driver savedDriver = driverService.createDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby drivers", description = "Finds available drivers within a specified distance")
    public ResponseEntity<List<Driver>> findNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5000") double maxDistanceInMeters) {

        Location location = new Location();
        location.setCoordinates(new GeoJsonPoint(longitude, latitude));

        List<Driver> drivers = driverService.findNearbyAvailableDrivers(location, maxDistanceInMeters);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update driver location", description = "Updates the current location of a driver")
    public ResponseEntity<Driver> updateDriverLocation(
            @PathVariable String id,
            @RequestBody LocationRequest request) {

        Location location = new Location();
        location.setCoordinates(new GeoJsonPoint(request.longitude(), request.latitude()));
        if (request.address() != null) {
            location.setAddress(request.address());
        }

        Driver driver = driverService.updateDriverLocation(id, location);
        return ResponseEntity.ok(driver);
    }

    @PutMapping("/{id}/availability")
    @Operation(summary = "Update driver availability", description = "Sets a driver as available or unavailable")
    public ResponseEntity<Driver> updateDriverAvailability(
            @PathVariable String id,
            @RequestBody AvailabilityRequest request) {

        Driver driver = driverService.updateDriverAvailability(id, request.available());
        return ResponseEntity.ok(driver);
    }

    public record LocationRequest(double latitude, double longitude, String address) {}
    public record AvailabilityRequest(boolean available) {}
}
