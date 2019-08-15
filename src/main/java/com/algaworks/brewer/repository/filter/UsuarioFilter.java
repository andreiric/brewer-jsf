package com.algaworks.brewer.repository.filter;

import java.util.List;

import com.algaworks.brewer.repository.filter.paginacao.ComumFilter;

public class UsuarioFilter extends ComumFilter {

	private static final long serialVersionUID = 1L;

	private String nome;
	private String email;
	private List<Long> grupos;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Long> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Long> grupos) {
		this.grupos = grupos;
	}

}
