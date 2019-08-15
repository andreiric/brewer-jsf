package com.algaworks.brewer.controller.dashboard;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.algaworks.brewer.dto.ValorItensEstoque;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Vendas;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Vendas vendas;
	
	@Inject
	private Clientes clientes;
	
	@Inject 
	private Cervejas cervejas;
	
	private BigDecimal vendasNoAno;
	private BigDecimal vendasNoMes;
	private BigDecimal ticketMedio;
	private ValorItensEstoque valorItensEstoque;
	private int totalClientes;
	
	private LineChartModel graficoVendasPorMes;
	private BarChartModel graficoVendasPorOrigem;
	private DateAxis dateAxis;
	
	public void inicializar() {
		vendasNoAno = vendas.valorTotalNoAno();
		vendasNoMes = vendas.valorTotalNoMes();
		ticketMedio = vendas.ticketMedio();
		valorItensEstoque = cervejas.valorItensEstoque();
		totalClientes = clientes.todos().size();
		
		inicializarGraficos();
	}

	private void inicializarGraficos() {
		graficoVendasPorMes = inicializarGraficoVendasPorMes();
		graficoVendasPorMes.setLegendPosition("n");
		dateAxis = new DateAxis();
		dateAxis.setTickFormat("%Y/%m");
		graficoVendasPorMes.getAxes().put(AxisType.X, dateAxis);
		
		graficoVendasPorOrigem = inicializarGraficoVendasPorOrigem();
		graficoVendasPorOrigem.setLegendPosition("n");
		dateAxis = new DateAxis();
		dateAxis.setTickFormat("%Y/%m");
		graficoVendasPorOrigem.getAxes().put(AxisType.X, dateAxis);
	}
	
	private LineChartModel inicializarGraficoVendasPorMes() {
		LineChartModel model = new LineChartModel();
		
		LineChartSeries series = new LineChartSeries();
		series.setLabel("Vendas por mês");
		
		for (VendaMes vendaMes : vendas.totalPorMes()) {
			series.set(vendaMes.getMes(), vendaMes.getTotal());
		}
		
		model.addSeries(series);
		return model;
	}
	
	private BarChartModel inicializarGraficoVendasPorOrigem() {
		BarChartModel model = new BarChartModel();
		
		BarChartSeries seriesInternacional = new BarChartSeries();
		seriesInternacional.setLabel("Internacional");
		
		BarChartSeries seriesNacional = new BarChartSeries();
		seriesNacional.setLabel("Nacional");
		
		for (VendaOrigem vendaOrigem : vendas.totalPorOrigem()) {
			seriesInternacional.set(vendaOrigem.getMes(), vendaOrigem.getTotalInternacional());
			seriesNacional.set(vendaOrigem.getMes(), vendaOrigem.getTotalNacional());
		}
		
		model.addSeries(seriesInternacional);
		model.addSeries(seriesNacional);
		return model;
	}
	
	public BigDecimal getVendasNoAno() {
		return vendasNoAno;
	}

	public BigDecimal getVendasNoMes() {
		return vendasNoMes;
	}

	public BigDecimal getTicketMedio() {
		return ticketMedio;
	}

	public ValorItensEstoque getValorItensEstoque() {
		return valorItensEstoque;
	}

	public int getTotalClientes() {
		return totalClientes;
	}

	public LineChartModel getGraficoVendasPorMes() {
		return graficoVendasPorMes;
	}

	public BarChartModel getGraficoVendasPorOrigem() {
		return graficoVendasPorOrigem;
	}
	
}
