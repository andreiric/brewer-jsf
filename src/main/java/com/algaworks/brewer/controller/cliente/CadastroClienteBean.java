package com.algaworks.brewer.controller.cliente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroClienteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Estados estados;
	
	@Inject
	private Cidades cidades;
	
	@Inject
	private CadastroClienteService cadastroClienteService;
	
	private List<TipoPessoa> todosTiposPessoas;
	private List<Estado> todosEstados;
	private List<Cidade> todasCidades;

	private Cliente cliente;

	public CadastroClienteBean() {
		limpar();
	}
	
	private void limpar() {
		cliente = new Cliente();
	}

	public void inicializar() {
		if (cliente == null) {
			limpar();
		}
		
		todosTiposPessoas = new ArrayList<>(Arrays.asList(TipoPessoa.values()));
		todosEstados      = estados.todos();
		todasCidades      = new ArrayList<>();

		if (!cliente.isNovo()) {
			cliente.getEndereco().setEstado(cliente.getEndereco().getCidade().getEstado());
			buscarCidades();
		}
	}
	
	public void buscarCidades() {
		todasCidades.clear();
		if (cliente.getEndereco().getEstado() != null) {
			todasCidades.addAll(cidades.porEstadoCodigo(cliente.getEndereco().getEstado().getCodigo()));
		}
	}
	
	public void salvar() {
		try {
			cadastroClienteService.salvar(cliente);
			limpar();
			
			FacesUtil.mensagemInfo("Cliente salvo com sucesso");
			FacesUtil.redirecionar("/cliente/CadastroCliente");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public List<TipoPessoa> getTodosTiposPessoas() {
		return todosTiposPessoas;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Estado> getTodosEstados() {
		return todosEstados;
	}

	public List<Cidade> getTodasCidades() {
		return todasCidades;
	}
	
	public boolean getDesabilitaCidades() {
		return todasCidades.isEmpty();
	}
	
}
