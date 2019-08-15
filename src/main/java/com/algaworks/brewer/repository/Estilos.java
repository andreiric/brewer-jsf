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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Estilos extends CrudRepository<Estilo> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	private TypedQuery<Estilo> query;

	public Estilos() {
		super(Estilo.class);
	}

	public Estilo porNome(String nome) {
		try {
			query = manager.createQuery("from Estilo e where upper(e.nome) = upper(:nome)", Estilo.class);
			query.setParameter("nome", nome);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Estilo> filtrar(EstiloFilter filtro) {
		From<?, ?> orderByFromEntity = null;

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Estilo> criteriaQuery = builder.createQuery(Estilo.class);

		Root<Estilo> estiloRoot = criteriaQuery.from(Estilo.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, estiloRoot);

		criteriaQuery.select(estiloRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = estiloRoot;

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
	
	public int total(EstiloFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Estilo> estiloRoot = criteriaQuery.from(Estilo.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, estiloRoot);

		criteriaQuery.select(builder.count(estiloRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}

	private List<Predicate> criarPredicatesParaFiltro(EstiloFilter filtro, Root<Estilo> estiloRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtil.isNotEmpty(filtro.getNome())) {
			predicates.add(builder.like(estiloRoot.get("nome"), "%" + filtro.getNome() + "%"));
		}

		return predicates;
	}

}
