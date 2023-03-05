package model;

import java.util.ArrayList;

public class MainModel {

	public enum TYPE {
		EDIT, NEW
	}

	public enum PRODUCT {
		EDIT, NEW
	}

	public ArrayList<Customer> customers;
	public ArrayList<String> employees;
	public ArrayList<Sale> sales;
	public ArrayList<HotelDeal> hotelDeals;
	public ArrayList<Flight> flights;
	public TYPE type;
	public int currentlySelected;
	private static MainModel model;

	public static MainModel getMainModel() {
		if (MainModel.model == null) {
			MainModel.model = new MainModel();
		}
		return MainModel.model;
	}

	private MainModel() {

		customers = new ArrayList<Customer>();
		employees = new ArrayList<String>();
		sales = new ArrayList<Sale>();
		hotelDeals = new ArrayList<HotelDeal>();
		flights = new ArrayList<Flight>();

		// Customers
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));
		customers.add(new Customer("Andreas", "andreas@theknapps.co.nz", "123 Fake Street", "0211111111113",
				"2345 2344 6666 1111"));

		// Employees
		employees.add("Bevan");
		employees.add("Andreas");
		employees.add("Lewis");
		employees.add("Rossman");
		employees.add("Senta");

		// Sales
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));
		sales.add(new Sale("Hotel Deal", "Adam", "Bevan", "23/05/2020"));

		// Flights
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));
		flights.add(new Flight("New York", "New Zealand", "$1000"));

		// Hotel Deals
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		hotelDeals.add(new HotelDeal("Downtown Abbey", "10 Days", "$1500"));
		currentlySelected = 0;

		type = TYPE.EDIT;
	}
}
