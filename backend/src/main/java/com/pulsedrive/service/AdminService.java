package com.pulsedrive.service;

import com.pulsedrive.dto.AdminDashboardDTO;
import com.pulsedrive.dto.SalesTrendDTO;

import com.pulsedrive.repository.OrderRepository;
import com.pulsedrive.repository.TestDriveRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;
import com.pulsedrive.dto.InventorySummaryDTO;
import com.pulsedrive.dto.VehicleResponseDTO;
import com.pulsedrive.mapper.VehicleMapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderRepository orderRepository;
    private final TestDriveRepository testDriveRepository;
private final VehicleMapper vehicleMapper;
    public AdminService(
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            OrderRepository orderRepository,
            TestDriveRepository testDriveRepository,
        VehicleMapper vehicleMapper) {

        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.orderRepository = orderRepository;
        this.testDriveRepository = testDriveRepository;
         this.vehicleMapper = vehicleMapper;
    }

    public AdminDashboardDTO getDashboard() {

        AdminDashboardDTO dto =
                new AdminDashboardDTO();

        dto.setTotalCustomers(
                userRepository
                        .countByRoleIgnoreCase("CUSTOMER")
        );

        dto.setTotalVehicles(
                vehicleRepository.count()
        );

        dto.setAvailableVehicles(
                vehicleRepository
                        .countByAvailableTrue()
        );

        dto.setTotalOrders(
                orderRepository.count()
        );

        dto.setPendingOrders(
                orderRepository
                        .countByStatusIgnoreCase("PLACED")
        );

        dto.setDeliveredOrders(
                orderRepository
                        .countByStatusIgnoreCase("DELIVERED")
        );

        dto.setCancelledOrders(
                orderRepository
                        .countByStatusIgnoreCase("CANCELLED")
        );

        dto.setTotalTestDrives(
                testDriveRepository.count()
        );

        dto.setPendingTestDrives(
                testDriveRepository
                        .countByStatusIgnoreCase("PENDING")
        );

        Double revenue =
                orderRepository
                        .calculateTotalRevenue();

        dto.setTotalRevenue(
                revenue == null ? 0.0 : revenue
        );

        List<com.pulsedrive.entity.Order> paidOrders =
                orderRepository.findByPaymentStatusIgnoreCase("PAID");
        dto.setMonthlySales(buildSalesTrend(paidOrders, false));
        dto.setYearlySales(buildSalesTrend(paidOrders, true));

        return dto;
    }

    private List<SalesTrendDTO> buildSalesTrend(
            List<com.pulsedrive.entity.Order> orders,
            boolean yearly) {
        Map<String, List<com.pulsedrive.entity.Order>> grouped = orders.stream()
                .filter(order -> order.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        order -> yearly
                                ? String.valueOf(order.getCreatedAt().getYear())
                                : order.getCreatedAt().getYear() + "-"
                                        + String.format("%02d", order.getCreatedAt().getMonthValue()),
                        TreeMap::new,
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> new SalesTrendDTO(
                        entry.getKey(),
                        entry.getValue().stream()
                                .mapToDouble(order -> order.getTotalAmount() == null ? 0 : order.getTotalAmount())
                                .sum(),
                        (long) entry.getValue().size()))
                .toList();
    }
    public InventorySummaryDTO getInventorySummary() {

    InventorySummaryDTO dto =
            new InventorySummaryDTO();

    dto.setTotalVehicles(
            vehicleRepository.count()
    );

    dto.setAvailableVehicles(
            vehicleRepository.countByAvailableTrue()
    );

    dto.setUnavailableVehicles(
            (long) vehicleRepository
                    .findByAvailableFalse()
                    .size()
    );

    dto.setLowStockVehicles(
            vehicleRepository
                    .countByStockLessThanEqual(2)
    );

    return dto;
}
public List<VehicleResponseDTO> getLowStockVehicles(
        Integer threshold) {

    int stockLimit =
            threshold == null
                    ? 2
                    : threshold;

    return vehicleRepository
            .findByStockLessThanEqual(stockLimit)
            .stream()
            .map(vehicleMapper::toResponseDTO)
            .toList();
}
public List<VehicleResponseDTO> getUnavailableVehicles() {

    return vehicleRepository
            .findByAvailableFalse()
            .stream()
            .map(vehicleMapper::toResponseDTO)
            .toList();
}
    
}