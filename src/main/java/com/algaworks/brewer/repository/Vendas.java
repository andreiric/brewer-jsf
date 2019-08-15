package com.algaworks.brewer.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Vendas extends CrudRepository<Venda> {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	private TypedQuery<Venda> query;
	
	public Vendas() {
		super(Venda.class);
	}

	public Venda buscarComItens(Long codigo) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> criteriaQuery = builder.createQuery(Venda.class);
		
		Root<Venda> vendaRoot = criteriaQuery.from(Venda.class);
		vendaRoot.fetch("itens", JoinType.LEFT);
		
		criteriaQuery.select(vendaRoot);
		criteriaQuery.where(builder.equal(vendaRoot.get("codigo"), codigo));
		
		query = manager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}
	
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
					.setParameter("mes", MonthDay.now().getMonth().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	public BigDecimal ticketMedio() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@SuppressWarnings("unchecked")
	public List<VendaMes> totalPorMes() {
		List<Object[]> results = manager.createNativeQuery(VendaMes.SQL).getResultList();
		
		List<VendaMes> vendasMes = new ArrayList<>();
		results.stream().forEach(r -> vendasMes.add(new VendaMes((String) r[0], ((BigInteger) r[1]).intValue())));
		
		LocalDate hoje = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal      = String.format("%d/%02d", hoje.getYear(), hoje.getMonth().getValue());
			
			boolean possuiMes    = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasMes.add(i - 1, new VendaMes(mesIdeal, 0));
			}
			
			hoje                 = hoje.minusMonths(1);
		} 
		
		return vendasMes;
	}

	@SuppressWarnings("unchecked")
	public List<VendaOrigem> totalPorOrigem() {
		List<Object[]> results = manager.createNativeQuery(VendaOrigem.SQL).getResultList();

		List<VendaOrigem> vendasOrigem = new ArrayList<>();
		results.stream().forEach(r -> vendasOrigem.add(new VendaOrigem((String) r[0], Integer.valueOf((String) r[1]), Integer.valueOf((String) r[2]))));
		
		LocalDate hoje           = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal      = String.format("%d/%02d", hoje.getYear(), hoje.getMonth().getValue());
			
			boolean possuiMes    = vendasOrigem.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasOrigem.add(i - 1, new VendaOrigem(mesIdeal, 0, 0));
			}
			
			hoje                 = hoje.minusMonths(1);
		}
		
		return vendasOrigem;
	}
	
	public List<Venda> filtrar(VendaFilter filtro) {
		From<?, ?> orderByFromEntity = null;

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> criteriaQuery = builder.createQuery(Venda.class);

		Root<Venda> vendaRoot = criteriaQuery.from(Venda.class);
		vendaRoot.fetch("cliente", JoinType.INNER).fetch("endereco").fetch("cidade", JoinType.INNER);
		vendaRoot.fetch("usuario", JoinType.INNER);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, vendaRoot);

		criteriaQuery.select(vendaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = vendaRoot;

			if (nomePropriedadeOrdenacao.equals("cliente.nome")) {
				if (filtro.isAscendente() && filtro.getPropriedadeOrdenacao() != null) {
					criteriaQuery.orderBy(builder.asc(orderByFromEntity.get("cliente").get("nome")));
				} else if (filtro.getPropriedadeOrdenacao() != null) {
					criteriaQuery.orderBy(builder.desc(orderByFromEntity.get("cliente").get("nome")));
				}
			} else {
				if (filtro.getPropriedadeOrdenacao().contains(".")) {
					nomePropriedadeOrdenacao = nomePropriedadeOrdenacao
							.substring(filtro.getPropriedadeOrdenacao().indexOf(".") + 1);
				}
				
				if (filtro.isAscendente() && filtro.getPropriedadeOrdenacao() != null) {
					criteriaQuery.orderBy(builder.asc(orderByFromEntity.get(nomePropriedadeOrdenacao)));
				} else if (filtro.getPropriedadeOrdenacao() != null) {
					criteriaQuery.orderBy(builder.desc(orderByFromEntity.get(nomePropriedadeOrdenacao)));
				}
			}
		}

		query = manager.createQuery(criteriaQuery);
		query.setFirstResult(filtro.getPrimeiroRegistro());
		query.setMaxResults(filtro.getQuantidadeRegistro());

		return query.getResultList();
	}

	public int total(VendaFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Venda> vendaRoot = criteriaQuery.from(Venda.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, vendaRoot);

		criteriaQuery.select(builder.count(vendaRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}

	private List<Predicate> criarPredicatesParaFiltro(VendaFilter filtro, Root<Venda> vendaRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (filtro.getCodigo() != null) {
			predicates.add(builder.equal(vendaRoot.get("codigo"), filtro.getCodigo()));
		}
		if (filtro.getStatus() != null) {
			predicates.add(builder.equal(vendaRoot.get("status"), filtro.getStatus()));
		}
		if (filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
			predicates.add(builder.between(vendaRoot.get("dataCriacao") , 
					filtro.getDataInicial().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), 
					filtro.getDataFinal().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
		}
		if (filtro.getValorInicial() != null) {
			predicates.add(builder.ge(vendaRoot.get("valorTotal"), filtro.getValorInicial()));
		}
		if (filtro.getValorFinal() != null) {
			predicates.add(builder.le(vendaRoot.get("valorTotal"), filtro.getValorFinal()));
		}
		if (StringUtil.isNotEmpty(filtro.getNomeCliente())) {
			predicates.add(builder.like(vendaRoot.get("cliente").get("nome"), "%" + filtro.getNomeCliente() + "%"));
		}
		if (StringUtil.isNotEmpty(filtro.getCpfOuCnpjCliente())) {
			predicates.add(builder.equal(vendaRoot.get("cliente").get("cpfOuCnpj"), filtro.getCpfOuCnpjCliente()));
		}

		return predicates;
	}

}
