package com.algaworks.brewer.dto;

public class VendaOrigem {

	public static final String SQL = "select date_format(v.data_criacao, '%Y/%m') as mes				"+
				 					 ", group_concat(distinct (select coalesce(sum(i.quantidade), 0) 	"+
									 "				     from item_venda i								"+
									 "				        , cerveja c									"+
									 "				        , venda v1									"+
									 "				     where i.codigo_venda = v1.codigo 				"+
									 "				       and i.codigo_cerveja = c.codigo				"+
									 "				       and c.origem = 'NACIONAL'					"+
									 "				       and date_format(v1.data_criacao, '%Y/%m') = date_format(v.data_criacao, '%Y/%m')  "+
									 "				   )) as total_nacional		 						"+
									 ", group_concat(distinct (select coalesce(sum(i.quantidade), 0)	"+
									 "				     from item_venda i								"+
									 "				        , cerveja c									"+
									 "				        , venda v1									"+
									 "				     where i.codigo_venda = v1.codigo 				"+
									 "				       and i.codigo_cerveja = c.codigo				"+
									 "				       and c.origem = 'INTERNACIONAL'				"+
									 "				       and date_format(v1.data_criacao, '%Y/%m') = date_format(v.data_criacao, '%Y/%m')  "+
									 "				   )) as total_internacional						"+
									 "from venda v										 				"+
									 "where v.data_criacao > DATE_SUB(NOW(), INTERVAL 6 MONTH) 			"+
									 "		AND now()          > v.data_criacao							"+
									 "		AND v.status         = 'EMITIDA'							"+
									 "group by date_format(v.data_criacao, '%Y/%m')  					"+
									 "order by date_format(v.data_criacao, '%Y/%m') desc				";

	private String mes;
	private Integer totalNacional;
	private Integer totalInternacional;

	public VendaOrigem(String mes, Integer totalNacional, Integer totalInternacional) {
		super();
		this.mes = mes;
		this.totalNacional = totalNacional;
		this.totalInternacional = totalInternacional;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public Integer getTotalNacional() {
		return totalNacional;
	}

	public void setTotalNacional(Integer totalNacional) {
		this.totalNacional = totalNacional;
	}

	public Integer getTotalInternacional() {
		return totalInternacional;
	}

	public void setTotalInternacional(Integer totalInternacional) {
		this.totalInternacional = totalInternacional;
	}

}
