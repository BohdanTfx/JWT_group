package com.epam.test.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.epam.test.context.ApplicationContext;
import com.epam.test.service.LanguageManager;
import com.epam.test.util.EnvironmentVariablesManager;

public class FrontController extends HttpServlet {
	private final static Logger LOG = Logger.getLogger(FrontController.class);
	private static final long serialVersionUID = 1L;
	private Map<String, HttpServlet> controllers = new HashMap<>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info(getClass().getSimpleName() + " - " + "init");
		super.init(config);
		configControllers();
	}

	private void configControllers() {
		LOG.info(getClass().getSimpleName() + " - " + "configControllers");
		StudentController studentController = (StudentController) ApplicationContext
				.getInstance().getBean("studentController");
		AdminController adminController = (AdminController) ApplicationContext
				.getInstance().getBean("adminController");
		TeacherController teacherController = (TeacherController) ApplicationContext
				.getInstance().getBean("teacherController");

		controllers.put("student/", studentController);
		controllers.put("admin/", adminController);
		controllers.put("teacher/", teacherController);

		LanguageManager languageManager = (LanguageManager) ApplicationContext
				.getInstance().getBean("languageManager");
		controllers.put("lang", languageManager);
	}

	public static String getPath(HttpServletRequest request) {
		return request.getRequestURI()
				.substring(request.getContextPath().length())
				.substring("/testsystem/".length());
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		LOG.info(getClass().getSimpleName() + " - " + "doGet");
		request.setCharacterEncoding("UTF-8");
		try {
			if (request.getSession().isNew())
				configAutoAuthentication(request.getSession());

			String path = request.getRequestURI().substring(
					request.getContextPath().length());

			if (path.startsWith("/testsystem/")) {
				path = path.substring("/testsystem/".length());
				if ("".equals(path))
					path = "main";

				controllers.get("lang").service(request, response);
				if (path.startsWith("admin/")) {
					controllers.get("admin/").service(request, response);
					return;
				}
				if (path.startsWith("student/")) {
					controllers.get("student/").service(request, response);
					return;
				}
				if (path.startsWith("teacher/")) {
					controllers.get("teacher/").service(request, response);
					return;
				}
				if (path.equals("language/bundle")) {
					sendBundle(request, response);
					return;
				}
				request.getRequestDispatcher("/views/" + path + ".jsp")
						.forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal(getClass().getSimpleName() + " - " + "doGet");
		}
	}

	private void sendBundle(HttpServletRequest request,
			HttpServletResponse response)
	{
		LOG.info(getClass().getSimpleName() + " - " + "sendBundle");
		Locale locale = ((Locale) request.getSession().getAttribute("language"));
		ResourceBundle bundle = null;
		if (locale != null)
			bundle = ResourceBundle.getBundle("/i18n/language", locale);
		else
			bundle = ResourceBundle.getBundle("/i18n/language", Locale.ENGLISH);

		Map<String, String> bundleMap = new HashMap<>();

		for (String key : bundle.keySet()) {
			String value = bundle.getString(key);
			bundleMap.put(key, value);
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try (PrintWriter out = response.getWriter()) {
			out.print(new ObjectMapper().writeValueAsString(bundleMap));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void configAutoAuthentication(HttpSession session) {
		LOG.info(getClass().getSimpleName() + " - "
				+ "configAutoAuthentication");
		EnvironmentVariablesManager manager = EnvironmentVariablesManager
				.getInstance();
		session.setAttribute(manager.getVar("session.student.autoauth"), 1);
		session.setAttribute(manager.getVar("session.admin.autoauth"), 1);
		session.setAttribute(manager.getVar("session.teacher.autoauth"), 1);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		LOG.info(getClass().getSimpleName() + " - " + "doPost");
		request.setCharacterEncoding("UTF-8");
		try {
			String path = request.getRequestURI().substring(
					request.getContextPath().length());

			if (path.startsWith("/testsystem/")) {
				path = path.substring("/testsystem/".length());
				if (path.startsWith("admin/")) {
					controllers.get("admin/").service(request, response);
					return;
				}
				if (path.startsWith("student/")) {
					controllers.get("student/").service(request, response);
					return;
				}
				if (path.startsWith("teacher/")) {
					controllers.get("teacher/").service(request, response);
					return;
				}
				controllers.get(path).service(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal(getClass().getSimpleName() + " - " + "doPost");
		}
	}

	@Override
	public void destroy() {
		LOG.info(getClass().getSimpleName() + " - " + "destroy");
		super.destroy();
		for (Entry<String, HttpServlet> controller : controllers.entrySet()) {
			controller.getValue().destroy();
		}
	}
}
