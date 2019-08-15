package com.algaworks.brewer.controller.estilo;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroEstiloBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CadastroEstiloService cadastroEstiloService;
	
	private Estilo estilo;
	
	public CadastroEstiloBean() {
		limpar();
	}
	
	private void limpar() {
		estilo = new Estilo();
	}

	public void inicializar() {
		if (estilo == null) {
			limpar();
		}
	}
	
	public void salvar() {
		try {
			cadastroEstiloService.salvar(estilo);
			limpar();
			
			FacesUtil.mensagemInfo("Estilo salvo com sucesso");
			FacesUtil.redirecionar("/estilo/CadastroEstilo");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public Estilo getEstilo() {
		return estilo;
	}

	public void setEstilo(Estilo estilo) {
		this.estilo = estilo;
	}
	
}
