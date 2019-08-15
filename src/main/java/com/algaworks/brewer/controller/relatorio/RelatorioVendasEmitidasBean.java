package com.algaworks.brewer.controller.relatorio;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.hibernate.Session;

import com.algaworks.brewer.util.jsf.FacesUtil;
import com.algaworks.brewer.util.report.ExecutorRelatorio;

@Named
@RequestScoped
public class RelatorioVendasEmitidasBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response;
	
	@Inject
	private EntityManager manager;
	
	@NotNull
	private Date dataInicio;
	
	@NotNull
	private Date dataFinal;
	
	public void emitir() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("data_inicio", dataInicio);
		parametros.put("data_fim", dataFinal);
		
		ExecutorRelatorio executor = new ExecutorRelatorio("/relatorios/relatorio_vendas_emitidas.jasper", 
				response, parametros, "VendasEmitidas.pdf");
		
		Session session = manager.unwrap(Session.class);
		session.doWork(executor);
		
		if (executor.isRelatorioGerado()) {
			facesContext.responseComplete();
		} else {
			FacesUtil.mensagemErro("A execução do relatório não retornou dados.");
		}
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
