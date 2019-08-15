package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Cliente.class)
public class ClienteConverter implements Converter {

	@Inject
	private Clientes clientes;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Cliente retorno = null;
		
		if (StringUtil.isNotEmpty(value)) {
			retorno    = clientes.porCodigo(new Long(value));
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Cliente cliente = (Cliente) value;
			if (cliente.getCodigo() != null) {
				return cliente.getCodigo().toString();
			}
		}
		return "";
	}
	
}
