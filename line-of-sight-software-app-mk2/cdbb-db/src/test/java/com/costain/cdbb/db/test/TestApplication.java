package com.costain.cdbb.db.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.costain.cdbb"})
@EntityScan("com.costain.cdbb.model")
@EnableJpaRepositories("com.costain.cdbb.repositories")
public class TestApplication {
}
