package model;

public class Sale {
	public String product;
	public String customer; 
	public String employee; 
	public String date;

	public Sale(String product, String customer, String employee, String date) {
		super();
		this.product = product;
		this.customer = customer;
		this.employee = employee;
		this.date = date;
	}
}
