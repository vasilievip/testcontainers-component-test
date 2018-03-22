package com.example.demo.context;

import com.example.demo.persistence.CityRepository;
import com.example.demo.service.CityService;
import com.example.demo.service.DefaultCityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfiguration {

    @Bean
    public CityService cityService(CityRepository cityRepository) {
        return new DefaultCityService(cityRepository);
    }
}
