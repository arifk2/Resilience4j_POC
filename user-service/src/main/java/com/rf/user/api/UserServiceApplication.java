package com.rf.user.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.rf.user.api.dto.OrderDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@SpringBootApplication
@RestController
@RequestMapping("/user-service")
public class UserServiceApplication {
	private static final String BASE_URL = "http://localhost:9191/orders";
	private static final String USER_SERVICE = "userService";

	@Autowired
	@Lazy
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@GetMapping("/displayOrders")
	@CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
	public List<OrderDTO> displayOrders(@RequestParam("category") String category) {
		String url = category == null ? BASE_URL : BASE_URL + "/" + category;
		return restTemplate.getForObject(url, ArrayList.class);
	}

	private List<OrderDTO> getAllAvailableProducts(Exception e) {
		return Stream.of(new OrderDTO("LED", "electronics", "white", 20000),
				new OrderDTO("Kurta Pajama", "clothes", "black", 999), new OrderDTO("Jeans", "clothes", "blue", 1999),
				new OrderDTO("Washing Machine", "electronics", "gray", 50000)).collect(Collectors.toList());
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
