package com.epam.test.model;

import java.util.Locale;

public interface UserEntity extends BaseEntity<Integer> {
	public String getEmail();

	public void setEmail(String email);

	public String getPassword();

	public void setPassword(String password);

	public String getUuid();

	public void setUuid(String uuid);

	public Locale getLocale();

	public void setLocale(Locale locale);
}
