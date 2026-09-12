package com.pulsedrive.mapper;

import com.pulsedrive.dto.DealershipRequestDTO;
import com.pulsedrive.dto.DealershipResponseDTO;
import com.pulsedrive.entity.Dealership;
import org.springframework.stereotype.Component;

@Component
public class DealershipMapper {

    public Dealership toEntity(
            DealershipRequestDTO dto) {

        Dealership dealership =
                new Dealership();

        dealership.setName(dto.getName());
        dealership.setAddress(dto.getAddress());
        dealership.setCity(dto.getCity());
        dealership.setState(dto.getState());
        dealership.setPhone(dto.getPhone());
        dealership.setEmail(dto.getEmail());
        dealership.setOpeningHours(dto.getOpeningHours());
        dealership.setLatitude(dto.getLatitude());
        dealership.setLongitude(dto.getLongitude());

        return dealership;
    }

    public DealershipResponseDTO toResponseDTO(
            Dealership dealership) {

        DealershipResponseDTO dto =
                new DealershipResponseDTO();

        dto.setId(dealership.getId());
        dto.setName(dealership.getName());
        dto.setAddress(dealership.getAddress());
        dto.setCity(dealership.getCity());
        dto.setState(dealership.getState());
        dto.setPhone(dealership.getPhone());
        dto.setEmail(dealership.getEmail());
        dto.setOpeningHours(dealership.getOpeningHours());
        dto.setLatitude(dealership.getLatitude());
        dto.setLongitude(dealership.getLongitude());

        return dto;
    }
}