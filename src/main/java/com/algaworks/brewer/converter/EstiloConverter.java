package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Estilo.class)
public class EstiloConverter implements Converter {

	@Inject
	private Estilos estilos;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Estilo retorno = null;
		
		if (StringUtil.isNotEmpty(value)) {
			retorno    = estilos.porCodigo(new Long(value));
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Estilo estilo = (Estilo) value;
			if (estilo.getCodigo() != null) {
				return estilo.getCodigo().toString();
			}
		}
		return "";
	}
	
}
