package com.algaworks.brewer.repository.filter;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.filter.paginacao.ComumFilter;

public class CidadeFilter extends ComumFilter {

	private static final long serialVersionUID = 1L;

	private Estado estado;
	private String nome;

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
