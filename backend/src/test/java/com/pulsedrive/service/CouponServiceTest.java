package com.pulsedrive.service;

import com.pulsedrive.entity.Coupon;
import com.pulsedrive.repository.CouponRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon percentageCoupon;

    @BeforeEach
    void setUp() {

        percentageCoupon = new Coupon();

        percentageCoupon.setCode("PULSE10");
        percentageCoupon.setDiscountType("PERCENTAGE");
        percentageCoupon.setDiscountValue(10.0);
        percentageCoupon.setMinimumAmount(50000.0);
        percentageCoupon.setExpiryDate(
                LocalDate.now().plusDays(30)
        );
        percentageCoupon.setActive(true);
    }

    @Test
    void shouldCalculatePercentageDiscount() {

        when(
                couponRepository
                        .findByCodeIgnoreCase("PULSE10")
        ).thenReturn(
                Optional.of(percentageCoupon)
        );

        Double discount =
                couponService.calculateDiscount(
                        "PULSE10",
                        100000.0
                );

        assertEquals(
                10000.0,
                discount
        );
    }

    @Test
    void shouldRejectInactiveCoupon() {

        percentageCoupon.setActive(false);

        when(
                couponRepository
                        .findByCodeIgnoreCase("PULSE10")
        ).thenReturn(
                Optional.of(percentageCoupon)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                couponService
                                        .calculateDiscount(
                                                "PULSE10",
                                                100000.0
                                        )
                );

        assertEquals(
                "Coupon is inactive",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectExpiredCoupon() {

        percentageCoupon.setExpiryDate(
                LocalDate.now().minusDays(1)
        );

        when(
                couponRepository
                        .findByCodeIgnoreCase("PULSE10")
        ).thenReturn(
                Optional.of(percentageCoupon)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                couponService
                                        .calculateDiscount(
                                                "PULSE10",
                                                100000.0
                                        )
                );

        assertEquals(
                "Coupon has expired",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectOrderBelowMinimumAmount() {

        when(
                couponRepository
                        .findByCodeIgnoreCase("PULSE10")
        ).thenReturn(
                Optional.of(percentageCoupon)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                couponService
                                        .calculateDiscount(
                                                "PULSE10",
                                                40000.0
                                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Minimum order amount")
        );
    }

    @Test
    void fixedDiscountShouldWork() {

        percentageCoupon.setDiscountType("FIXED");
        percentageCoupon.setDiscountValue(5000.0);

        when(
                couponRepository
                        .findByCodeIgnoreCase("PULSE10")
        ).thenReturn(
                Optional.of(percentageCoupon)
        );

        Double discount =
                couponService.calculateDiscount(
                        "PULSE10",
                        100000.0
                );

        assertEquals(
                5000.0,
                discount
        );
    }
}