package com.algaworks.brewer.repository.filter.paginacao;

import java.io.Serializable;

public class ComumFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private int primeiroRegistro;
	private int quantidadeRegistro;
	private String propriedadeOrdenacao;
	private boolean ascendente;

	public int getPrimeiroRegistro() {
		return primeiroRegistro;
	}

	public void setPrimeiroRegistro(int primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}

	public int getQuantidadeRegistro() {
		return quantidadeRegistro;
	}

	public void setQuantidadeRegistro(int quantidadeRegistro) {
		this.quantidadeRegistro = quantidadeRegistro;
	}

	public String getPropriedadeOrdenacao() {
		return propriedadeOrdenacao;
	}

	public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
		this.propriedadeOrdenacao = propriedadeOrdenacao;
	}

	public boolean isAscendente() {
		return ascendente;
	}

	public void setAscendente(boolean ascendente) {
		this.ascendente = ascendente;
	}

}
