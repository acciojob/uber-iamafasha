package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		List<Driver> driverList = driverRepository2.findAll();
		Driver driver1 = null;
		int min = Integer.MAX_VALUE;
		for(Driver driver: driverList){
			if(driver.getCab().getAvailable() && driver.getDriverId() < min){
				min = driver.getDriverId();
				driver1 = driver;
			}
		}

		if(min < Integer.MAX_VALUE && driver1 != null){
			Customer customer = customerRepository2.findById(customerId).get();
			TripBooking tripBooking = new TripBooking();

			tripBooking.setFromLocation(fromLocation);
			tripBooking.setToLocation(toLocation);
			tripBooking.setCustomer(customer);
			tripBooking.setBill(distanceInKm * driver1.getCab().getPerKmRate());
			tripBooking.setStatus(TripStatus.CONFIRMED);
			tripBooking.setDistanceInKm(distanceInKm);
			tripBooking.setDriver(driver1);

			customer.getTripBookingList().add(tripBooking);

			driver1.getCab().setAvailable(false);
			driver1.getTripBookingList().add(tripBooking);


			driverRepository2.save(driver1);
			customerRepository2.save(customer);
			tripBookingRepository2.save(tripBooking);
			return tripBooking;
		}
		else{
			throw new Exception("No cab available!");
		}
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.setBill(0);
		Driver driver = trip.getDriver();
		driver.getCab().setAvailable(true);
		tripBookingRepository2.save(trip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		Driver driver = trip.getDriver();
		driver.getCab().setAvailable(true);
		int perKmRate = driver.getCab().getPerKmRate();
		int km = trip.getDistanceInKm();
		trip.setBill(perKmRate*km);
		tripBookingRepository2.save(trip);
	}
}
