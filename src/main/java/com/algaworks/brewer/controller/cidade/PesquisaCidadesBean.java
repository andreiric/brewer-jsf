package com.algaworks.brewer.controller.cidade;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaCidadesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Estados estados;

	@Inject
	private Cidades cidades;
	
	@Inject
	private CadastroCidadeService cadastroCidadeService;

	private List<Estado> todosEstados;
	private CidadeFilter cidadeFilter;
	private LazyDataModel<Cidade> model;
	private Cidade cidadeSelecionada;

	public PesquisaCidadesBean() {
		if (cidadeFilter == null) {
			cidadeFilter = new CidadeFilter();
		}	

		model = new LazyDataModel<Cidade>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Cidade> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				cidadeFilter.setPrimeiroRegistro(first);
				cidadeFilter.setQuantidadeRegistro(pageSize);
				cidadeFilter.setPropriedadeOrdenacao(sortField);
				cidadeFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				
				setRowCount(cidades.total(cidadeFilter));

				return cidades.filtrar(cidadeFilter);
			}
		};
	}
	
	public void inicializar() {
		todosEstados = estados.todos();
	}
	
	public void excluir() {
		try {
			cadastroCidadeService.excluir(cidadeSelecionada);
			
			FacesUtil.mensagemInfo("Cidade excluída com sucesso");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}
	
	public CidadeFilter getCidadeFilter() {
		return cidadeFilter;
	}

	public void setCidadeFilter(CidadeFilter cidadeFilter) {
		this.cidadeFilter = cidadeFilter;
	}

	public List<Estado> getTodosEstados() {
		return todosEstados;
	}

	public LazyDataModel<Cidade> getModel() {
		return model;
	}

	public Cidade getCidadeSelecionada() {
		return cidadeSelecionada;
	}

	public void setCidadeSelecionada(Cidade cidadeSelecionada) {
		this.cidadeSelecionada = cidadeSelecionada;
	}
	
}
