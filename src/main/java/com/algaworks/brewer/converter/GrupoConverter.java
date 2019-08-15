package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Grupo.class)
public class GrupoConverter implements Converter {

	@Inject
	private Grupos grupos;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Grupo retorno = null;
		
		if (StringUtil.isNotEmpty(value)) {
			retorno    = grupos.porCodigo(new Long(value));
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Grupo grupo = (Grupo) value;
			if (grupo.getCodigo() != null) {
				return grupo.getCodigo().toString();
			}
		}
		return "";
	}
	
}
