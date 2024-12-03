package com.example.WheelWise.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WheelWise.entities.Vehicle;
import com.example.WheelWise.entities.Booking;
import com.example.WheelWise.repositories.VehicleRepository;
import com.example.WheelWise.repositories.BookingRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    // Method to fetch all vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    // Method to check if a vehicle is available for the given time range
    private boolean isVehicleAvailable(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime) {
        // Get all confirmed bookings for the vehicle
        List<Booking> bookings = bookingRepository.findByVehicle(vehicle);

        // Loop through all bookings to check for any time overlap
        for (Booking booking : bookings) {
            LocalDateTime bookingStart = booking.getStartDate();
            LocalDateTime bookingEnd = booking.getEndDate();

            // If the requested time range overlaps with any booking, return false
            if (startTime.isBefore(bookingEnd) && endTime.isAfter(bookingStart)) {
                return false; // Vehicle is not available due to time overlap
            }
        }

        // If no overlap, vehicle is available
        return true;
    }
    
    public List<Vehicle> findAvailableVehiclesWithFilters(
            String location,
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<String> type, // Accept multiple types
            List<String> companyName, // Accept multiple companies
            List<String> fuelType, // Accept multiple fuel types
            List<String> transmissionType, // Accept multiple transmission types
            List<Integer> numofseats, // Accept multiple seat numbers
            List<Double> minPrice, // Accept multiple min prices
            List<Double> maxPrice // Accept multiple max prices
    ) {
        // Assuming you have a repository or data source where vehicles are stored
        List<Vehicle> vehicles = vehicleRepository.findAll(); // Or your method of fetching vehicles
        
        return vehicles.stream()
                .filter(vehicle -> (startTime == null || endTime == null) || isVehicleAvailable(vehicle, startTime, endTime)) // Check availability if both are provided
                .filter(vehicle -> location == null || vehicle.getLocation().equalsIgnoreCase(location)) // Filter by location
                .filter(vehicle -> type == null || type.isEmpty() || type.contains(vehicle.getType())) // Filter by multiple types
                .filter(vehicle -> companyName == null || companyName.isEmpty() || companyName.contains(vehicle.getCompanyName())) // Filter by multiple companies
                .filter(vehicle -> fuelType == null || fuelType.isEmpty() || fuelType.contains(vehicle.getFuelType())) // Filter by multiple fuel types
                .filter(vehicle -> transmissionType == null || transmissionType.isEmpty() || transmissionType.contains(vehicle.getTransmissionType())) // Filter by multiple transmission types
                .filter(vehicle -> numofseats == null || numofseats.isEmpty() || numofseats.contains(vehicle.getCapacity())) // Filter by multiple seat counts
                .filter(vehicle -> minPrice == null || minPrice.isEmpty() || minPrice.stream().anyMatch(price -> vehicle.getPricePerDay().compareTo(BigDecimal.valueOf(price)) >= 0)) // Filter by multiple min prices
                .filter(vehicle -> maxPrice == null || maxPrice.isEmpty() || maxPrice.stream().anyMatch(price -> vehicle.getPricePerDay().compareTo(BigDecimal.valueOf(price)) <= 0)) // Filter by multiple max prices
                .collect(Collectors.toList());
    }
    public void saveVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }
    public Vehicle findByCompanyNameAndNumberPlate(String companyName, String numberPlate) {
        return vehicleRepository.findByCompanyNameAndNumberPlate(companyName, numberPlate);
    }
}
