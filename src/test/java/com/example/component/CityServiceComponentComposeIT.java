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

import com.example.demo.domain.City;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("componentCompose")
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        classes = CityServiceComponentComposeIT.TestConfig.class
)
public class CityServiceComponentComposeIT {

    public static final File composeFile = new File("src/test/resources/docker-compose.yml");
    public static final String HTTP_LOCALHOST_8080_ACTUATOR_HEALTH = "http://localhost:8080/actuator/health";

    @ClassRule
    public static DockerComposeContainer stageEnvironment = new DockerComposeContainer(composeFile)
            .withPull(false)
            .withLocalCompose(true)
            .withExposedService("myservice", 8080)
            .withExposedService("mariadb", 3306);

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Before
    public void setUp() throws Exception {
        await().atMost(30, SECONDS)
                .pollInterval(5, SECONDS)
                .until(this::serviceIsUp);
    }

    @Test
    public void shouldReturnAllCities() throws Exception {

        List<City> cities = restTemplate.getForObject("http://localhost:8080/", List.class);

        assertThat(cities).isNotNull();
        assertThat(cities.size()).isGreaterThan(0);

        log.trace("------------------------");
        log.trace(cities.toString());
        log.trace("------------------------");
    }

    private boolean serviceIsUp() {
        try {
            ResponseEntity<Map> health = restTemplate.getForEntity(HTTP_LOCALHOST_8080_ACTUATOR_HEALTH, Map.class);
            assertThat(health.getStatusCode().is2xxSuccessful());
            assertThat(health.getBody()).isNotNull().containsKey("status");
            assertThat(health.getBody().get("status")).isEqualTo("UP");
        } catch (Exception e) {
            log.debug("Service is not UP!");
            return false;
        }
        return true;
    }

    @Configuration
    @SpringBootConfiguration
    public static class TestConfig {
    }
}
