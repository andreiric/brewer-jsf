package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Cerveja.class, value = "cervejaConverter")
public class CervejaConverter implements Converter {

	@Inject
	private Cervejas cervejas;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Cerveja cerveja = null;

		if (StringUtil.isNotEmpty(value)) {
			cerveja = cervejas.porCodigo(new Long(value));
		}

		return cerveja;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Cerveja cerveja = (Cerveja) value;
			if (cerveja.getCodigo() != null) {
				return cerveja.getCodigo().toString();
			}
		}
		return "";
	}

}
