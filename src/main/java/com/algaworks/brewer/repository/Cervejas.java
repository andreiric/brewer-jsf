package com.algaworks.brewer.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.algaworks.brewer.dto.ValorItensEstoque;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Cervejas extends CrudRepository<Cerveja> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	private TypedQuery<Cerveja> query;

	public Cervejas() {
		super(Cerveja.class);
	}

	public Cerveja porSku(String sku) {
		try {
			query = manager.createQuery("from Cerveja c where upper(c.sku) = upper(:sku)", Cerveja.class);
			query.setParameter("sku", sku);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Cerveja> porSkuOuNome(String skuOuNome) {
		query = manager.createQuery(
					"from Cerveja c where lower(c.sku) like lower(:skuOuNome) or lower(c.nome) like lower(:skuOuNome)", 
					Cerveja.class);
		query.setParameter("skuOuNome", skuOuNome+"%");
		return query.getResultList();
	}

	public ValorItensEstoque valorItensEstoque() {
		String jpql = "select new com.algaworks.brewer.dto.ValorItensEstoque(coalesce(sum(valor * quantidadeEstoque),0), coalesce(sum(quantidadeEstoque),0)) from Cerveja";
		return manager.createQuery(jpql, ValorItensEstoque.class).getSingleResult();
	}
	
	public List<Cerveja> filtrar(CervejaFilter filtro) {
		From<?, ?> orderByFromEntity = null;

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cerveja> criteriaQuery = builder.createQuery(Cerveja.class);

		Root<Cerveja> cervejaRoot = criteriaQuery.from(Cerveja.class);
		cervejaRoot.fetch("estilo", JoinType.INNER);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, cervejaRoot);

		criteriaQuery.select(cervejaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = cervejaRoot;

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

		query = manager.createQuery(criteriaQuery);
		query.setFirstResult(filtro.getPrimeiroRegistro());
		query.setMaxResults(filtro.getQuantidadeRegistro());

		return query.getResultList();
	}

	public int total(CervejaFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Cerveja> cervejaRoot = criteriaQuery.from(Cerveja.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, cervejaRoot);

		criteriaQuery.select(builder.count(cervejaRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}

	private List<Predicate> criarPredicatesParaFiltro(CervejaFilter filtro, Root<Cerveja> cervejaRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtil.isNotEmpty(filtro.getSku())) {
			predicates.add(builder.equal(cervejaRoot.get("sku"), filtro.getSku()));
		}
		if (StringUtil.isNotEmpty(filtro.getNome())) {
			predicates.add(builder.like(cervejaRoot.get("nome"), "%" + filtro.getNome() + "%"));
		}
		if (isEstiloPresente(filtro)) {
			predicates.add(builder.equal(cervejaRoot.get("estilo"), filtro.getEstilo()));
		}
		if (filtro.getSabor() != null) {
			predicates.add(builder.equal(cervejaRoot.get("sabor"), filtro.getSabor()));
		}
		if (filtro.getOrigem() != null) {
			predicates.add(builder.equal(cervejaRoot.get("origem"), filtro.getOrigem()));
		}
		if (filtro.getValorDe() != null) {
			predicates.add(builder.ge(cervejaRoot.get("valor"), filtro.getValorDe()));
		}
		if (filtro.getValorAte() != null) {
			predicates.add(builder.le(cervejaRoot.get("valor"), filtro.getValorAte()));
		}

		return predicates;
	}

	private boolean isEstiloPresente(CervejaFilter filter) {
		return filter.getEstilo() != null && filter.getEstilo().getCodigo() != null;
	}

}
