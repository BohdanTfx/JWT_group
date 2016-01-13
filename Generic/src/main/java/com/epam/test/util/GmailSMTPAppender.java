package com.epam.test.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.sun.mail.smtp.SMTPTransport;

public class GmailSMTPAppender extends SMTPAppender {
	protected Session session;

	public GmailSMTPAppender() {
		super();
	}

	protected Session createSession() {
		Properties props = new Properties();
		props.put("mail.smtps.host", getSMTPHost());
		props.put("mail.smtps.auth", "true");

		Authenticator auth = null;
		if (getSMTPPassword() != null && getSMTPUsername() != null) {
			auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(getSMTPUsername(),
							getSMTPPassword());
				}
			};
		}
		session = Session.getInstance(props, auth);
		if (getSMTPProtocol() != null) {
			session.setProtocolForAddress("rfc822", getSMTPProtocol());
		}
		if (getSMTPDebug()) {
			session.setDebug(getSMTPDebug());
		}
		return session;
	}

	protected void sendBuffer() {
		try {
			MimeBodyPart part = new MimeBodyPart();

			StringBuffer sbuf = new StringBuffer();
			String t = layout.getHeader();
			if (t != null)
				sbuf.append(t);
			int len = cb.length();
			for (int i = 0; i < len; i++) {
				LoggingEvent event = cb.get();
				sbuf.append(layout.format(event));
				if (layout.ignoresThrowable()) {
					String[] s = event.getThrowableStrRep();
					if (s != null) {
						for (int j = 0; j < s.length; j++) {
							sbuf.append(s[j]);
							sbuf.append(Layout.LINE_SEP);
						}
					}
				}
			}
			t = layout.getFooter();
			if (t != null)
				sbuf.append(t);
			part.setContent(sbuf.toString(), layout.getContentType());

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(part);
			msg.setContent(mp);

			msg.setSentDate(new Date());
			send(msg);
		} catch (Exception e) {
			LogLog.error("Error occured while sending e-mail notification.", e);
		}
	}

	protected void send(Message msg) throws MessagingException {
		SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
		try {
			t.connect(getSMTPHost(), getSMTPUsername(), getSMTPPassword());
			t.sendMessage(msg, msg.getAllRecipients());
		} finally {
			System.out.println("Response: " + t.getLastServerResponse());
			t.close();
		}
	}
}
