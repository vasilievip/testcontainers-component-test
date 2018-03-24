/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.component;

import com.example.demo.DemoApplication;
import com.example.demo.domain.City;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , classes = DemoApplication.class
        , properties = "embedded.mariadb.enabled=false")
@ActiveProfiles("componentSpring")
@AutoConfigureTestDatabase
public class CityServiceComponentSpringIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnAllCities() throws Exception {
        List<City> cities = restTemplate.getForObject("/", List.class);

        assertThat(cities).isNotNull();
        assertThat(cities.size()).isGreaterThan(0);

        log.error("------------------------");
        log.error(cities.toString());
        log.error("------------------------");

    }

    @Configuration
    public static class Config {

        @Bean
        public String embeddedMariaDb() {
            return "embeddedMariaDb";
        }
    }
}
