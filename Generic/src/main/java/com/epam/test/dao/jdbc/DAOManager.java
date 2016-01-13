package com.epam.test.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.epam.test.controller.FrontController;
import com.epam.test.dao.GenericDao;
import com.epam.test.dao.GenericManyToManyDao;
import com.epam.test.model.BaseEntity;

public final class DAOManager {
	private DataSource connectionPool;
	private Map<String, Object> daos = new HashMap<>();

	private DAOManager() {
	}

	private static final class DAOManagerHolder {
		private static final DAOManager INSTANCE = new DAOManager();
		static {
			try {
				INSTANCE.initConnectionPool();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static DAOManager getInstance() {
		return DAOManagerHolder.INSTANCE;
	}

	private void initConnectionPool() throws ClassNotFoundException,
			IOException, SQLException, InstantiationException,
			IllegalAccessException
	{
		 Properties properties =
		 getDatasourceProperties("/datasource.properties");
		
		 Class.forName(properties.getProperty("driver"));
		 connectionPool = new org.apache.tomcat.jdbc.pool.DataSource();
		
		 connectionPool.setUsername(properties.getProperty("user"));
		 connectionPool.setPassword(properties.getProperty("password"));
		
		 connectionPool.setDriverClassName(properties.getProperty("driver"));
		 connectionPool.setUrl(properties.getProperty("url"));
		 connectionPool.setInitialSize(200);
	}

	private Properties getDatasourceProperties(String path) throws IOException {
		Properties properties = new Properties();
		try (InputStream file = FrontController.class.getResourceAsStream(path);)
		{
			properties.load(file);
		}

		return properties;
	}

	public DataSource getDataSource() {
		return connectionPool;
	}

	public void addDao(String name, Object dao) {
		daos.put(name, dao);
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseEntity<ID>, ID> GenericDao<T, ID> getDAO(
			String daoName)
	{
		return (GenericDao<T, ID>) daos.get(daoName);
	}

	@SuppressWarnings("unchecked")
	public <F extends BaseEntity<ID>, S extends BaseEntity<ID>, ID> GenericManyToManyDao<F, S, ID> getManyToManyDAO(
			String daoName)
	{
		return (GenericManyToManyDao<F, S, ID>) daos.get(daoName);
	}

}
