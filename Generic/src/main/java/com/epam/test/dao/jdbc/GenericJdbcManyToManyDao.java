package com.epam.test.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.epam.test.dao.GenericManyToManyDao;
import com.epam.test.model.BaseEntity;
import com.epam.test.transformer.DataTransformer;

public abstract class GenericJdbcManyToManyDao<F extends BaseEntity<ID>, S extends BaseEntity<ID>, ID>
		implements GenericManyToManyDao<F, S, ID>
{
	protected DataSource dataSource;
	protected String table;
	protected String firstTable;

	protected String secondTable;
	protected String firstIdName;
	protected String secondIdName;
	protected DataTransformer<F> firstTransformer;
	protected DataTransformer<S> secondTransformer;

	public GenericJdbcManyToManyDao(String table, String firstTable,
			String secondTable, String firstIdName, String secondIdName,
			DataTransformer<F> firstTransformer,
			DataTransformer<S> secondTransformer)
	{
		this.table = table;
		this.firstTable = firstTable;
		this.secondTable = secondTable;
		this.firstIdName = firstIdName;
		this.secondIdName = secondIdName;
		this.firstTransformer = firstTransformer;
		this.secondTransformer = secondTransformer;
	}

	public GenericJdbcManyToManyDao(String table, String firstTable,
			String secondTable, DataTransformer<F> firstTransformer,
			DataTransformer<S> secondTransformer)
	{
		this.table = table;
		this.firstTable = firstTable;
		this.secondTable = secondTable;
		this.firstIdName = firstTable + "_id";
		this.secondIdName = secondTable + "_id";
		this.firstTransformer = firstTransformer;
		this.secondTransformer = secondTransformer;
	}

	@Override
	public List<S> getBasedOnFirst(ID firstId) {
		List<S> entities = new ArrayList<>();
		String query = "SELECT " + secondTable + ".* FROM " + secondTable
				+ ", " + table + " WHERE " + secondTable + ".id = " + table
				+ "." + secondIdName + " AND " + table + "." + firstIdName
				+ " = ?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setObject(1, firstId);
			try (ResultSet set = statement.executeQuery()) {
				while (set.next()) {
					entities.add(secondTransformer.getObject(set));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entities;
	}

	@Override
	public List<F> getBasedOnSecond(ID secondId) {
		List<F> entities = new ArrayList<>();
		String query = "SELECT " + firstTable + ".* FROM " + firstTable + ", "
				+ table + " WHERE " + firstTable + ".id = " + table + "."
				+ firstIdName + " AND " + table + "." + secondIdName + " = ?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setObject(1, secondId);
			try (ResultSet set = statement.executeQuery()) {
				while (set.next()) {
					entities.add(firstTransformer.getObject(set));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entities;
	}

	@Override
	public void saveContact(ID firstId, ID secondId) {
		String query = "INSERT INTO " + table + " (" + firstIdName + ", "
				+ secondIdName + ") VALUES (?, ?)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setObject(1, firstId);
			statement.setObject(2, secondId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
