package com.epam.test.service;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.epam.test.context.ApplicationContext;
import com.epam.test.model.UserEntity;
import com.epam.test.util.CookieManager;
import com.epam.test.util.EnvironmentVariablesManager;

public class LanguageManager extends HttpServlet {
	private static final long serialVersionUID = -4804392698613743083L;
	private String cookieLangName;
	private String sessionUserName;
	private String sessionLanguage;
	private CookieManager cookieManager;

	public LanguageManager() {
		this.cookieManager = (CookieManager) ApplicationContext.getInstance()
				.getBean("cookieManager");
		EnvironmentVariablesManager manager = EnvironmentVariablesManager
				.getInstance();
		cookieLangName = manager.getVar("cookie.user.lang");
		sessionUserName = manager.getVar("session.user");
		sessionLanguage = manager.getVar("session.language");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		Locale locale = null;
		UserEntity user = checkUser(req);
		if (user != null) {
			locale = user.getLocale();
			if (locale != null) {
				req.getSession().setAttribute(sessionLanguage, locale);
				return;
			}
		}

		Cookie cookie = cookieManager.getCookie(req, cookieLangName);
		if (cookie != null) {
			locale = getLocaleFromCookie(cookie);
			if (locale != null) {
				req.getSession().setAttribute(sessionLanguage, locale);
				cookieManager.modifyCookie(req, resp, cookieLangName,
						locale.toLanguageTag());
				return;
			}
		}

		locale = Locale.ENGLISH;
		req.getSession().setAttribute(sessionLanguage, locale);
		cookieManager.addCookie(resp, cookieLangName, locale.toLanguageTag(),
				60 * 60 * 24 * 365);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		Locale locale = null;
		locale = checkRequest(req);
		if (locale != null) {
			req.getSession().setAttribute(sessionLanguage, locale);
			if (checkUser(req) == null) {
				if (cookieManager.getCookie(req, cookieLangName) != null)
					cookieManager.modifyCookie(req, resp, cookieLangName,
							locale.toLanguageTag());
				else
					cookieManager.addCookie(resp, cookieLangName,
							locale.toLanguageTag(), 60 * 60 * 24 * 365);
			} else
				modifyUser(req, locale);
			return;
		}

		UserEntity user = checkUser(req);
		if (user != null) {
			locale = user.getLocale();
			if (locale != null) {
				req.getSession().setAttribute(sessionLanguage, locale);
				modifyUser(req, locale);
				return;
			}
		}

		Cookie cookie = cookieManager.getCookie(req, cookieLangName);
		if (cookie != null) {
			locale = getLocaleFromCookie(cookie);
			if (locale != null) {
				req.getSession().setAttribute(sessionLanguage, locale);
				modifyUser(req, locale);
				cookieManager.modifyCookie(req, resp, cookieLangName,
						locale.toLanguageTag());
				return;
			}
		}

		locale = Locale.ENGLISH;
		req.getSession().setAttribute(sessionLanguage, locale);
		cookieManager.addCookie(resp, cookieLangName, locale.toLanguageTag(),
				60 * 60 * 24 * 365);
	}

	private Locale getLocaleFromCookie(Cookie cookie) {
		Locale locale = null;
		try {
			String[] langCode = cookie.getValue().split("-");
			if (langCode.length == 2)
				locale = new Locale(langCode[0], langCode[1]);
			else
				locale = new Locale(langCode[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locale;
	}

	private UserEntity checkUser(HttpServletRequest req) {
		UserEntity user = (UserEntity) req.getSession().getAttribute(
				sessionUserName);
		return user;
	}

	private void modifyUser(HttpServletRequest req, Locale locale) {
		UserEntity user = (UserEntity) req.getSession().getAttribute(
				sessionUserName);
		if (user != null) {
			user.setLocale(locale);
			try {
				modifyEntity(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void modifyEntity(UserEntity user) throws Exception {
		// modify user
	}

	private Locale checkRequest(HttpServletRequest req) {
		String localeParam = (String) req.getParameter("langParam");
		Locale locale = null;
		if ("enLang".equals(localeParam))
			locale = Locale.ENGLISH;
		if ("ukLang".equals(localeParam))
			locale = new Locale("uk", "UA");
		return locale;
	}
}