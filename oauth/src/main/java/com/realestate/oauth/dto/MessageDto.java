package com.realestate.oauth.dto;

public class MessageDto {
	private Long propertyId;
	private String message;
	private String email;
	public MessageDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MessageDto(Long propertyid, String message , String email) {
		super();
		this.propertyId = propertyId;
		this.message = message;
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(Long propertyid) {
		this.propertyId = propertyid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "MessageDto [propertyid=" + propertyId + ", message=" + message + "]";
	}
	
	
}
