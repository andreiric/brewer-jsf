package com.algaworks.brewer.repository.crud;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

public abstract class CrudRepository<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Class<T> classe;
	
	@Inject
	private EntityManager manager;
	
	public CrudRepository(Class<T> classe) {
		this.classe = classe;
	}
	
	public List<T> todos() {
		CriteriaQuery<T> query = manager.getCriteriaBuilder().createQuery(classe);
		query.from(classe);
		return manager.createQuery(query).getResultList();
	}
	
	public T porCodigo(Long codigo) {
		return manager.find(classe, codigo);
	}
	
	public T salvar(T entidade) {
		return manager.merge(entidade);
	}
	
	public void excluir(T entidade) {
		manager.remove(entidade);
		manager.flush();
	}

}
