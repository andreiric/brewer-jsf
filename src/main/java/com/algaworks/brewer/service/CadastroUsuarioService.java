package com.algaworks.brewer.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.util.jpa.Transactional;
import com.algaworks.brewer.util.string.StringUtil;

public class CadastroUsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Usuarios usuarios;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public void salvar(Usuario usuario) throws NegocioException {
		Usuario usuarioPresente = usuarios.porEmail(usuario.getEmail());
		
		if (usuario.isNovo() && usuarioPresente != null) {
			throw new NegocioException("E-mail já cadastrado");
		}
		if (usuario.getGrupos() == null) {
			throw new NegocioException("Selecione pelo menos um grupo");
		}
		if (usuario.isNovo() && !StringUtil.isNotEmpty(usuario.getSenha())) {
			throw new NegocioException("Senha obrigatória para novo usuário");
		}
		if (usuario.isNovo() || StringUtil.isNotEmpty(usuario.getSenha())) {
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
		} else if (!StringUtil.isNotEmpty(usuario.getSenha())) {
			usuario.setSenha(usuarioPresente.getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());
		
		if (!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioPresente.getAtivo());
		}
		
		usuarios.salvar(usuario);
	}

	@Transactional
	public void excluir(Usuario usuario) throws NegocioException {
		try {
			usuario = usuarios.porCodigo(usuario.getCodigo());
			usuarios.excluir(usuario);
		} catch (PersistenceException e) {
			throw new NegocioException("Impossível apagar usuário. Já foi utilizado em alguma venda");
		}
	}
	
}
