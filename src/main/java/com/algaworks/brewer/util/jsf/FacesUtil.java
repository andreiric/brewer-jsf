package com.algaworks.brewer.util.jsf;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.algaworks.brewer.util.string.StringUtil;

public class FacesUtil {

	public static void mensagemErro(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem+".", mensagem+"."));
	}
	
	public static void mensagemInfo(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem+"!", mensagem+"!"));
	}
	
	public static void redirecionar(String url) {
		redirecionar(url, "");
	}
	
	public static void redirecionar(String url, String parametros) {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			ec.getFlash().setKeepMessages(true);
			ec.redirect(ec.getRequestContextPath() + url + ".xhtml" + (StringUtil.isNotEmpty(parametros) ? "?"+parametros : ""));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}