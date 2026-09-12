package com.pulsedrive.repository;

import com.pulsedrive.entity.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealershipRepository
        extends JpaRepository<Dealership, Long> {

    List<Dealership> findByCityIgnoreCase(String city);

    List<Dealership> findByStateIgnoreCase(String state);
}