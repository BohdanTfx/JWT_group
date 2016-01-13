package com.epam.test.security.element;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AccessTag extends SimpleTagSupport {
	private String role;

	@Override
	public void doTag() throws JspException, IOException {
		if (checkAccess())
			allowShowBody();
	}

	private boolean checkAccess() {
		String currentRole = getRole();
		if (role == null)
			return false;
		if (!role.startsWith("not_"))
			return currentRole.equals(role);
		return !role.substring(4).equals(currentRole);
	}

	private String getRole() {
		String role = "";
		PageContext pageContext = (PageContext) getJspContext();
		HttpSession session = ((HttpServletRequest) pageContext.getRequest())
				.getSession();
		Object user = session.getAttribute("user");
		// check type
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	private void allowShowBody() {
		try {
			PageContext pageContext = (PageContext) getJspContext();
			JspWriter out = pageContext.getOut();

			StringWriter sw = new StringWriter();
			JspFragment body = getJspBody();
			body.invoke(sw);

			out.println(sw.toString());
		} catch (IOException | JspException e) {
			e.printStackTrace();
		}
	}
}
