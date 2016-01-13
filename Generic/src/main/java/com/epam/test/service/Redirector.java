package com.epam.test.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class Redirector implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getRequestURI().substring(
				req.getContextPath().length());
		if (path.startsWith("/testsystem/") || path.startsWith("/resources/")) {
			chain.doFilter(request, response);
		} else {
			request.getRequestDispatcher("/testsystem/" + path).forward(request,
					response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}
