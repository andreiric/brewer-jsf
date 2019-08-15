package com.algaworks.brewer.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.util.jpa.Transactional;

public class CadastroClienteService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	@Transactional
	public void salvar(Cliente cliente) throws NegocioException {
		Cliente clientePresente = clientes.porCpfOuCnpj(cliente.getCpfOuCnpjSemMascara());
		
		if (cliente.isNovo() && clientePresente != null && clientePresente.getCodigo() != null) {
			throw new NegocioException((cliente.getTipoPessoa() == TipoPessoa.FISICA ? "CPF" : "CNPJ") + " já cadastrado");
		}
		
		clientes.salvar(cliente);
	}
	
	@Transactional
	public void excluir(Cliente cliente) throws NegocioException {
		try {
			cliente = clientes.porCodigo(cliente.getCodigo());
			clientes.excluir(cliente);
		} catch (PersistenceException e) {
			throw new NegocioException("Impossível apagar cliente. Já foi utilizado em alguma venda");
		}
	}

}
