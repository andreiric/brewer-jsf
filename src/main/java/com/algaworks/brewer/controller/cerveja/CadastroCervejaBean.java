package com.algaworks.brewer.controller.cerveja;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroCervejaBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Estilos estilos;
	
	@Inject
	private CadastroCervejaService cadastroCervejaService;
	
	@Inject
	private FotoStorage fotoStorage;

	private List<Sabor> todosSabores;  
	private List<Origem> todasOrigens;
	private List<Estilo> todosEstilos;
	
	private Cerveja cerveja;
	
	public CadastroCervejaBean() {
		limpar();
	}
	
	private void limpar() {
		cerveja  = new Cerveja();
	}
	
	private void carregarEstilos() {
		todosEstilos = estilos.todos();
	}

	public void inicializar() {
		if (cerveja == null) {
			limpar();
		}

		todosSabores = new ArrayList<>(Arrays.asList(Sabor.values()));
		todasOrigens = new ArrayList<>(Arrays.asList(Origem.values()));
		carregarEstilos();
	}

	public void salvar() {
		try {
			cadastroCervejaService.salvar(cerveja);
			limpar();

			FacesUtil.mensagemInfo("Cerveja salva com sucesso");
			FacesUtil.redirecionar("/cerveja/CadastroCerveja");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public void estiloCadastrado(SelectEvent event) {
		if (event.getObject() != null) {
			Estilo estilo = (Estilo) event.getObject();
			
			if (estilo.getCodigo() != null && !"".equals(estilo.getCodigo())) {
				carregarEstilos();
				cerveja.setEstilo(estilo);
			}
		}
	}
	
	public void uploadFoto(FileUploadEvent event) throws IOException {
		String nomeFoto    = fotoStorage.salvar(event.getFile());
		String contentType = event.getFile().getContentType();
		cerveja.setFoto(nomeFoto);
		cerveja.setContentType(contentType);
	}
	
	public void removerFoto() {
		if (cerveja.temFoto()) {
			fotoStorage.excluir(cerveja.getFoto());
			cerveja.setFoto(null);
			cerveja.setContentType(null);
		}
	}
	
	public String getUrlFoto() {
		return fotoStorage.getUrl(cerveja.getFoto());
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

	public Cerveja getCerveja() {
		return cerveja;
	}

	public void setCerveja(Cerveja cerveja) {
		this.cerveja = cerveja;
	}

	
}
