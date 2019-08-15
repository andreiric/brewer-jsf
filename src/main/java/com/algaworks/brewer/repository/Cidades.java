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

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Cidades extends CrudRepository<Cidade> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	private TypedQuery<Cidade> query;
	
	public Cidades() {
		super(Cidade.class);
	}
	
	public List<Cidade> porEstadoCodigo(Long codigoEstado) {
		query = manager.createQuery(
				"from Cidade c where c.estado.codigo = :codigoEstado", 
				Cidade.class);
		query.setParameter("codigoEstado", codigoEstado);
		return query.getResultList();
	}

	public Cidade porEstadoCodigoENome(Long codigoEstado, String nome) {
		try {
			query = manager.createQuery(
					"from Cidade c where c.estado.codigo = :codigoEstado and upper(c.nome) = upper(:nome)", 
					Cidade.class);
			query.setParameter("codigoEstado", codigoEstado);
			query.setParameter("nome", nome);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Cidade> filtrar(CidadeFilter filtro) {
		From<?, ?> orderByFromEntity = null;
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cidade> criteriaQuery = builder.createQuery(Cidade.class);
		
		Root<Cidade> cidadeRoot = criteriaQuery.from(Cidade.class);
		cidadeRoot.fetch("estado", JoinType.INNER);
		
		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, cidadeRoot);
		
		criteriaQuery.select(cidadeRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = cidadeRoot;
			
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
	
	public int total(CidadeFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Cidade> cidadeRoot = criteriaQuery.from(Cidade.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, cidadeRoot);

		criteriaQuery.select(builder.count(cidadeRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}

	private List<Predicate> criarPredicatesParaFiltro(CidadeFilter filtro, Root<Cidade> cidadeRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (filtro.getEstado() != null) {
			predicates.add(builder.equal(cidadeRoot.get("estado"), filtro.getEstado()));
		}
		if (StringUtil.isNotEmpty(filtro.getNome())) {
			predicates.add(builder.like(cidadeRoot.get("nome"), "%" + filtro.getNome() + "%"));
		}

		return predicates;
	}
	
}
