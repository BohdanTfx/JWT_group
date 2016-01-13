package com.epam.test.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieManager {
	public void addCookie(HttpServletResponse response, String name,
			String value, Integer lifeTime)
	{
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(lifeTime);
		response.addCookie(cookie);
	}

	public void removeCookie(HttpServletResponse response, String name) {
		addCookie(response, name, null, 0);
	}

	public String getCookieValue(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (name.equals(cookie.getName()))
					return cookie.getValue();
		return null;
	}

	public void modifyCookie(HttpServletRequest req, HttpServletResponse resp,
			String name, String value)
	{
		Cookie cookie = getCookie(req, name);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 365);
		cookie.setValue(value);
		resp.addCookie(cookie);
	}

	public Cookie getCookie(HttpServletRequest req, String name) {
		Cookie cookie = null;
		Cookie[] cookies = req.getCookies();
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++)
				if (name.equals(cookies[i].getName()))
					cookie = cookies[i];
		return cookie;
	}
}
