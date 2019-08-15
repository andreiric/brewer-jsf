package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Estado.class)
public class EstadoConverter implements Converter {

	@Inject
	private Estados estados;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Estado retorno = null;
		
		if (StringUtil.isNotEmpty(value)) {
			retorno    = estados.porCodigo(new Long(value));
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Estado estado = (Estado) value;
			if (estado.getCodigo() != null) {
				return estado.getCodigo().toString();
			}
		}
		return "";
	}
	
}
