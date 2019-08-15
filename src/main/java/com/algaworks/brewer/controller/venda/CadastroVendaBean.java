package com.algaworks.brewer.controller.venda;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.security.Seguranca;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.service.NegocioException;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.jsf.FacesUtil;
import com.algaworks.brewer.util.mail.Mailer;

@Named
@ViewScoped
public class CadastroVendaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Cervejas cervejas;

	@Inject
	private FotoStorage fotoStorage;

	@Inject
	private CadastroVendaService cadastroVendaService;
	
	@Inject
	private Seguranca seguranca;
	
	@Inject
	private Mailer mailer;
	
	@Inject
	private Vendas vendas;
	
	private TabelaItensVenda tabelaItensVenda;
	private Venda venda;
	private Cerveja cervejaSelecionada;

	public CadastroVendaBean() {
		limpar();
	}

	private void limpar() {
		venda = new Venda();
		tabelaItensVenda = new TabelaItensVenda();
	}

	public void inicializar() {
		if (venda == null) {
			limpar();
		}
		if (!venda.isNova()) {
			venda = vendas.buscarComItens(venda.getCodigo());
			
			for (ItemVenda item : venda.getItens()) {
				tabelaItensVenda.adicionarItem(item.getCerveja(), item.getQuantidade());
			}
		}
	}

	public void selecionouCliente(SelectEvent event) {
		if (event.getObject() != null) {
			venda.setCliente(((Cliente) event.getObject()));
		}
	}

	public void adicionarItem(SelectEvent event) {
		if (event.getObject() != null) {
			tabelaItensVenda.adicionarItem((Cerveja) event.getObject(), 1);
			cervejaSelecionada = new Cerveja();
		}
	}

	public void excluirItem(Cerveja cerveja) {
		tabelaItensVenda.excluitItem(cerveja);
	}

	public void salvar() {
		try {
			venda.setUsuario(seguranca.getUsuarioSistema().getUsuario());
			venda.adicionarItens(tabelaItensVenda.getItens());
			venda.calcularValorTotal();
			cadastroVendaService.salvar(venda);
			limpar();
			
			FacesUtil.mensagemInfo("Venda salva com sucesso");
			FacesUtil.redirecionar("/venda/CadastroVenda");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}
	
	public void emitir() {
		try {
			venda.setUsuario(seguranca.getUsuarioSistema().getUsuario());
			venda.adicionarItens(tabelaItensVenda.getItens());
			venda.calcularValorTotal();
			cadastroVendaService.emitir(venda);
			limpar();
			
			FacesUtil.mensagemInfo("Venda emitida com sucesso");
			FacesUtil.redirecionar("/venda/CadastroVenda");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		}
	}

	public void enviarEmail() {
		try {
			venda.setUsuario(seguranca.getUsuarioSistema().getUsuario());
			venda.adicionarItens(tabelaItensVenda.getItens());
			venda.calcularValorTotal();
			venda = cadastroVendaService.salvar(venda);
			mailer.enviar(venda);
			
			FacesUtil.mensagemInfo(String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
			limpar();
			FacesUtil.redirecionar("/venda/CadastroVenda");
		} catch (NegocioException e) {
			FacesUtil.mensagemErro(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelar() {
		try {
			cadastroVendaService.cancelar(venda);
			
			FacesUtil.mensagemInfo("Venca cancelada com sucesso");
			FacesUtil.redirecionar("/venda/CadastroVenda", "codigo=" + venda.getCodigo());
		} catch (AccessDeniedException e) {
			FacesUtil.redirecionar("/403", "");
		}
	}
	
	public String getUrlThumbnailFoto(String foto) {
		return fotoStorage.getUrlThumbnail(foto);
	}

	public List<Cerveja> pesquisarCervejas(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public TabelaItensVenda getTabelaItensVenda() {
		return tabelaItensVenda;
	}

	public Cerveja getCervejaSelecionada() {
		return cervejaSelecionada;
	}

	public void setCervejaSelecionada(Cerveja cervejaSelecionada) {
		this.cervejaSelecionada = cervejaSelecionada;
	}
	
	public BigDecimal getValorTotalVenda() {
		BigDecimal valorTotalItens = tabelaItensVenda.getValorTotal();
		return valorTotalItens.add(Optional.ofNullable(venda.getValorFrete()).orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(venda.getValorDesconto()).orElse(BigDecimal.ZERO));
	}

}
