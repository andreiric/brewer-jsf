package com.algaworks.brewer.controller.cliente;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.util.jsf.FacesUtil;
import com.algaworks.brewer.util.string.StringUtil;

@Named
@ViewScoped
public class PesquisaRapidaClienteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;

	private String nome;

	private List<Cliente> clientesFiltrados;

	public void pesquisar() {
		if (!StringUtil.isNotEmpty(nome) || nome.length() < 3) {
			FacesUtil.mensagemErro("Informe pelo menos 3 letras na pesquisa");
			return;
		}
		clientesFiltrados = clientes.porNome(nome);
	}

	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("draggable", false);
		opcoes.put("resizable", false);
		opcoes.put("height", 400);

		RequestContext.getCurrentInstance().openDialog("/cliente/PesquisaRapidaCliente", opcoes, null);
	}

	public void selecionar(Cliente cliente) {
		RequestContext.getCurrentInstance().closeDialog(cliente);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Cliente> getClientesFiltrados() {
		return clientesFiltrados;
	}

}
