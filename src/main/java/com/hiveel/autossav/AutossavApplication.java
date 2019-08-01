package com.hiveel.autossav;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.hiveel")
@EnableFeignClients(basePackages = "com.hiveel")
@MapperScan("com.hiveel.autossav.dao")
public class AutossavApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutossavApplication.class, args);
	}

}
