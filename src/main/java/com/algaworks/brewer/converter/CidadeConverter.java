package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Cidade.class)
public class CidadeConverter implements Converter {

	@Inject
	private Cidades cidades;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Cidade retorno = null;
		
		if (StringUtil.isNotEmpty(value)) {
			retorno    = cidades.porCodigo(new Long(value));
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Cidade cidade = (Cidade) value;
			if (cidade.getCodigo() != null) {
				return cidade.getCodigo().toString();
			}
		}
		return "";
	}
	
}
