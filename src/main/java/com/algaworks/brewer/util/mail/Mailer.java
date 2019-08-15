package com.algaworks.brewer.util.mail;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.string.StringUtil;

public class Mailer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MailConfig mailConfig;
	
	@Inject
	private FotoStorage fotoStorage;
	
	public void enviar(Venda venda) throws IOException {
        VelocityEngine ve = new VelocityEngine();
		ve.setProperty("resource.loader", "class");
		ve.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init();

		VelocityContext context = new VelocityContext();
		Template template = ve.getTemplate("com/algaworks/brewer/mail/ResumoVenda.vm", "UTF-8");
		
		context.put("venda", venda);
		context.put("logo", "logo");
		context.put("numberTool", new NumberTool());
		context.put("locale", new Locale("pt", "BR"));

		Map<String, String> fotos = new HashMap<>();
		boolean adicionarMockCerveja = false;
		for (ItemVenda item : venda.getItens()) {
			Cerveja cerveja = item.getCerveja();
			if (StringUtil.isNotEmpty(cerveja.getFoto())) {
				String cid = "foto-" + cerveja.getCodigo();
				context.put(cid, cid);
				
				fotos.put(cid, cerveja.getFoto());
			} else {
				adicionarMockCerveja = true;
				context.put("cervejaMock", "cervejaMock");
			}
				
		}
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		
		try {
			HtmlEmail email = mailConfig.getConnection();
			email.setFrom("andrei.ricardo.gomes.marques@gmail.com");
			email.addTo(venda.getCliente().getEmail());
			email.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));
			email.setHtmlMsg(writer.toString());
			
			email.embed(new File(fotoStorage.pastaFoto() + getDefault().getSeparator() + "images" + getDefault().getSeparator() + "logo-gray.png"), "logo");
			
			if (adicionarMockCerveja) {
				email.embed(fotoStorage.recuperarThumbnailEmail("cerveja-mock.png"), "cervejaMock");
			}
			
			for (String cid : fotos.keySet()) {
				email.embed(fotoStorage.recuperarThumbnailEmail(fotos.get(cid)), cid);
			}
						
			email.send();
		} catch (EmailException e) {
			System.err.println(e.getMessage());
		}
	}
	
}
