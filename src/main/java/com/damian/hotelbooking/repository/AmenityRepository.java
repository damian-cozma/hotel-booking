package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
