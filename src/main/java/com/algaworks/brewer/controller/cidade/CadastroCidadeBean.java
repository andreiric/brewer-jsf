package com.algaworks.brewer.controller.cidade;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroCidadeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Estados estados;

	@Inject
	private CadastroCidadeService cadastroCidadeService;
	
	private List<Estado> todosEstados;

	private Cidade cidade;

	public CadastroCidadeBean() {
		limpar();
	}

	private void limpar() {
		cidade = new Cidade();
	}

	public void inicializar() {
		if (cidade == null) {
			limpar();
		}

		todosEstados = estados.todos();
	}
	
	public void salvar() {
		try {
			cadastroCidadeService.salvar(cidade);
			limpar();
			
			FacesUtil.mensagemInfo("Cidade salva com sucesso");
			FacesUtil.redirecionar("/cidade/CadastroCidade");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public List<Estado> getTodosEstados() {
		return todosEstados;
	}

}
