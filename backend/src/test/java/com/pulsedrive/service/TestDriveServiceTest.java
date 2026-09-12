package com.pulsedrive.service;

import com.pulsedrive.dto.TestDriveRequestDTO;
import com.pulsedrive.dto.TestDriveResponseDTO;

import com.pulsedrive.entity.Dealership;
import com.pulsedrive.entity.TestDrive;
import com.pulsedrive.entity.User;
import com.pulsedrive.entity.Vehicle;

import com.pulsedrive.exception.DuplicateResourceException;

import com.pulsedrive.repository.DealershipRepository;
import com.pulsedrive.repository.TestDriveRepository;
import com.pulsedrive.repository.UserRepository;
import com.pulsedrive.repository.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestDriveServiceTest {

    @Mock
    private TestDriveRepository testDriveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DealershipRepository dealershipRepository;

    @InjectMocks
    private TestDriveService testDriveService;

    private User user;
    private Vehicle vehicle;
    private Dealership dealership;
    private TestDriveRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        // ===============================
        // USER
        // ===============================

        user = new User();

        ReflectionTestUtils.setField(
                user,
                "id",
                4L
        );

        user.setEmail(
                "customer@example.com"
        );

        user.setFirstName("Test");
        user.setLastName("Customer");

        // ===============================
        // VEHICLE
        // ===============================

        vehicle = new Vehicle();

        ReflectionTestUtils.setField(
                vehicle,
                "id",
                1L
        );

        vehicle.setBrand("BMW");
        vehicle.setModel("M4");
        vehicle.setAvailable(true);

        // ===============================
        // DEALERSHIP
        // ===============================

        dealership = new Dealership();

        ReflectionTestUtils.setField(
                dealership,
                "id",
                1L
        );

        dealership.setName(
                "PulseDrive Pune"
        );

        dealership.setCity("Pune");
        dealership.setState("Maharashtra");
        dealership.setAddress(
                "Baner Road, Pune"
        );

        // ===============================
        // REQUEST DTO
        // ===============================

        requestDTO =
                new TestDriveRequestDTO();

        requestDTO.setVehicleId(1L);

        requestDTO.setDealershipId(1L);

        requestDTO.setDate(
                LocalDate.now().plusDays(5)
        );

        requestDTO.setTime(
                LocalTime.of(11, 30)
        );

        requestDTO.setNotes(
                "BMW M4 test drive"
        );
    }

    // =========================================
    // TEST 1 - SUCCESSFUL BOOKING
    // =========================================

    @Test
    void shouldBookTestDriveSuccessfully() {

        prepareBookingMocks();

        when(
                testDriveRepository
                        .existsByDealershipIdAndVehicleIdAndDateAndTime(
                                1L,
                                1L,
                                requestDTO.getDate(),
                                requestDTO.getTime()
                        )
        ).thenReturn(false);

        when(
                testDriveRepository.save(
                        any(TestDrive.class)
                )
        ).thenAnswer(invocation -> {

            TestDrive testDrive =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    testDrive,
                    "id",
                    10L
            );

            return testDrive;
        });

        TestDriveResponseDTO response =
                testDriveService.bookTestDrive(
                        "customer@example.com",
                        requestDTO
                );

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId()
        );

        assertEquals(
                1L,
                response.getVehicleId()
        );

        assertEquals(
                "BMW M4",
                response.getVehicleName()
        );

        assertEquals(
                1L,
                response.getDealershipId()
        );

        assertEquals(
                "PulseDrive Pune",
                response.getDealershipName()
        );

        assertEquals(
                "PENDING",
                response.getStatus()
        );

        verify(
                testDriveRepository,
                times(1)
        ).save(any(TestDrive.class));
    }

    // =========================================
    // TEST 2 - DUPLICATE SLOT
    // =========================================

    @Test
    void shouldRejectDuplicateTestDriveSlot() {

        prepareBookingMocks();

        when(
                testDriveRepository
                        .existsByDealershipIdAndVehicleIdAndDateAndTime(
                                1L,
                                1L,
                                requestDTO.getDate(),
                                requestDTO.getTime()
                        )
        ).thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () ->
                                testDriveService.bookTestDrive(
                                        "customer@example.com",
                                        requestDTO
                                )
                );

        assertEquals(
                "This test drive slot is already booked",
                exception.getMessage()
        );

        verify(
                testDriveRepository,
                never()
        ).save(any(TestDrive.class));
    }

    // =========================================
    // TEST 3 - PAST DATE
    // =========================================

    @Test
    void shouldRejectPastTestDriveDate() {

        requestDTO.setDate(
                LocalDate.now().minusDays(1)
        );

        prepareBookingMocks();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                testDriveService.bookTestDrive(
                                        "customer@example.com",
                                        requestDTO
                                )
                );

        assertEquals(
                "Test drive date cannot be in the past",
                exception.getMessage()
        );

        verify(
                testDriveRepository,
                never()
        ).save(any(TestDrive.class));
    }

    // =========================================
    // TEST 4 - UNAUTHORIZED ACCESS
    // =========================================

    @Test
    void shouldRejectAccessToAnotherUsersTestDrive() {

        TestDrive testDrive =
                createExistingTestDrive();

        when(
                testDriveRepository.findById(10L)
        ).thenReturn(
                Optional.of(testDrive)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                testDriveService
                                        .getMyTestDriveById(
                                                "another@example.com",
                                                10L
                                        )
                );

        assertEquals(
                "You are not allowed to access this test drive",
                exception.getMessage()
        );
    }

    // =========================================
    // TEST 5 - CANCEL TEST DRIVE
    // =========================================

    @Test
    void shouldCancelTestDriveSuccessfully() {

        TestDrive testDrive =
                createExistingTestDrive();

        testDrive.setStatus("PENDING");

        when(
                testDriveRepository.findById(10L)
        ).thenReturn(
                Optional.of(testDrive)
        );

        when(
                testDriveRepository.save(testDrive)
        ).thenReturn(testDrive);

        testDriveService.cancelTestDrive(
                "customer@example.com",
                10L
        );

        assertEquals(
                "CANCELLED",
                testDrive.getStatus()
        );

        verify(
                testDriveRepository,
                times(1)
        ).save(testDrive);
    }

    // =========================================
    // TEST 6 - COMPLETED CANNOT CANCEL
    // =========================================

    @Test
    void shouldRejectCancellationOfCompletedTestDrive() {

        TestDrive testDrive =
                createExistingTestDrive();

        testDrive.setStatus("COMPLETED");

        when(
                testDriveRepository.findById(10L)
        ).thenReturn(
                Optional.of(testDrive)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                testDriveService.cancelTestDrive(
                                        "customer@example.com",
                                        10L
                                )
                );

        assertEquals(
                "Completed test drive cannot be cancelled",
                exception.getMessage()
        );

        verify(
                testDriveRepository,
                never()
        ).save(any(TestDrive.class));
    }

    // =========================================
    // TEST 7 - ADMIN UPDATE STATUS
    // =========================================

    @Test
    void shouldUpdateTestDriveStatusToApproved() {

        TestDrive testDrive =
                createExistingTestDrive();

        testDrive.setStatus("PENDING");

        when(
                testDriveRepository.findById(10L)
        ).thenReturn(
                Optional.of(testDrive)
        );

        when(
                testDriveRepository.save(testDrive)
        ).thenReturn(testDrive);

        TestDriveResponseDTO response =
                testDriveService.updateStatus(
                        10L,
                        "approved"
                );

        assertEquals(
                "APPROVED",
                response.getStatus()
        );

        assertEquals(
                "APPROVED",
                testDrive.getStatus()
        );

        verify(
                testDriveRepository,
                times(1)
        ).save(testDrive);
    }

    // =========================================
    // TEST 8 - INVALID ADMIN STATUS
    // =========================================

    @Test
    void shouldRejectInvalidTestDriveStatus() {

        TestDrive testDrive =
                createExistingTestDrive();

        when(
                testDriveRepository.findById(10L)
        ).thenReturn(
                Optional.of(testDrive)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                testDriveService.updateStatus(
                                        10L,
                                        "HELLO"
                                )
                );

        assertEquals(
                "Invalid test drive status: HELLO",
                exception.getMessage()
        );

        verify(
                testDriveRepository,
                never()
        ).save(any(TestDrive.class));
    }

    // =========================================
    // COMMON BOOKING MOCKS
    // =========================================

    private void prepareBookingMocks() {

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                vehicleRepository.findById(1L)
        ).thenReturn(
                Optional.of(vehicle)
        );

        when(
                dealershipRepository.findById(1L)
        ).thenReturn(
                Optional.of(dealership)
        );
    }

    // =========================================
    // EXISTING TEST DRIVE
    // =========================================

    private TestDrive createExistingTestDrive() {

        TestDrive testDrive =
                new TestDrive();

        ReflectionTestUtils.setField(
                testDrive,
                "id",
                10L
        );

        testDrive.setUser(user);
        testDrive.setVehicle(vehicle);
        testDrive.setDealership(dealership);

        testDrive.setDate(
                LocalDate.now().plusDays(5)
        );

        testDrive.setTime(
                LocalTime.of(11, 30)
        );

        testDrive.setNotes(
                "BMW M4 test drive"
        );

        testDrive.setStatus(
                "PENDING"
        );

        return testDrive;
    }
}