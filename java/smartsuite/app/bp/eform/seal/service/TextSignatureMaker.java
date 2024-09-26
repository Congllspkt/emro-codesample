package smartsuite.app.bp.eform.seal.service;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import smartsuite.app.common.shared.Const;

@Service
public class TextSignatureMaker {
	private final static Logger LOG = LoggerFactory.getLogger(TextSignatureMaker.class);
	
	@Value("#{eform['signature.text.font.path']}")
	String fontPath;
	@Value("#{eform['signature.text.font.ttfList']}")
	String fontList;
	
	List<String> fontFileList;

	@PostConstruct
	private void setup() { // NOPMD
		this.fontFileList = Arrays.asList(fontList.split(","));
	}
	
	public Map<String, Object> makeTextSignature(String text, String fontIndex) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int fontIdx = Integer.parseInt(fontIndex);
		if (this.fontFileList.size() <= fontIdx) {
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, "Font List Out Of Index: " + fontIndex);
			return resultMap;
		}
		
		File fontFile = new File(this.fontPath + "/" + this.fontFileList.get(fontIdx) + ".ttf");
		if (!fontFile.exists()) {
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, "Font Not Suuport: " + fontIndex);
			return resultMap;
		}
		
		try {
			final BufferedImage textImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = textImage.createGraphics();
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			font = font.deriveFont(50.f);
			FontRenderContext frc = g.getFontMetrics(font).getFontRenderContext();
			GlyphVector gv = font.createGlyphVector(frc, text);
			Shape shape = gv.getOutline();
			StringBuilder result = new StringBuilder();
			float[] coords = new float[6];
			PathIterator pi = shape.getPathIterator(null);
			while (!pi.isDone()) {
				int seg = pi.currentSegment(coords);
				//SEG_CLOSE;
				switch (seg) {
					case PathIterator.SEG_MOVETO: {
						result.append("M");
						result.append(" ").append(coords[0]);
						result.append(" ").append(coords[1]);
						result.append(" ");
						break;
					}
					case PathIterator.SEG_LINETO: {
						result.append("L");
						result.append(" ").append(coords[0]);
						result.append(" ").append(coords[1]);
						result.append(" ");
						break;
					}
					case PathIterator.SEG_QUADTO: {
						result.append("Q");
						result.append(" ").append(coords[0]);
						result.append(" ").append(coords[1]);
						result.append(" ").append(coords[2]);
						result.append(" ").append( coords[3]);
						result.append(" ");
						break;
					}
					case PathIterator.SEG_CUBICTO: {
						result.append("C");
						result.append(" ").append(coords[0]);
						result.append(" ").append(coords[1]);
						result.append(" ").append(coords[2]);
						result.append(" ").append( coords[3]);
						result.append(" ").append(coords[4]);
						result.append(" ").append(coords[5]);
						result.append(" ");
						break;
					}
					case PathIterator.SEG_CLOSE: {
						result.append("Z ");
					}
				}
				pi.next();
			}
			String re = result.toString();
			g.dispose();
			resultMap.put("signature_data", re);
			resultMap.put("font_name", this.fontFileList.get(fontIdx));
			
		} catch (FontFormatException e) {
			e.printStackTrace();
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, "Font Exception: " + e.getLocalizedMessage());
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, "IOException: " + e.getLocalizedMessage());
		}
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		
		return resultMap;
	}
	
}