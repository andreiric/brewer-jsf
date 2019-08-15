package com.algaworks.brewer.controller.cerveja;

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

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaCervejasBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Estilos estilos;

	@Inject
	private Cervejas cervejas;

	@Inject
	private FotoStorage fotoStorage;
	
	@Inject
	private CadastroCervejaService cadastroCervejaService;

	private List<Sabor> todosSabores;
	private List<Origem> todasOrigens;
	private List<Estilo> todosEstilos;
	private CervejaFilter cervejaFilter;
	private LazyDataModel<Cerveja> model;
	private Cerveja cervejaSelecionada;
	
	public PesquisaCervejasBean() {
		if (cervejaFilter == null) {
			cervejaFilter = new CervejaFilter();
		}

		model = new LazyDataModel<Cerveja>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Cerveja> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				cervejaFilter.setPrimeiroRegistro(first);
				cervejaFilter.setQuantidadeRegistro(pageSize);
				cervejaFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				cervejaFilter.setPropriedadeOrdenacao(sortField);
				
				setRowCount(cervejas.total(cervejaFilter));
				
				return cervejas.filtrar(cervejaFilter);
			}
		};
	}
	
	public void inicializar() {
		todosSabores = new ArrayList<>(Arrays.asList(Sabor.values()));
		todasOrigens = new ArrayList<>(Arrays.asList(Origem.values()));
		todosEstilos = estilos.todos();
	}

	public String getUrlThumbnailFoto(String nomeFoto) {
		return fotoStorage.getUrlThumbnail(nomeFoto);
	}
	
	public void excluir() {
		try {
			cadastroCervejaService.excluir(cervejaSelecionada);
			
			FacesUtil.mensagemInfo("Cerveja excluída com sucesso");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public List<Sabor> getTodosSabores() {
		return todosSabores;
	}

	public List<Origem> getTodasOrigens() {
		return todasOrigens;
	}

	public List<Estilo> getTodosEstilos() {
		return todosEstilos;
	}

	public CervejaFilter getCervejaFilter() {
		return cervejaFilter;
	}

	public void setCervejaFilter(CervejaFilter cervejaFilter) {
		this.cervejaFilter = cervejaFilter;
	}

	public LazyDataModel<Cerveja> getModel() {
		return model;
	}

	public Cerveja getCervejaSelecionada() {
		return cervejaSelecionada;
	}

	public void setCervejaSelecionada(Cerveja cervejaSelecionada) {
		this.cervejaSelecionada = cervejaSelecionada;
	}

}
