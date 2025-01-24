package com.realestate.oauth.dto;

public class ProfilesDto {
	private String fullName;
	private Long phno;
	private String address;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Long getPhno() {
		return phno;
	}
	public void setPhno(Long phno) {
		this.phno = phno;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "ProfilesDto [fullName=" + fullName + ", phno=" + phno + ", address=" + address + "]";
	}
	
	
	
	
}
