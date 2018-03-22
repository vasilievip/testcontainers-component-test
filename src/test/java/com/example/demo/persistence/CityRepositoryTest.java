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