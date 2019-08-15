package com.algaworks.brewer.controller.venda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;

@Named
@ViewScoped
public class PesquisaVendasBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Vendas vendas;

	private List<StatusVenda> todosStatus;
	private VendaFilter vendaFilter;
	private LazyDataModel<Venda> model;

	public PesquisaVendasBean() {
		if (vendaFilter == null) {
			vendaFilter = new VendaFilter();
		}

		model = new LazyDataModel<Venda>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Venda> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				vendaFilter.setPrimeiroRegistro(first);
				vendaFilter.setQuantidadeRegistro(pageSize);
				vendaFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				vendaFilter.setPropriedadeOrdenacao(sortField);

				setRowCount(vendas.total(vendaFilter));

				return vendas.filtrar(vendaFilter);
			}

		};
	}

	public void inicializar() {
		todosStatus = new ArrayList<>(Arrays.asList(StatusVenda.values()));
	}

	public VendaFilter getVendaFilter() {
		return vendaFilter;
	}

	public void setVendaFilter(VendaFilter vendaFilter) {
		this.vendaFilter = vendaFilter;
	}

	public List<StatusVenda> getTodosStatus() {
		return todosStatus;
	}

	public LazyDataModel<Venda> getModel() {
		return model;
	}

}
