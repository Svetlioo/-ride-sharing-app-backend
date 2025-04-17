package bg.tu_sofia_svetlio.ride_sharing_app_backend.controller;

import bg.tu_sofia_svetlio.ride_sharing_app_backend.model.Rider;
import bg.tu_sofia_svetlio.ride_sharing_app_backend.repository.RiderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/riders")
@Tag(name = "Rider Management", description = "Creation and management of riders")
public class RiderController {

    @Autowired
    private RiderRepository riderRepository;

    @PostMapping
    @Operation(summary = "Create rider", description = "Register a new rider in the system")
    public ResponseEntity<Rider> createRider(@RequestBody Rider rider) {
        rider.setIsVerified(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(riderRepository.save(rider));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rider by ID", description = "Retrieve a rider's information by ID")
    public ResponseEntity<Rider> getRiderById(@PathVariable String id) {
        return riderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}