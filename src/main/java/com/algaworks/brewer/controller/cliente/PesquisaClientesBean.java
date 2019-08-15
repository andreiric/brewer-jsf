package com.algaworks.brewer.controller.cliente;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaClientesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;

	@Inject
	private CadastroClienteService cadastroClienteService;
	
	private ClienteFilter clienteFilter;
	private LazyDataModel<Cliente> model;
	private Cliente clienteSelecionado;

	public PesquisaClientesBean() {
		if (clienteFilter == null) {
			clienteFilter = new ClienteFilter();
		}
		
		model = new LazyDataModel<Cliente>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Cliente> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				clienteFilter.setPrimeiroRegistro(first);
				clienteFilter.setQuantidadeRegistro(pageSize);
				clienteFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				clienteFilter.setPropriedadeOrdenacao(sortField);
				
				setRowCount(clientes.total(clienteFilter));
				
				return clientes.filtrar(clienteFilter);
			}
		};
	}
	
	public void excluir() {
		try {
			cadastroClienteService.excluir(clienteSelecionado);
			
			FacesUtil.mensagemInfo("Cliente excluído com sucesso");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}
	
	public ClienteFilter getClienteFilter() {
		return clienteFilter;
	}

	public void setClienteFilter(ClienteFilter clienteFilter) {
		this.clienteFilter = clienteFilter;
	}

	public LazyDataModel<Cliente> getModel() {
		return model;
	}

	public Cliente getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}
	
}
