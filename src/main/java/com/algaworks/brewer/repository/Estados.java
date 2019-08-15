package com.algaworks.brewer.repository;

import java.io.Serializable;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.crud.CrudRepository;

public class Estados extends CrudRepository<Estado> implements Serializable {

	private static final long serialVersionUID = 1L;

	public Estados() {
		super(Estado.class);
	}
	
}
