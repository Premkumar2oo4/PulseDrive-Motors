package com.pulsedrive.controller;

import com.pulsedrive.dto.CouponRequestDTO;
import com.pulsedrive.dto.CouponResponseDTO;
import com.pulsedrive.service.CouponService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(
            CouponService couponService) {

        this.couponService =
                couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponseDTO>
    createCoupon(
           @Valid @RequestBody CouponRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        couponService
                                .createCoupon(dto)
                );
    }

    @GetMapping
    public ResponseEntity<List<CouponResponseDTO>>
    getAllCoupons() {

        return ResponseEntity.ok(
                couponService.getAllCoupons()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponseDTO>
    getCouponById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                couponService
                        .getCouponById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponseDTO>
    updateCoupon(
            @PathVariable Long id,
           @Valid @RequestBody CouponRequestDTO dto) {

        return ResponseEntity.ok(
                couponService
                        .updateCoupon(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteCoupon(
            @PathVariable Long id) {

        couponService.deleteCoupon(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}