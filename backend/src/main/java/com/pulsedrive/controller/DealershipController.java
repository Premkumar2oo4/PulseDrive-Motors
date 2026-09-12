package com.pulsedrive.controller;

import com.pulsedrive.dto.DealershipRequestDTO;
import com.pulsedrive.dto.DealershipResponseDTO;
import com.pulsedrive.service.DealershipService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealerships")
public class DealershipController {

    private final DealershipService dealershipService;

    public DealershipController(
            DealershipService dealershipService) {

        this.dealershipService =
                dealershipService;
    }

    @GetMapping
    public ResponseEntity<List<DealershipResponseDTO>>
    getAllDealerships() {

        return ResponseEntity.ok(
                dealershipService.getAllDealerships()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO>
    getDealershipById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                dealershipService
                        .getDealershipById(id)
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<DealershipResponseDTO>>
    getByCity(
            @PathVariable String city) {

        return ResponseEntity.ok(
                dealershipService.getByCity(city)
        );
    }

    @PostMapping
    public ResponseEntity<DealershipResponseDTO>
    createDealership(
           @Valid @RequestBody DealershipRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        dealershipService
                                .createDealership(dto)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO>
    updateDealership(
            @PathVariable Long id,
          @Valid  @RequestBody DealershipRequestDTO dto) {

        return ResponseEntity.ok(
                dealershipService
                        .updateDealership(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealership(
            @PathVariable Long id) {

        dealershipService.deleteDealership(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}