package com.algaworks.brewer.controller.usuario;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaUsuariosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Grupos grupos;
	
	@Inject
	private Usuarios usuarios;
	
	@Inject
	private CadastroUsuarioService cadastroUsuarioService;
	
	private UsuarioFilter usuarioFilter;
	private List<Grupo> todosGrupos;
	private LazyDataModel<Usuario> model;
	private Usuario usuarioSelecionado;
	
	public PesquisaUsuariosBean() {
		if (usuarioFilter == null) {
			usuarioFilter = new UsuarioFilter();
		}
		
		model = new LazyDataModel<Usuario>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Usuario> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				usuarioFilter.setPrimeiroRegistro(first);
				usuarioFilter.setQuantidadeRegistro(pageSize);
				usuarioFilter.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				usuarioFilter.setPropriedadeOrdenacao(sortField);
				
				setRowCount(usuarios.total(usuarioFilter));
				
				return usuarios.filtrar(usuarioFilter);
			}
		};
	}
	
	public void inicializar() {
		todosGrupos = grupos.todos();
	}
	
	public void excluir() {
		try {
			cadastroUsuarioService.excluir(usuarioSelecionado);
			
			FacesUtil.mensagemInfo("Usuário excluído com sucesso");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}
	
	public UsuarioFilter getUsuarioFilter() {
		return usuarioFilter;
	}

	public void setUsuarioFilter(UsuarioFilter usuarioFilter) {
		this.usuarioFilter = usuarioFilter;
	}

	public LazyDataModel<Usuario> getModel() {
		return model;
	}

	public List<Grupo> getTodosGrupos() {
		return todosGrupos;
	}

	public void setTodosGrupos(List<Grupo> todosGrupos) {
		this.todosGrupos = todosGrupos;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

}
