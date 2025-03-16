package com.cinema;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.cinema.application",
    "com.cinema.domain"
})
// @SpringBootApplication(scanBasePackages="") 사용시
// 기존 ComponentScan 이 override 되는 문제로 Config 클래스에서 명시하도록 함
public class ServerConfig {
}
