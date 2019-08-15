package com.algaworks.brewer.util.mail;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.mail.HtmlEmail;

public class MailConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public HtmlEmail getConnection() throws IOException {
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("/mail.properties"));
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName(props.getProperty("mail.server.host"));
		email.setSslSmtpPort(props.getProperty("mail.server.port"));
		email.setSSLOnConnect(Boolean.parseBoolean(props.getProperty("mail.enable.ssl")));
		email.setStartTLSRequired(Boolean.parseBoolean(props.getProperty("mail.enable.ssl")));
		if (Boolean.parseBoolean(props.getProperty("mail.auth"))) {
			email.setAuthentication(props.getProperty("mail.username"), props.getProperty("mail.password"));
		}
		
		return email;
	}

}
