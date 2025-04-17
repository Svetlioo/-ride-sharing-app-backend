package bg.tu_sofia_svetlio.ride_sharing_app_backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "riders")
public class Rider extends User {
    private Integer rating;
    private String paymentMethod;
    private Boolean isVerified;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
}
