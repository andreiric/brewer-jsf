package com.algaworks.brewer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.util.string.StringUtil;

@FacesConverter(forClass = Venda.class)
public class VendaConverter implements Converter {

	@Inject
	private Vendas vendas;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Venda retorno = null;

		if (StringUtil.isNotEmpty(value)) {
			retorno = this.vendas.porCodigo(new Long(value));
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Venda venda =  (Venda) value;
			if (venda.getCodigo() != null) {
				return venda.getCodigo().toString();
			}
		}
		return "";
	}

}
