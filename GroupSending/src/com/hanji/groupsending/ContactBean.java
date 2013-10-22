package com.hanji.groupsending;

import java.io.Serializable;

public class ContactBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String email;
	private String number;
	private boolean checked = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public String toString() {
		return "ContactBean [id=" + id + ", name=" + name + ", email=" + email + ", number="
				+ number + ", checked=" + checked + "]";
	}

}
