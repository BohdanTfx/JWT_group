package com.epam.test.dao;

import java.util.List;

import javax.sql.DataSource;

public interface GenericDao<T, ID> {

	T save(T entity);

	T update(T entity);

	void delete(T entity);

	T getById(ID id);

	List<T> getAll();

	void setDataSource(DataSource dataSource);
}