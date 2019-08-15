package com.algaworks.brewer.repository.filter;

import com.algaworks.brewer.repository.filter.paginacao.ComumFilter;

public class ClienteFilter extends ComumFilter {

	private static final long serialVersionUID = 1L;

	private String nome;
	private String cpfOuCnpj;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

}
