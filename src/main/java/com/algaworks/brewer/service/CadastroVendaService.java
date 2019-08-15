package com.algaworks.brewer.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import javax.inject.Inject;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.security.Seguranca;
import com.algaworks.brewer.util.jpa.Transactional;

public class CadastroVendaService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Vendas vendas;
	
	@Inject
	private Seguranca seguranca;
	
	@Inject
	private Cervejas cervejas;

	@Transactional
	public Venda salvar(Venda venda) throws NegocioException {
		validarVenda(venda);
		
		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		} else {
			Venda vendaExistente = vendas.porCodigo(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		if (venda.getDataEntrega() != null) {
			LocalDate dataEntrega = venda.getDataEntrega().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalTime horarioEntraga = venda.getHorarioEntrega() == null ? LocalTime.NOON : venda.getHorarioEntrega().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
			venda.setDataHoraEntrega(LocalDateTime.of(dataEntrega, horarioEntraga));
		}
		
		return vendas.salvar(venda);
	}

	@Transactional
	public void emitir(Venda venda) throws NegocioException {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);

		atualizarEstoque(venda);
	}
	
	@Transactional
	public void cancelar(Venda venda) throws AccessDeniedException {
		if (venda.getUsuario().equals(seguranca.getUsuarioSistema().getUsuario()) || 
				seguranca.getUsuarioSistema().getAuthorities()
					.stream().filter(ga -> ga.getAuthority().equals("CANCELAR_VENDA")).findAny().isPresent()) {
			venda.setStatus(StatusVenda.CANCELADA);
			salvar(venda);
		} else {
			throw new AccessDeniedException("");
		}
	}

	private void validarVenda(Venda venda) {
		if (venda.getCliente() == null) {
			throw new NegocioException("Selecione um cliente na pesquisa rápida");
		}
		if (venda.getItens().isEmpty()) {
			throw new NegocioException("Adicione pelo menos uma cerveja na venda");
		}
		if (venda.getHorarioEntrega() != null && venda.getDataEntrega() == null) {
			throw new NegocioException("Informe uma data da entrega para um horário");
		}
		if (venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			throw new NegocioException("Valor Total não pode ser negativo");
		}
	}
	
	private void atualizarEstoque(Venda venda) {
		for (ItemVenda itemVenda : venda.getItens()) {
			Cerveja cerveja = cervejas.porCodigo(itemVenda.getCerveja().getCodigo());
			cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() - itemVenda.getQuantidade());
			cervejas.salvar(cerveja);
		}
	}

}
