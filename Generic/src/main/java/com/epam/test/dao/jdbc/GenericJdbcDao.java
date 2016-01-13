package com.epam.test.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.epam.test.dao.GenericDao;
import com.epam.test.model.BaseEntity;
import com.epam.test.transformer.DataTransformer;

public abstract class GenericJdbcDao<T extends BaseEntity<ID>, ID> implements
		GenericDao<T, ID>
{
	protected DataTransformer<T> transformer;
	protected DataSource dataSource;
	protected String table;
	protected Class<T> class1;

	public GenericJdbcDao(Class<T> class1, String table) throws Exception {
		this.class1 = class1;
		this.table = table;
		this.transformer = new DataTransformer<>(class1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T save(T entity) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						transformer.getSaveStatement(),
						Statement.RETURN_GENERATED_KEYS)) {
			transformer.fillSave(statement, entity);
			statement.executeUpdate();
			try (ResultSet set = statement.getGeneratedKeys()) {
				if (set.next())
					entity.setId((ID) new Integer(set.getInt(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	@Override
	public T update(T entity) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(transformer.getUpdateStatement())) {
			transformer.fillUpdate(statement, entity);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	@Override
	public void delete(T entity) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(transformer.getDeleteStatement())) {
			transformer.fillDelete(statement, entity);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public T getById(ID id) {
		T entity = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(transformer.getSelectStatement())) {
			T temp = class1.newInstance();
			temp.setId(id);
			transformer.fillSelect(statement, temp);
			try (ResultSet set = statement.executeQuery()) {
				if (set.next()) {
					entity = transformer.getObject(set);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	@Override
	public List<T> getAll() {
		List<T> entities = new ArrayList<>();
		String query = "SELECT * FROM " + table;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query);
				ResultSet set = statement.executeQuery()) {
			while (set.next()) {
				T entity = transformer.getObject(set);
				entities.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entities;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
