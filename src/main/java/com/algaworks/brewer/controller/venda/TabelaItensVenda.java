package com.algaworks.brewer.controller.venda;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

public class TabelaItensVenda implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ItemVenda> itens = new ArrayList<>();
	
	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}

	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			itens.add(itemVenda);
		}
	}
	
	public int total() {
		return itens.size();
	}
	
	public void excluitItem(Cerveja cerveja) {
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				.findAny()
				.getAsInt();
		itens.remove(indice);
	}
	
	public List<ItemVenda> getItens() {
		return itens;
	}
	
	public boolean isTabelaVazia() {
		return itens.isEmpty();
	}

	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}

}
