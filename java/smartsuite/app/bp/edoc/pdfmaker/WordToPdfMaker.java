package smartsuite.app.bp.edoc.pdfmaker;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.WordToPdfMakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfSignStrategy;

public class WordToPdfMaker extends PdfMaker{

	public WordToPdfMaker(WordToPdfMakingStrategy makingStrategy, PdfSignStrategy signStrategy) {
		super(makingStrategy, signStrategy);
	}
}
