package com.realestate.oauth.dto;

public class PropertyDto {
	private String title;
	private String description;
	private String location;
	private String propertyType;
	private String price;
	private String contact;
	public PropertyDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PropertyDto(String title, String description, String location, String property, String price , String contact) {
		super();
		this.title = title;
		this.description = description;
		this.location = location;
		this.propertyType = property;
		this.price = price;
		this.contact = contact;
	}
	public String getTitle() {
		return title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getPropertyType() {
	    return propertyType;
	}

	public void setPropertyType(String propertyType) {
	    this.propertyType = propertyType;
	}

	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	@Override
	public String toString() {
		return "PropertyDto [title=" + title + ", description=" + description + ", location=" + location
				+ ", type=" + propertyType + ", price=" + price + ", contact=" + contact + "]";
	}
}
