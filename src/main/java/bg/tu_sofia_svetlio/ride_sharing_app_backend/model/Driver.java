package bg.tu_sofia_svetlio.ride_sharing_app_backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "drivers")
public class Driver extends User {
    private String licenseNumber;
    private String vehicleModel;
    private String vehiclePlate;
    private Location currentLocation;
    private boolean available;
    private double rating;
    private int completedRides;

    public Driver() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(int completedRides) {
        this.completedRides = completedRides;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Driver driver = (Driver) o;
        return available == driver.available &&
                Double.compare(driver.rating, rating) == 0 &&
                completedRides == driver.completedRides &&
                Objects.equals(licenseNumber, driver.licenseNumber) &&
                Objects.equals(vehicleModel, driver.vehicleModel) &&
                Objects.equals(vehiclePlate, driver.vehiclePlate) &&
                Objects.equals(currentLocation, driver.currentLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licenseNumber, vehicleModel, vehiclePlate,
                currentLocation, available, rating, completedRides);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "licenseNumber='" + licenseNumber + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", currentLocation=" + currentLocation +
                ", available=" + available +
                ", rating=" + rating +
                ", completedRides=" + completedRides +
                "} " + super.toString();
    }
}

