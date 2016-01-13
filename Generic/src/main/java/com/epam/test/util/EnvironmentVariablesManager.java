package com.epam.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.epam.test.controller.FrontController;

public class EnvironmentVariablesManager {
	private Properties properties;

	private EnvironmentVariablesManager() {
	}

	private void loadVariables(String path) throws IOException {
		properties = new Properties();

		try (InputStream file = FrontController.class.getResourceAsStream(path);)
		{
			properties.load(file);
		}
	}

	private static final class EnvironmentVariablesManagertHolder {
		private static final EnvironmentVariablesManager HOLDER_INSTANCE = new EnvironmentVariablesManager();
		static {
			try {
				HOLDER_INSTANCE
						.loadVariables("/environmentVariables.properties");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static EnvironmentVariablesManager getInstance() {
		return EnvironmentVariablesManagertHolder.HOLDER_INSTANCE;
	}

	public String getVar(String name) {
		return properties.getProperty(name);
	}
}
