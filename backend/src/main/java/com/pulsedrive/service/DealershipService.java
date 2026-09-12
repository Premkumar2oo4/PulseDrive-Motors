package com.pulsedrive.service;

import com.pulsedrive.dto.DealershipRequestDTO;
import com.pulsedrive.dto.DealershipResponseDTO;
import com.pulsedrive.entity.Dealership;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.mapper.DealershipMapper;
import com.pulsedrive.repository.DealershipRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealershipService {

    private final DealershipRepository dealershipRepository;
    private final DealershipMapper dealershipMapper;

    public DealershipService(
            DealershipRepository dealershipRepository,
            DealershipMapper dealershipMapper) {

        this.dealershipRepository =
                dealershipRepository;

        this.dealershipMapper =
                dealershipMapper;
    }

    public List<DealershipResponseDTO> getAllDealerships() {

        return dealershipRepository
                .findAll()
                .stream()
                .map(dealershipMapper::toResponseDTO)
                .toList();
    }

    public DealershipResponseDTO getDealershipById(
            Long id) {

        Dealership dealership =
                dealershipRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dealership not found with id: "
                                                + id
                                )
                        );

        return dealershipMapper
                .toResponseDTO(dealership);
    }

    public DealershipResponseDTO createDealership(
            DealershipRequestDTO dto) {

        Dealership dealership =
                dealershipMapper.toEntity(dto);

        Dealership saved =
                dealershipRepository.save(dealership);

        return dealershipMapper
                .toResponseDTO(saved);
    }

    public DealershipResponseDTO updateDealership(
            Long id,
            DealershipRequestDTO dto) {

        Dealership dealership =
                dealershipRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dealership not found with id: "
                                                + id
                                )
                        );

        dealership.setName(dto.getName());
        dealership.setAddress(dto.getAddress());
        dealership.setCity(dto.getCity());
        dealership.setState(dto.getState());
        dealership.setPhone(dto.getPhone());
        dealership.setEmail(dto.getEmail());
        dealership.setOpeningHours(dto.getOpeningHours());
        dealership.setLatitude(dto.getLatitude());
        dealership.setLongitude(dto.getLongitude());

        Dealership updated =
                dealershipRepository.save(dealership);

        return dealershipMapper
                .toResponseDTO(updated);
    }

    public void deleteDealership(Long id) {

        if (!dealershipRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Dealership not found with id: " + id
            );
        }

        dealershipRepository.deleteById(id);
    }

    public List<DealershipResponseDTO> getByCity(
            String city) {

        return dealershipRepository
                .findByCityIgnoreCase(city)
                .stream()
                .map(dealershipMapper::toResponseDTO)
                .toList();
    }
}