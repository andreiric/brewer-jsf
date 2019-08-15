package com.algaworks.brewer.repository;

import java.io.Serializable;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.repository.crud.CrudRepository;

public class Grupos extends CrudRepository<Grupo> implements Serializable {

	private static final long serialVersionUID = 1L;

	public Grupos() {
		super(Grupo.class);
	}
	
}
