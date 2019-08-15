package com.algaworks.brewer.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.util.cdi.CDIServiceLocator;

public class AppUserDetailService implements UserDetailsService, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Usuarios usuarios;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		usuarios = CDIServiceLocator.getBean(Usuarios.class);
		Usuario usuario = usuarios.porEmailEAtivo(email);
		
		UsuarioSistema user = null;
		
		if (usuario != null) {
			user = new UsuarioSistema(usuario, getPermissoes(usuario));
		} else {
			throw new UsernameNotFoundException("Usuário não encontrado.");
		}
		
		return user;
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		usuarios = CDIServiceLocator.getBean(Usuarios.class);
		for (String permissao : usuarios.permissoes(usuario)) {
			authorities.add(new SimpleGrantedAuthority(permissao));
		}
		
		return authorities;
	}

}
