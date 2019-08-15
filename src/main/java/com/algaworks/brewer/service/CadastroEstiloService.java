package com.algaworks.brewer.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.util.jpa.Transactional;

public class CadastroEstiloService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Estilos estilos;
	
	@Transactional
	public Estilo salvar(Estilo estilo) throws NegocioException {
		Estilo estiloPresente = estilos.porNome(estilo.getNome());
		
		if (estilo.isNovo() && estiloPresente != null && estiloPresente.getCodigo() != null) {
			throw new NegocioException("Nome do estilo já cadastrado");
		}
		
		return estilos.salvar(estilo);
	}

	@Transactional
	public void excluir(Estilo estilo) throws NegocioException {
		try {
			estilo = estilos.porCodigo(estilo.getCodigo());
			estilos.excluir(estilo);
		} catch (PersistenceException e) {
			throw new NegocioException("Impossível apagar estilo. Já foi utilizado em alguma cerveja");
		}
	}

}
