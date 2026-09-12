package com.pulsedrive.service;

import com.pulsedrive.dto.CouponRequestDTO;
import com.pulsedrive.dto.CouponResponseDTO;
import com.pulsedrive.entity.Coupon;
import com.pulsedrive.exception.DuplicateResourceException;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.repository.CouponRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {

        private final CouponRepository couponRepository;

        public CouponService(
                        CouponRepository couponRepository) {

                this.couponRepository = couponRepository;
        }

        public CouponResponseDTO createCoupon(
                        CouponRequestDTO dto) {

                String code = dto.getCode().trim().toUpperCase();

                if (couponRepository
                                .existsByCodeIgnoreCase(code)) {

                        throw new DuplicateResourceException(
                                        "Coupon already exists with code: " + code);
                }

                validateCouponData(dto);

                Coupon coupon = new Coupon();

                coupon.setCode(code);
                coupon.setDiscountType(
                                dto.getDiscountType().toUpperCase());
                coupon.setDiscountValue(
                                dto.getDiscountValue());
                coupon.setMinimumAmount(
                                dto.getMinimumAmount());
                coupon.setExpiryDate(
                                dto.getExpiryDate());

                coupon.setActive(
                                dto.getActive() == null
                                                ? true
                                                : dto.getActive());

                return toDTO(
                                couponRepository.save(coupon));
        }

        public List<CouponResponseDTO> getAllCoupons() {

                return couponRepository.findAll()
                                .stream()
                                .map(this::toDTO)
                                .toList();
        }

        public CouponResponseDTO getCouponById(
                        Long id) {

                return toDTO(
                                getCoupon(id));
        }

        public CouponResponseDTO updateCoupon(
                        Long id,
                        CouponRequestDTO dto) {

                Coupon coupon = getCoupon(id);

                validateCouponData(dto);

                coupon.setCode(
                                dto.getCode().trim().toUpperCase());

                coupon.setDiscountType(
                                dto.getDiscountType().toUpperCase());

                coupon.setDiscountValue(
                                dto.getDiscountValue());

                coupon.setMinimumAmount(
                                dto.getMinimumAmount());

                coupon.setExpiryDate(
                                dto.getExpiryDate());

                coupon.setActive(
                                dto.getActive());

                return toDTO(
                                couponRepository.save(coupon));
        }

        public void deleteCoupon(Long id) {

                Coupon coupon = getCoupon(id);

                couponRepository.delete(coupon);
        }

        public Double calculateDiscount(
        String code,
        Double orderAmount) {

    Coupon coupon =
            getValidCoupon(
                    code,
                    orderAmount
            );

    double discount;

    if ("PERCENTAGE".equalsIgnoreCase(
            coupon.getDiscountType())) {

        discount =
                orderAmount
                        * coupon.getDiscountValue()
                        / 100.0;

    } else {

        discount =
                coupon.getDiscountValue();
    }

    return Math.min(
            discount,
            orderAmount
    );
}

        private void validateCouponData(
                        CouponRequestDTO dto) {

                if (dto.getCode() == null ||
                                dto.getCode().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Coupon code is required");
                }

                if (dto.getDiscountType() == null) {

                        throw new IllegalArgumentException(
                                        "Discount type is required");
                }

                String type = dto.getDiscountType()
                                .toUpperCase();

                if (!type.equals("PERCENTAGE") &&
                                !type.equals("FIXED")) {

                        throw new IllegalArgumentException(
                                        "Discount type must be PERCENTAGE or FIXED");
                }

                if (dto.getDiscountValue() == null ||
                                dto.getDiscountValue() <= 0) {

                        throw new IllegalArgumentException(
                                        "Discount value must be greater than 0");
                }

                if (type.equals("PERCENTAGE") &&
                                dto.getDiscountValue() > 100) {

                        throw new IllegalArgumentException(
                                        "Percentage discount cannot exceed 100");
                }
        }

        private Coupon getCoupon(Long id) {

                return couponRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Coupon not found with id: " + id));
        }

        private CouponResponseDTO toDTO(
                        Coupon coupon) {

                CouponResponseDTO dto = new CouponResponseDTO();

                dto.setId(coupon.getId());
                dto.setCode(coupon.getCode());
                dto.setDiscountType(
                                coupon.getDiscountType());
                dto.setDiscountValue(
                                coupon.getDiscountValue());
                dto.setMinimumAmount(
                                coupon.getMinimumAmount());
                dto.setExpiryDate(
                                coupon.getExpiryDate());
                dto.setActive(
                                coupon.getActive());

                return dto;
        }

        public Coupon getValidCoupon(
                        String code,
                        Double orderAmount) {

                Coupon coupon = couponRepository
                                .findByCodeIgnoreCase(code)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Coupon not found: " + code));

                if (!Boolean.TRUE.equals(coupon.getActive())) {
                        throw new IllegalArgumentException(
                                        "Coupon is inactive");
                }

                if (coupon.getExpiryDate() != null &&
                                coupon.getExpiryDate()
                                                .isBefore(LocalDate.now())) {

                        throw new IllegalArgumentException(
                                        "Coupon has expired");
                }

                if (coupon.getMinimumAmount() != null &&
                                orderAmount < coupon.getMinimumAmount()) {

                        throw new IllegalArgumentException(
                                        "Minimum order amount for this coupon is "
                                                        + coupon.getMinimumAmount());
                }

                return coupon;
        }
}