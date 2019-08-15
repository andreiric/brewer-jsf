package com.algaworks.brewer.dto;

import java.math.BigDecimal;

public class ValorItensEstoque {

	private BigDecimal valor;
	private Long quantidade;

	public ValorItensEstoque(BigDecimal valor, Long quantidade) {
		this.valor = valor;
		this.quantidade = quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

}
