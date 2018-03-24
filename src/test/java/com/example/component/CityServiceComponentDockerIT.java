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
import com.github.dockerjava.api.model.Link;
import com.playtika.test.common.utils.ContainerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.HostPortWaitStrategy;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("componentDocker")
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = WebEnvironment.MOCK,
        classes = CityServiceComponentDockerIT.TestConfig.class
)
public class CityServiceComponentDockerIT {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void shouldReturnAllCities() throws Exception {
        List<City> cities = restTemplate.getForObject("http://localhost:8080/", List.class);

        assertThat(cities).isNotNull();
        assertThat(cities.size()).isGreaterThan(0);

        log.error("------------------------");
        log.error(cities.toString());
        log.error("------------------------");
    }

    @Configuration
    @SpringBootConfiguration
    public static class TestConfig {

        @Value("${docker.image.name}")
        String imageName;

        @Value("${container.runArgs}")
        String containerRunArgs;

        @Value("${container.javaOpts}")
        String containerJavaOpts;

        @Bean
        FixedHostPortGenericContainer application(GenericContainer mariadb) {

            String mariaDbHostname = ContainerUtils.getContainerHostname(mariadb);
            Link mariadbLink = new Link(mariaDbHostname, "mariadb");

            FixedHostPortGenericContainer app = new FixedHostPortGenericContainer<>(imageName)
                    .withEnv("JAVA_OPTS", containerJavaOpts)
                    .withEnv("RUN_ARGS", containerRunArgs)
                    .withLogConsumer(containerLogsConsumer())
                    .waitingFor(new HostPortWaitStrategy())
                    .withExposedPorts(8080)
                    .withCreateContainerCmdModifier(cmd -> cmd.withLinks(mariadbLink))
                    .withFixedExposedPort(8080, 8080);
            app.start();
            return app;
        }

        private Consumer<OutputFrame> containerLogsConsumer() {
            return (OutputFrame outputFrame) -> {
                switch (outputFrame.getType()) {
                    case STDERR:
                        log.error(outputFrame.getUtf8String());
                        break;
                    case STDOUT:
                    case END:
                        log.info(outputFrame.getUtf8String());
                        break;
                    default:
                        log.info(outputFrame.getUtf8String());
                        break;
                }
            };
        }
    }
}
