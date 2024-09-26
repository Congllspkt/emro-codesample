package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.app.bp.edoc.pdfmaker.HtmlToPdfMaker;
import smartsuite.app.bp.edoc.pdfmaker.WordToPdfMaker;
import smartsuite.app.bp.edoc.pdfmaker.factory.ImageTypeFactoryBean;
import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.HtmlToPdfMakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.WordToPdfMakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfSignStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfUtil;
import smartsuite.app.util.EdocInitSettings;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class EdocConfiguration {

	@Bean("edoc")
	public Properties edocProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/edoc.properties");
	}

	@Bean("edocInitSettings")
	public EdocInitSettings edocInitSettings() {
		return new EdocInitSettings();
	}

	@Bean("imageTypeProvider")
	public ImageTypeFactoryBean imageTypeFactoryBean() {
		return new ImageTypeFactoryBean();
	}

	@Bean("pdfUtil")
	public PdfUtil pdfUtil() {
		return new PdfUtil();
	}

	@Bean("wordToPdfMakingStrategy")
	public WordToPdfMakingStrategy wordToPdfMakingStrategy() {
		return new WordToPdfMakingStrategy();
	}

	@Bean("pdfSignStrategy")
	public PdfSignStrategy pdfSignStrategy() {
		return new PdfSignStrategy();
	}

	@Bean("wordToPdfMaker")
	public WordToPdfMaker wordToPdfMaker(WordToPdfMakingStrategy makingStrategy, PdfSignStrategy signStrategy) {
		return new WordToPdfMaker(makingStrategy, signStrategy);
	}

	@Bean("htmlToPdfMakingStrategy")
	public HtmlToPdfMakingStrategy htmlToPdfMakingStrategy() {
		return new HtmlToPdfMakingStrategy();
	}

	@Bean("htmlToPdfMaker")
	public HtmlToPdfMaker htmlToPdfMaker(HtmlToPdfMakingStrategy makingStrategy, PdfSignStrategy signStrategy) {
		return new HtmlToPdfMaker(makingStrategy, signStrategy);
	}
}
