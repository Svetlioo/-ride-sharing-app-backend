package bg.tu_sofia_svetlio.ride_sharing_app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class RideSharingAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideSharingAppBackendApplication.class, args);
	}

}
