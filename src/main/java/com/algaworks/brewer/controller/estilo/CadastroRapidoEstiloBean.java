package com.algaworks.brewer.controller.estilo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroRapidoEstiloBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private CadastroEstiloService cadastroEstiloService;
	
	private Estilo estilo = new Estilo();
	
	public void salvar() {
		try {
			estilo = cadastroEstiloService.salvar(estilo);
			
			fecharDialogo();
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}
	
	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("draggable", false);
		opcoes.put("resizable", false);
		opcoes.put("contentHeight", 190);
		opcoes.put("contentWidth", "100%");
		
		RequestContext.getCurrentInstance().openDialog("/estilo/CadastroRapidoEstilo", opcoes, null);
	}
	
	public void fecharDialogo() {
		RequestContext.getCurrentInstance().closeDialog(estilo);
	}

	public Estilo getEstilo() {
		return estilo;
	}
	
}
