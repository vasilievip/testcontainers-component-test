package com.example.demo.service;

import com.example.demo.domain.City;
import com.example.demo.persistence.CityRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCityServiceTest {

    @Mock
    CityRepository cityRepository;
    CityService cityService;

    @Before
    public void setUp() throws Exception {
        Mockito.reset(cityRepository);
        cityService = new DefaultCityService(cityRepository);
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {

        PageImpl<City> emptyList = new PageImpl<>(Collections.emptyList());

        when(cityRepository.findAll(any(Pageable.class)))
                .thenReturn(emptyList);

        CitySearchCriteria emptyCriteria = new CitySearchCriteria();
        PageRequest oneElementPage = new PageRequest(0, 1);

        Page<City> cities = cityService.findCities(emptyCriteria, oneElementPage);

        assertThat(cities.getContent()).isNotNull();
        assertThat(cities.getContent()).isEmpty();

    }

    @Test
    public void shouldGetCityByName() throws Exception {
        City example = City.builder().name("example").build();
        when(cityRepository.findByNameAndCountryAllIgnoringCase("name", "country"))
                .thenReturn(example);

        Optional<City> city = cityService.getCity("name", "country");

        assertThat(city).isNotNull();
        assertThat(city.isPresent()).isTrue();
        assertThat(city.get().getName()).isEqualTo("example");
    }

}