package com.taskflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.directory(System.getProperty("user.dir")) // 현재 작업 디렉토리 (프로젝트 루트) 설정
				.load();

		// PostgreSQL DataSource 설정
		System.setProperty("spring.datasource.url", dotenv.get("POSTGRES_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("POSTGRES_USER"));
		System.setProperty("spring.datasource.password", dotenv.get("POSTGRES_PASSWORD"));

		// Redis 설정
		System.setProperty("spring.data.redis.host", dotenv.get("REDIS_HOST"));
		System.setProperty("spring.data.redis.port", dotenv.get("REDIS_PORT"));

		// MongoDB 설정
		System.setProperty("spring.data.mongodb.uri", dotenv.get("MONGODB_URI"));

		// JWT 설정
		System.setProperty("jwt.access-token.secret-key", dotenv.get("ACCESS_TOKEN_SECRET"));
		System.setProperty("jwt.refresh-token.secret-key", dotenv.get("REFRESH_TOKEN_SECRET"));

		// OAuth Google 설정
		System.setProperty("spring.security.oauth2.client.registration.google.client-id",
				dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-secret",
				dotenv.get("GOOGLE_CLIENT_SECRET"));

		// GMail 설정
		System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));

		SpringApplication.run(ApiApplication.class, args);
	}
}
