package com.algaworks.brewer.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.util.jpa.Transactional;

public class CadastroCidadeService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Cidades cidades;
	
	@Transactional
	public void salvar(Cidade cidade) throws NegocioException {
		Cidade cidadePresente = cidades.porEstadoCodigoENome(cidade.getEstado().getCodigo(), cidade.getNome());
		
		if (cidade.isNova() && cidadePresente != null && cidadePresente.getCodigo() != null) {
			throw new NegocioException("Nome da Cidade já cadastrado");
		}

		cidades.salvar(cidade);
	}

	@Transactional
	public void excluir(Cidade cidade) throws NegocioException {
		try {
			cidade = cidades.porCodigo(cidade.getCodigo());
			cidades.excluir(cidade);
		} catch (PersistenceException e) {
			throw new NegocioException("Impossível apagar cidade. Já foi utilizada em algum cliente");
		}
	}

}
