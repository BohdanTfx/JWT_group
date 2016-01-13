package com.epam.test.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.epam.test.model.BaseEntity;
import com.epam.test.util.ValidationParametersBuilder.Parameters;

public interface Service<T extends BaseEntity<ID>, ID> {
	T create(Map<String, String[]> data);

	public T modify(T entity);

	public void remove(T entity);

	public T findById(ID id);

	public List<T> findAll();

	public default String encodePassword(String password)
			throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());

		byte byteData[] = md.digest();
		StringBuilder encodedPassword = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			encodedPassword.append(Integer.toString(
					(byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return encodedPassword.toString();
	}
	
	public boolean isDataValid(Map<Parameters, String> data);
}
