package com.elinext.demo;

public class UserModel {
	public int id;
	public String name, email;
	public boolean isActivated;

	public UserModel(int id, String name, String email, boolean isActivated) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.isActivated = isActivated;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", isActivated=" + isActivated + "]";
	}
}