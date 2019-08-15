package com.algaworks.brewer.repository.filter;

import com.algaworks.brewer.repository.filter.paginacao.ComumFilter;

public class EstiloFilter extends ComumFilter {

	private static final long serialVersionUID = 1L;

	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
