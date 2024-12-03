package com.example.WheelWise.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WheelWise.entities.Booking;
import com.example.WheelWise.entities.Vehicle;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVehicle(Vehicle vehicle); // Fetch bookings for a specific vehicle
}
