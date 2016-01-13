package com.epam.test.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.epam.test.dao.jdbc.DAOManager;
import com.epam.test.security.AuthenticationProvider;
import com.epam.test.service.LanguageManager;
import com.epam.test.util.CookieManager;

public final class ApplicationContext {
	private Map<String, Object> beans = new ConcurrentHashMap<>();

	private ApplicationContext() {
	}

	private void initContext() {
		initDAO();

		addBean("cookieManager", new CookieManager());
		addBean("authenticationProvider", new AuthenticationProvider());

		addBean("languageManager", new LanguageManager());
	}

	private void initDAO() {
		DAOManager daoManager = DAOManager.getInstance();
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final class ApplicationContextHolder {
		private static final ApplicationContext INSTANCE = new ApplicationContext();
		static {
			INSTANCE.initContext();
		}
	}

	public static ApplicationContext getInstance() {
		return ApplicationContextHolder.INSTANCE;
	}

	public Object getBean(String key) {
		return beans.get(key);
	}

	public Object addBean(String key, Object bean) {
		return beans.put(key, bean);
	}

}
