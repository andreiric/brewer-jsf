package com.algaworks.brewer.controller.estilo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaEstilosBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Estilos estilos;
	
	@Inject
	private CadastroEstiloService cadastroEstiloService;
	
	private EstiloFilter estiloFilter;
	private LazyDataModel<Estilo> model;
	private Estilo estiloSelecionado;
	
	public PesquisaEstilosBean() {
		if (estiloFilter == null) {
			estiloFilter = new EstiloFilter();
		}
		
		model = new LazyDataModel<Estilo>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Estilo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				estiloFilter.setPrimeiroRegistro(first);
				estiloFilter.setQuantidadeRegistro(pageSize);
				estiloFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				estiloFilter.setPropriedadeOrdenacao(sortField);
				
				setRowCount(estilos.total(estiloFilter));
				
				return estilos.filtrar(estiloFilter);
			}
		};
	}
	
	public void excluir() {
		try {
			cadastroEstiloService.excluir(estiloSelecionado);
			
			FacesUtil.mensagemInfo("Estilo excluído com sucesso");			
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public EstiloFilter getEstiloFilter() {
		return estiloFilter;
	}

	public void setEstiloFilter(EstiloFilter estiloFilter) {
		this.estiloFilter = estiloFilter;
	}

	public LazyDataModel<Estilo> getModel() {
		return model;
	}

	public Estilo getEstiloSelecionado() {
		return estiloSelecionado;
	}

	public void setEstiloSelecionado(Estilo estiloSelecionado) {
		this.estiloSelecionado = estiloSelecionado;
	}
	
	
}
