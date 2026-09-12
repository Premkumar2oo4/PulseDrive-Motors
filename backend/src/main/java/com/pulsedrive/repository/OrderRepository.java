package com.pulsedrive.repository;

import com.pulsedrive.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByPaymentStatusIgnoreCase(String paymentStatus);
    long countByStatusIgnoreCase(String status);
    @Query("""
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM Order o
       WHERE UPPER(o.paymentStatus) = 'PAID'
       """)
Double calculateTotalRevenue();
}