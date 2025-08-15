package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.repository.AmenityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public List<String> findAllAmenities() {

        return amenityRepository.findAll()
                .stream()
                .map(Amenity::getName)
                .toList();

    }

}
