package com.algaworks.brewer.controller.usuario;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroUsuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Grupos grupos;
	
	@Inject
	private CadastroUsuarioService cadastroUsuarioService;

	private List<Grupo> todosGrupos;
	private Grupo[] gruposSelecionados;

	private Usuario usuario;

	public CadastroUsuarioBean() {
		limpar();
	}
	
	private void limpar() {
		usuario = new Usuario();
		gruposSelecionados = null;
	}

	public void inicializar() {
		if (usuario == null) {
			limpar();
		}
		if (!usuario.isNovo()) {
			gruposSelecionados = (Grupo[]) usuario.getGrupos().toArray(new Grupo[usuario.getGrupos().size()]);
		}
		
		todosGrupos = grupos.todos();
	}
	
	public void salvar() {
		try {
			if (gruposSelecionados.length > 0) {
				usuario.setGrupos(Arrays.asList(this.gruposSelecionados));
			}
			cadastroUsuarioService.salvar(usuario);
			limpar();
			
			FacesUtil.mensagemInfo("Usuário salvo com sucesso");
			FacesUtil.redirecionar("/usuario/CadastroUsuario");
		} catch (Exception e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Grupo> getTodosGrupos() {
		return todosGrupos;
	}

	public Grupo[] getGruposSelecionados() {
		return gruposSelecionados;
	}

	public void setGruposSelecionados(Grupo[] gruposSelecionados) {
		this.gruposSelecionados = gruposSelecionados;
	}
	
}
