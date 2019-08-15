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

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.crud.CrudRepository;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.util.string.StringUtil;

public class Usuarios extends CrudRepository<Usuario> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	private TypedQuery<Usuario> query;
	
	public Usuarios() {
		super(Usuario.class);
	}
	
	public Usuario porEmail(String email) {
		try {
			query = manager.createQuery(
					"from Usuario u where lower(u.email) = lower(:email)", 
					Usuario.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Usuario porEmailEAtivo(String email) {
		try {
			query = manager.createQuery(
					"from Usuario u where lower(u.email) = lower(:email) and ativo = :ativo", 
					Usuario.class);
			query.setParameter("email", email);
			query.setParameter("ativo", true);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<String> permissoes(Usuario usuario) {
		return manager.createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}


	public List<Usuario> filtrar(UsuarioFilter filtro) {
		From<?, ?> orderByFromEntity = null;

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteriaQuery = builder.createQuery(Usuario.class);

		Root<Usuario> usuarioRoot = criteriaQuery.from(Usuario.class);
		
		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, usuarioRoot);

		criteriaQuery.select(usuarioRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = usuarioRoot;

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

	public int total(UsuarioFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<Usuario> usuarioRoot = criteriaQuery.from(Usuario.class);
		
		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, usuarioRoot);

		criteriaQuery.select(builder.count(usuarioRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Long> query = manager.createQuery(criteriaQuery);

		return query.getSingleResult().intValue();
	}

	private List<Predicate> criarPredicatesParaFiltro(UsuarioFilter filtro, Root<Usuario> usuarioRoot) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtil.isNotEmpty(filtro.getNome())) {
			predicates.add(builder.like(usuarioRoot.get("nome"), "%" + filtro.getNome() + "%"));
		}
		if (StringUtil.isNotEmpty(filtro.getEmail())) {
			predicates.add(builder.equal(usuarioRoot.get("email"), filtro.getEmail()));
		}
		if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
			for (int i = 0; i < filtro.getGrupos().size(); i++) {
				predicates.add(builder.equal(usuarioRoot.join("grupos").get("codigo"), filtro.getGrupos().get(i)));
			}
		}

		return predicates;
	}

}
