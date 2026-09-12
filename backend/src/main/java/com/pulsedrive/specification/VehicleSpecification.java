package com.pulsedrive.specification;

import com.pulsedrive.entity.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecification {

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand")),
                        brand.toLowerCase()
                );
    }

    public static Specification<Vehicle> hasFuelType(String fuelType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("fuelType")),
                        fuelType.toLowerCase()
                );
    }

    public static Specification<Vehicle> hasBodyType(String bodyType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("bodyType")),
                        bodyType.toLowerCase()
                );
    }

    public static Specification<Vehicle> hasTransmission(
            String transmission) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("transmission")),
                        transmission.toLowerCase()
                );
    }

    public static Specification<Vehicle> priceGreaterThanOrEqualTo(
            Double minPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Vehicle> priceLessThanOrEqualTo(
            Double maxPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }
}