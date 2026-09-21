package jln.microservicos.p1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class P1SensatoJlnApplication {

	public static void main(String[] args) {
		SpringApplication.run(P1SensatoJlnApplication.class, args);
	}

}
