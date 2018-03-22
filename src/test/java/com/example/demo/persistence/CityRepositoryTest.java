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
package com.example.demo.persistence;

import com.example.demo.domain.City;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration")
public class CityRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CityRepository repository;

    @Test
    public void findAll() throws Exception {
        Page<City> all = repository.findAll(new PageRequest(0, 1));

        assertThat(all.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void findByNameContainingAndCountryContainingAllIgnoringCase() throws Exception {
        PageRequest onePageRequest = new PageRequest(0, 1);
        Page<City> result = repository.findByNameContainingAndCountryContainingAllIgnoringCase("Tel Aviv", "Israel", onePageRequest);

        assertThat(result.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void findByNameAndCountryAllIgnoringCase() throws Exception {

        City result = repository.findByNameAndCountryAllIgnoringCase("Tel Aviv", "Israel");

        assertThat(result).isNotNull();
    }

}