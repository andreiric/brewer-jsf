package com.algaworks.brewer.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.jpa.Transactional;
import com.algaworks.brewer.util.string.StringUtil;

public class CadastroCervejaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Cervejas cervejas;
	
	@Inject
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja) throws NegocioException {
		Cerveja cervejaPresente = cervejas.porSku(cerveja.getSku());
		
		if (cerveja.isNova() && cervejaPresente != null && cervejaPresente.getCodigo() != null) {
			throw new NegocioException("SKU já cadastrado em outra cerveja");
		}
		
		cervejas.salvar(cerveja);
	}

	@Transactional
	public void excluir(Cerveja cerveja) throws NegocioException {
		try {
			cerveja = cervejas.porCodigo(cerveja.getCodigo());
			String foto = cerveja.getFoto();
			cervejas.excluir(cerveja);
			if (StringUtil.isNotEmpty(foto)) {
				fotoStorage.excluir(foto);
			}
		} catch (PersistenceException e) {
			throw new NegocioException("Impossível apagar cerveja. Já foi utilizada em alguma venda");
		}
	}
	
}
