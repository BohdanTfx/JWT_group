package com.epam.test.model;

public interface BaseEntity<ID> {
	public ID getId();

	public void setId(ID id);
}
