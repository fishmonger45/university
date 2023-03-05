package model;

public class Customer {
	public String name;
	public String email;
	public String address;
	public String contactNumber;
	public String creditCardNumber;

	public Customer(String name, String email, String address, String contactNumber, String creditCardNumber) {
		super();
		this.name = name;
		this.email = email;
		this.address = address;
		this.contactNumber = contactNumber;
		this.creditCardNumber = creditCardNumber;
	}
}
