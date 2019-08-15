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

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Clientes extends CrudRepository<Cliente> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	private TypedQuery<Cliente> query;

	public Clientes() {
		super(Cliente.class);
	}
	
	public Cliente porCpfOuCnpj(String cpfOuCnpj) {
		try {
			query = manager.createQuery(
						"from Cliente c where c.cpfOuCnpj = :cpfOuCnpj", 
						Cliente.class);
			query.setParameter("cpfOuCnpj", cpfOuCnpj);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Cliente> porNome(String nome) {
		try {
			query = manager.createQuery(
						"from Cliente c where upper(c.nome) like upper(:nome)",
						Cliente.class);
			query.setParameter("nome", nome + "%");
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Cliente> filtrar(ClienteFilter filtro) {
		From<?, ?> orderByFromEntity = null;

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cliente> criteriaQuery = builder.createQuery(Cliente.class);
		
		Root<Cliente> clienteRoot = criteriaQuery.from(Cliente.class);
		clienteRoot.fetch("endereco")
				   .fetch("cidade", JoinType.LEFT)
				   .fetch("estado", JoinType.LEFT);
		
		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, clienteRoot);

		criteriaQuery.select(clienteRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = clienteRoot;

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

	public int total(ClienteFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Cliente> clienteRoot = criteriaQuery.from(Cliente.class);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, clienteRoot);

		criteriaQuery.select(builder.count(clienteRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}
	
	private List<Predicate> criarPredicatesParaFiltro(ClienteFilter filtro, Root<Cliente> clienteRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtil.isNotEmpty(filtro.getNome())) {
			predicates.add(builder.like(clienteRoot.get("nome"), "%" + filtro.getNome() + "%"));
		}
		if (StringUtil.isNotEmpty(filtro.getCpfOuCnpj())) {
			predicates.add(builder.equal(clienteRoot.get("cpfOuCnpj"), filtro.getCpfOuCnpj()));
		}

		return predicates;
	}

}
