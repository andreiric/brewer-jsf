package com.algaworks.brewer.storage;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

public class FotoStorage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String THUMBNAIL_PREFIX = "thumbnail.";
	public static final String URL              = "cerveja_fotos/";

	private static final Logger logger = LoggerFactory.getLogger(FotoStorage.class);

	private Path local;

	public FotoStorage() {
		this.local = getDefault().getPath(pastaFoto(), "cerveja_fotos");
		criarPastas();
	}

	public String salvar(UploadedFile file) {
		String novoNome = null;
		if (file != null) {
			novoNome                 = renomearArquivo(file.getFileName());
			String arquivo           = this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome;
			try {
				FileOutputStream fos = new FileOutputStream(
						new File(arquivo));
				fos.write(file.getContents());
				fos.close();
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto na pasta", e);
			}

			try {
				Thumbnails.of(arquivo).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			} catch (IOException e) {
				throw new RuntimeException("Erro gerando thumbnail", e);
			}
		}

		return novoNome;
	}

	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			logger.warn(String.format("Erro apagando foto '%s'. Mensagem '%s'.", foto, e.getMessage()));
		}
	}

	public String getUrl(String foto) {
		return URL + foto;
	}
	
	public String getUrlThumbnail(String foto) {
		return URL + THUMBNAIL_PREFIX + foto;
	}
	
	public StreamedContent recuperar(String foto, String contentType) throws IOException {
		return new DefaultStreamedContent(
				FileUtils.openInputStream(
						new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + foto)), contentType); 
	}
	
	public File recuperarThumbnailEmail(String foto) {
		return new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + THUMBNAIL_PREFIX + foto);
	}
	
	public String pastaFoto() {
		FacesContext facesContext     = FacesContext.getCurrentInstance();
		ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
		return servletContext.getRealPath("/")+"resources";
	}

	private String renomearArquivo(String nomeOriginal) {
		return  UUID.randomUUID().toString() + "_" + nomeOriginal;
	}

	private void criarPastas() {
		try {
			Files.createDirectories(this.local);

			if (logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}
	
}
