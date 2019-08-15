package com.algaworks.brewer.controller.layout;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class MenuLateralBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String uri;
	
	public String getUri() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		uri = request.getRequestURI().substring(11);
		return uri;
	}

}
