package smartsuite.app.bp.eform.eformsign.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.bouncycastle.util.encoders.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import smartsuite.upload.entity.FileItem;

@Service
public class PdfFlattenService {

	@Value("#{eform['signature.text.font.path']}")
	String fontPath;

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;

	private Color hex2Rgb(String colorStr) {
		return new Color(
				Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
				Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
				Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}

	private Double getDoubleValue(Object data) {
		if (data instanceof Double) {
			return (Double) data;
		} else if (data instanceof Long) {
			return ((Long) data).doubleValue();
		} else if (data instanceof Integer) {
			return ((Integer) data).doubleValue();
		} else if (data instanceof String) {
			return Double.parseDouble(data.toString());
		}
		return null;
	}

	private Integer getIntegerValue(Object data) {
		if (data instanceof Integer) {
			return (Integer) data;
		} else if (data instanceof Long) {
			return ((Long) data).intValue();
		}
		return null;
	}

	private PDFont getPDFont(PDDocument document, String fontName, boolean isBold) throws IOException {
		PDFont font = null;
		if ("MalgunGothic".equals(fontName) && isBold) {
			font = PDType0Font.load(document, new File(fontPath + "/MALGUNBD.ttf"));
		} else if ("MalgunGothic".equals(fontName) && !isBold) {
			font = PDType0Font.load(document, new File(fontPath + "/MALGUN.ttf"));
		} else if ("Batang".equals(fontName) && !isBold) {
			font = PDType0Font.load(document, new File(fontPath + "/batang.ttf"));
		} else if ("Batang".equals(fontName) && isBold) {
			font = PDType0Font.load(document, new File(fontPath + "/KoPubBatangBold.ttf"));
		} else {
			font = PDType1Font.HELVETICA;
		}
		return font;
	}

	private float getTextPosX(float pdfX1, String textAlign, float textFieldWidth, float textWidth) {
		float textPosX = pdfX1;
		if ("center".equals(textAlign)) {
			textPosX = pdfX1 + (textFieldWidth - textWidth) / 2;
		} else if ("right".equals(textAlign)) {
			textPosX = pdfX1 + (textFieldWidth - textWidth);
		}
		return textPosX;
	}

	private void showText(PDPageContentStream contentStream, PDFont font, float fontSize, Color fontColor, float textPosX, float textPosY, String text) throws IOException {
		contentStream.saveGraphicsState();
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.setNonStrokingColor(fontColor);
		contentStream.newLineAtOffset(textPosX, textPosY);
		contentStream.showText(text);
		contentStream.endText();
		contentStream.restoreGraphicsState();
	}

	private void writeTextFieldData(PDDocument document, PDPageContentStream contentStream, JSONObject jsonObject, boolean isMultiline) {
		String textValue = jsonObject.get("m_value").toString();
		if (textValue.isEmpty()) {
			return;
		}
		try {
			Double pdfX1 = getDoubleValue(jsonObject.get("m_pdfX"));
			Double pdfY1 = getDoubleValue(jsonObject.get("m_pdfY"));
			Double pdfX2 = getDoubleValue(jsonObject.get("m_pdfX2"));
			Double pdfY2 = getDoubleValue(jsonObject.get("m_pdfY2"));

			Double fontSize = getDoubleValue(jsonObject.get("m_fontSize"));
			Double textFieldWidth = Math.abs(pdfX2 - pdfX1);
			Double textFieldHeight = Math.abs(pdfY2 - pdfY1);
			String fontName = jsonObject.get("m_fontName").toString();
			String fontBold = jsonObject.get("m_bold").toString();

			PDFont font = this.getPDFont(document, fontName, "true".equals(fontBold) ? true : false);

			float textHeight = (font.getFontDescriptor().getCapHeight() / 1000.f) * fontSize.floatValue();
			// Setting the font to the Content stream

			String fontColorStr = jsonObject.get("m_fontColor").toString();
			Color fontColor = this.hex2Rgb(fontColorStr);

			if (isMultiline) {
				/* TO-BE Start */
				String finalTextValue = "";
				String [] tempTextValues = textValue.split("\\n");
				
				for (int i = 0; i < tempTextValues.length; ++i) {
					String oneLine = tempTextValues[i];
					
					int startPos = 0;
					int endPos = 1;
					
					if (oneLine.length() == 0) {
						finalTextValue += "\n";
					}
					
					while (endPos <= oneLine.length()) {
						String tmpTxt = oneLine.substring(startPos, endPos);
						float tmpTxtFontWidth = (font.getStringWidth(tmpTxt) / 1000.0f) * fontSize.floatValue(); 
						
						if (tmpTxtFontWidth >= textFieldWidth) {
							finalTextValue += oneLine.substring(startPos, endPos - 1) + "\n";
							startPos = endPos - 1;
						} else if (endPos == oneLine.length()) {
							finalTextValue += oneLine.substring(startPos, endPos) + "\n";
						} 
						
						endPos++;
					}
				} 
				
				String [] textValues = finalTextValue.split("\\n");
				float startPosY = pdfY1.floatValue() ;
				for (int i = 0; i < textValues.length; ++i) {
					String text = textValues[i];

					float textWidth = (font.getStringWidth(text) / 1000.0f) * fontSize.floatValue();
					float textPosX = this.getTextPosX(pdfX1.floatValue(), jsonObject.get("m_textAlign").toString(), textFieldWidth.floatValue(), textWidth);
					float lineHeight = 5 + ((fontSize.floatValue() * 0.4f) * i);
					float textPosY = startPosY - (textHeight * (i + 1)) - lineHeight; 
					this.showText(contentStream, font, fontSize.floatValue(), fontColor, textPosX, textPosY, text);
				}
			} else {
				float textWidth = (font.getStringWidth(textValue) / 1000.0f) * fontSize.floatValue();
				float textPosX = this.getTextPosX(pdfX1.floatValue(), jsonObject.get("m_textAlign").toString(), textFieldWidth.floatValue(), textWidth);
				float textPosY = pdfY2.floatValue() + (textFieldHeight.floatValue() - textHeight) / 2;
				this.showText(contentStream, font, fontSize.floatValue(), fontColor, textPosX, textPosY, textValue);
			}

			contentStream.saveGraphicsState();

//			contentStream.setStrokingColor(Color.BLACK);
//			contentStream.setLineWidth(1.f);
//			contentStream.addRect(pdfX1.floatValue(), pdfY2.floatValue(), textFieldWidth.floatValue(), textFieldHeight.floatValue());
//			contentStream.stroke();
//			contentStream.restoreGraphicsState();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeImageFieldData(PDDocument document, PDPageContentStream contentStream, JSONObject jsonObject) {
		String imageValue = jsonObject.get("m_value").toString();
		if (imageValue.isEmpty()) {
			return;
		}
		try {
			byte[] imageData = Base64.decode(imageValue.split(",")[1]);
			PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "signField");

			Double pdfX1 = this.getDoubleValue(jsonObject.get("m_pdfX"));
			Double pdfY1 = this.getDoubleValue(jsonObject.get("m_pdfY"));
			Double pdfX2 = this.getDoubleValue(jsonObject.get("m_pdfX2"));
			Double pdfY2 = this.getDoubleValue(jsonObject.get("m_pdfY2"));

			Double fieldWidth = Math.abs(pdfX2 - pdfX1);
			Double fieldHeight = Math.abs(pdfY2 - pdfY1);

			Double scaleX = fieldWidth / pdImage.getWidth();
			Double scaleY = fieldHeight / pdImage.getHeight();
			Double scale1 = scaleX > scaleY ? scaleY : scaleX;
			float imgWidth = pdImage.getWidth() * scale1.floatValue();
			float imgHeight = pdImage.getHeight() * scale1.floatValue();
			float posX = (pdfX1.floatValue() + (fieldWidth.floatValue() / 2) - (imgWidth / 2));
			float posY = (pdfY2.floatValue() + (fieldHeight.floatValue() / 2) - (imgHeight / 2));

			contentStream.drawImage(pdImage, posX, posY, imgWidth, imgHeight);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void writeSignFieldImageData(PDDocument document, PDPageContentStream contentStream, JSONObject jsonObject,
			Double pdfX1, Double pdfY1, Double pdfX2, Double pdfY2, Double fieldWidth, Double fieldHeight,
			Double orgSignWidth, Double orgSignHeight) {
		String imageValue = jsonObject.get("m_value").toString();
		try {
			Double scaleX = fieldWidth / orgSignWidth;
			Double scaleY = fieldHeight / orgSignHeight;

			Double scale1 = scaleX > scaleY ? scaleY : scaleX;
			byte[] imageData = Base64.decode(imageValue.split(",")[1]);
			PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "signField");

			float imgWidth = orgSignWidth.floatValue() * scale1.floatValue();
			float imgHeight = orgSignHeight.floatValue() * scale1.floatValue();
			float posX = (pdfX1.floatValue() + (fieldWidth.floatValue() / 2) - (imgWidth / 2));
			float posY = (pdfY2.floatValue() + (fieldHeight.floatValue() / 2) - (imgHeight / 2));

			contentStream.drawImage(pdImage, posX, posY, imgWidth, imgHeight);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void writeSignFieldPathData(PDPageContentStream contentStream, JSONObject jsonObject, Double ury,
			Double pdfX1, Double pdfY1, Double pdfX2, Double pdfY2, Double fieldWidth, Double fieldHeight,
			Double orgSignWidth, Double orgSignHeight) {
		JSONParser parser = new JSONParser();
		try {
			String signType = jsonObject.get("signType").toString();
			String signValue = jsonObject.get("m_value").toString();
			Double strokeWidth = this.getDoubleValue(jsonObject.get("m_signPenThickness"));
			Object obj = parser.parse(signValue);

			Double orgSignLeft = this.getDoubleValue(jsonObject.get("m_orgSignLeft"));
			Double orgSignTop = this.getDoubleValue(jsonObject.get("m_orgSignTop"));
			if (orgSignLeft == null) {
				orgSignLeft = 0.;
			}
			if (orgSignTop == null) {
				orgSignTop = 0.;
			}

			Double scaleX = fieldWidth / orgSignWidth;
			Double scaleY = fieldHeight / orgSignHeight;

			Double scale1 = scaleX > scaleY ? scaleY : scaleX;

			Double pathWidth = orgSignWidth * scale1;
			Double pathHeight = orgSignHeight * scale1;
			Double posX = pdfX1 + ((fieldWidth - pathWidth) / 2);
			Double posY = pdfY1 - ((fieldHeight - pathHeight) / 2);
			JSONObject jsonSignObject = (JSONObject) obj;
			JSONArray objectsList = (JSONArray) jsonSignObject.get("objects");

//			contentStream.saveGraphicsState();
//			contentStream.addRect(pdfX1.floatValue(), pdfY2.floatValue(), fieldWidth.floatValue(), fieldHeight.floatValue());
//			contentStream.restoreGraphicsState();
			contentStream.saveGraphicsState();
			Double offsetX = orgSignLeft * scale1;
			Double offsetY = orgSignTop * scale1;
			Double startXPos = posX - offsetX;
			Double startYPos = posY + offsetY;

			// 등록된서명 Group SVG 예외처리 추가
			if (objectsList.size() == 1) {
				JSONObject groupJObj = (JSONObject) objectsList.get(0);
				if ("group".equals(groupJObj.get("type"))) {
					objectsList = (JSONArray) groupJObj.get("objects");
				}
			}

			for (Object o : objectsList) {
				JSONObject jsonObject2 = (JSONObject) o;

				contentStream.setStrokingColor(Color.BLACK);
				contentStream.setLineWidth(strokeWidth.floatValue());

				JSONArray pathList = (JSONArray) jsonObject2.get("path");
				for (Object o2 : pathList) {
					JSONArray dataArr = (JSONArray) o2;
					if ("M".equals(dataArr.get(0))) {
						Double x1 = startXPos + this.getDoubleValue((dataArr.get(1))) * scale1;
						Double y1 = startYPos - this.getDoubleValue((dataArr.get(2))) * scale1;
						contentStream.moveTo(x1.floatValue(), y1.floatValue());
					} else if ("Q".equals(dataArr.get(0))) {
						Double x1 = startXPos + this.getDoubleValue((dataArr.get(1))) * scale1;
						Double y1 = startYPos - this.getDoubleValue((dataArr.get(2))) * scale1;
						Double x2 = startXPos + this.getDoubleValue((dataArr.get(3))) * scale1;
						Double y2 = startYPos - this.getDoubleValue((dataArr.get(4))) * scale1;
						contentStream.curveTo2(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue());
					} else if ("C".equals(dataArr.get(0))) {
						Double x1 = startXPos + this.getDoubleValue((dataArr.get(1))) * scale1;
						Double y1 = startYPos - this.getDoubleValue((dataArr.get(2))) * scale1;
						Double x2 = startXPos + this.getDoubleValue((dataArr.get(3))) * scale1;
						Double y2 = startYPos - this.getDoubleValue((dataArr.get(4))) * scale1;
						Double x3 = startXPos + this.getDoubleValue((dataArr.get(5))) * scale1;
						Double y3 = startYPos - this.getDoubleValue((dataArr.get(6))) * scale1;
						contentStream.curveTo(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue(),
								x3.floatValue(), y3.floatValue());
					} else if ("L".equals(dataArr.get(0))) {
						Double x1 = startXPos + this.getDoubleValue((dataArr.get(1))) * scale1;
						Double y1 = startYPos - this.getDoubleValue((dataArr.get(2))) * scale1;
						contentStream.lineTo(x1.floatValue(), y1.floatValue());
					}
				}
				if ("path".equals(signType)) {
					contentStream.stroke();
				} else {
					contentStream.fillAndStroke();
				}
			}
			contentStream.restoreGraphicsState();

		} catch (ParseException parseException) {
			parseException.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeSignFieldData(PDDocument document, PDPage page, PDPageContentStream contentStream, JSONObject jsonObject) {
		String signValue = jsonObject.get("m_value").toString();
		if (signValue.isEmpty()) {
			return;
		}

		try {
			Double ury = (double) page.getCropBox().getUpperRightY();

			String signType = jsonObject.get("signType").toString();

			Double pdfX1 = this.getDoubleValue(jsonObject.get("m_pdfX"));
			Double pdfY1 = this.getDoubleValue(jsonObject.get("m_pdfY"));
			Double pdfX2 = this.getDoubleValue(jsonObject.get("m_pdfX2"));
			Double pdfY2 = this.getDoubleValue(jsonObject.get("m_pdfY2"));

			Double fieldWidth = Math.abs(pdfX2 - pdfX1);
			Double fieldHeight = Math.abs(pdfY2 - pdfY1);
			Double orgSignWidth = this.getDoubleValue(jsonObject.get("m_orgSignWidth"));
			Double orgSignHeight = this.getDoubleValue(jsonObject.get("m_orgSignHeight"));

			if ("image".equals(signType)) {
				this.writeSignFieldImageData(document, contentStream, jsonObject, pdfX1, pdfY1, pdfX2, pdfY2,
						fieldWidth, fieldHeight, orgSignWidth, orgSignHeight);
			} else {
				this.writeSignFieldPathData(contentStream, jsonObject, ury, pdfX1, pdfY1, pdfX2, pdfY2, fieldWidth,
						fieldHeight, orgSignWidth, orgSignHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeCheckboxFieldPathData(PDPageContentStream contentStream, PDPage page, JSONObject jsonObject) {
		String checkValue = jsonObject.get("m_value").toString();
		if (!"true".equals(checkValue)) {
			return;
		}
		try {
			Double pdfX1 = this.getDoubleValue(jsonObject.get("m_pdfX"));
			Double pdfY1 = this.getDoubleValue(jsonObject.get("m_pdfY"));
			Double pdfX2 = this.getDoubleValue(jsonObject.get("m_pdfX2"));
			Double pdfY2 = this.getDoubleValue(jsonObject.get("m_pdfY2"));

			Double fieldWidth = Math.abs(pdfX2 - pdfX1);
			Double fieldHeight = Math.abs(pdfY2 - pdfY1);

			Double scaleX = fieldWidth / 48;
			Double scaleY = fieldHeight / 48;

			Double scale1 = scaleX > scaleY ? scaleY : scaleX;

			Double pathWidth = 48 * scale1;
			Double pathHeight = 48 * scale1;
			Double posX = pdfX1 + ((fieldWidth - pathWidth) / 2);
			Double posY = pdfY1 - ((fieldHeight - pathHeight) / 2);

//			contentStream.saveGraphicsState();
//			contentStream.addRect(pdfX1.floatValue(), pdfY2.floatValue(), fieldWidth.floatValue(), fieldHeight.floatValue());
//			contentStream.stroke();
//			contentStream.restoreGraphicsState();
//			contentStream.saveGraphicsState();

			Double startXPos = posX;
			Double startYPos = posY;
			if (posY.equals(0.)) {
				startYPos = startYPos - pathHeight;
			}

			contentStream.saveGraphicsState();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setLineWidth(1.f);
			// M17.2 32.2L4.9 19.9 0 24.8 17.2 42 48 10.9 43.1 6z
			contentStream.moveTo(startXPos.floatValue() + 17.2f * scale1.floatValue(),
					startYPos.floatValue() - 32.2f * scale1.floatValue());
			contentStream.lineTo(startXPos.floatValue() + 4.9f * scale1.floatValue(),
					startYPos.floatValue() - 19.9f * scale1.floatValue());
			contentStream.lineTo(startXPos.floatValue() + 0f * scale1.floatValue(),
					startYPos.floatValue() - 24.8f * scale1.floatValue());
			contentStream.lineTo(startXPos.floatValue() + 17.2f * scale1.floatValue(),
					startYPos.floatValue() - 42f * scale1.floatValue());
			contentStream.lineTo(startXPos.floatValue() + 48f * scale1.floatValue(),
					startYPos.floatValue() - 10.9f * scale1.floatValue());
			contentStream.lineTo(startXPos.floatValue() + 43.1f * scale1.floatValue(),
					startYPos.floatValue() - 6f * scale1.floatValue());
			contentStream.closeAndFillAndStroke();

			contentStream.restoreGraphicsState();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeRadioFieldPathData(PDPageContentStream contentStream, PDPage page, JSONObject jsonObject) {
		try {
			String radioValue = jsonObject.get("m_value").toString();
			String radioDValue = jsonObject.get("m_defaultValue").toString();

			Double pdfX1 = this.getDoubleValue(jsonObject.get("m_pdfX"));
			Double pdfY1 = this.getDoubleValue(jsonObject.get("m_pdfY"));
			Double pdfX2 = this.getDoubleValue(jsonObject.get("m_pdfX2"));
			Double pdfY2 = this.getDoubleValue(jsonObject.get("m_pdfY2"));

			Double fieldWidth = Math.abs(pdfX2 - pdfX1);
			Double fieldHeight = Math.abs(pdfY2 - pdfY1);

			Double scaleX = fieldWidth / 48;
			Double scaleY = fieldHeight / 48;

			Double scale1 = scaleX > scaleY ? scaleY : scaleX;

			Double pathWidth = 48 * scale1;
			Double pathHeight = 48 * scale1;
			Double posX = pdfX1 + ((fieldWidth - pathWidth) / 2);
			Double posY = pdfY1 - ((fieldHeight - pathHeight) / 2);

			// contentStream.saveGraphicsState();
			// contentStream.addRect(pdfX1.floatValue(), pdfY2.floatValue(),
			// fieldWidth.floatValue(), fieldHeight.floatValue());
			// contentStream.stroke();
			// contentStream.restoreGraphicsState();
			// contentStream.saveGraphicsState();

			Double startXPos = posX;
			Double startYPos = posY;
			if (posY.equals(0.)) {
				startYPos = startYPos - pathHeight;
			}

			contentStream.saveGraphicsState();
			contentStream.setStrokingColor(Color.BLACK);
			contentStream.setLineWidth(1.f);

			// on M 24 12 C 17.4 12 12 17.4 12 24 C 12 30.6 17.4 36 24 36 C 30.6 36 36 30.6
			// 36 24 C 36 17.4 30.6 12 24 12 Z\
			// M24,0C10.8,0,0,10.8,0,24s10.8,24,24,24s24-10.8,24-24S37.2,0,24,0z
			// M24,43.2C13.4,43.2,4.8,34.6,4.8,24S13.4,4.8,24,4.8S43.2,13.4,43.2,24S34.6,43.2,24,43.2z

			// off M 24 0 C 10.8 0 0 10.8 0 24 C 0 37.2 10.8 48 24 48 C 37.2 48 48 37.2 48
			// 24 C 48 10.8 37.2 0 24 0 Z
			// M 24 43.2 C 13.4 43.2 4.8 34.6 4.8 24 C 4.8 13.4 13.4 4.8 24 4.8 C 34.6 4.8
			// 43.2 13.4 43.2 24 C 43.2 34.6 34.6 43.2 24 43.2 z
			if (radioValue.equals(radioDValue)) {
				contentStream.moveTo(startXPos.floatValue() + 24f * scale1.floatValue(),
						startYPos.floatValue() - 12f * scale1.floatValue());
				contentStream.curveTo(startXPos.floatValue() + 17.4f * scale1.floatValue(),
						startYPos.floatValue() - 12f * scale1.floatValue(),
						startXPos.floatValue() + 12f * scale1.floatValue(),
						startYPos.floatValue() - 17.4f * scale1.floatValue(),
						startXPos.floatValue() + 12f * scale1.floatValue(),
						startYPos.floatValue() - 24f * scale1.floatValue());
				contentStream.curveTo(startXPos.floatValue() + 12f * scale1.floatValue(),
						startYPos.floatValue() - 30.6f * scale1.floatValue(),
						startXPos.floatValue() + 17.4f * scale1.floatValue(),
						startYPos.floatValue() - 36f * scale1.floatValue(),
						startXPos.floatValue() + 24f * scale1.floatValue(),
						startYPos.floatValue() - 36f * scale1.floatValue());
				contentStream.curveTo(startXPos.floatValue() + 30.6f * scale1.floatValue(),
						startYPos.floatValue() - 36f * scale1.floatValue(),
						startXPos.floatValue() + 36f * scale1.floatValue(),
						startYPos.floatValue() - 30.6f * scale1.floatValue(),
						startXPos.floatValue() + 36f * scale1.floatValue(),
						startYPos.floatValue() - 24f * scale1.floatValue());
				contentStream.curveTo(startXPos.floatValue() + 36f * scale1.floatValue(),
						startYPos.floatValue() - 17.4f * scale1.floatValue(),
						startXPos.floatValue() + 30.6f * scale1.floatValue(),
						startYPos.floatValue() - 12f * scale1.floatValue(),
						startXPos.floatValue() + 24f * scale1.floatValue(),
						startYPos.floatValue() - 12f * scale1.floatValue());
			}

			contentStream.moveTo(startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 0f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 10.8f * scale1.floatValue(),
					startYPos.floatValue() - 0f * scale1.floatValue(),
					startXPos.floatValue() + 0f * scale1.floatValue(),
					startYPos.floatValue() - 10.8f * scale1.floatValue(),
					startXPos.floatValue() + 0f * scale1.floatValue(),
					startYPos.floatValue() - 24f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 0f * scale1.floatValue(),
					startYPos.floatValue() - 37.2f * scale1.floatValue(),
					startXPos.floatValue() + 10.8f * scale1.floatValue(),
					startYPos.floatValue() - 48f * scale1.floatValue(),
					startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 48f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 37.2f * scale1.floatValue(),
					startYPos.floatValue() - 48f * scale1.floatValue(),
					startXPos.floatValue() + 48f * scale1.floatValue(),
					startYPos.floatValue() - 37.2f * scale1.floatValue(),
					startXPos.floatValue() + 48f * scale1.floatValue(),
					startYPos.floatValue() - 24f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 48f * scale1.floatValue(),
					startYPos.floatValue() - 10.8f * scale1.floatValue(),
					startXPos.floatValue() + 37.2f * scale1.floatValue(),
					startYPos.floatValue() - 0f * scale1.floatValue(),
					startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 0f * scale1.floatValue());

			contentStream.moveTo(startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 43.2f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 13.4f * scale1.floatValue(),
					startYPos.floatValue() - 43.2f * scale1.floatValue(),
					startXPos.floatValue() + 4.8f * scale1.floatValue(),
					startYPos.floatValue() - 34.6f * scale1.floatValue(),
					startXPos.floatValue() + 4.8f * scale1.floatValue(),
					startYPos.floatValue() - 24f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 4.8f * scale1.floatValue(),
					startYPos.floatValue() - 13.4f * scale1.floatValue(),
					startXPos.floatValue() + 13.4f * scale1.floatValue(),
					startYPos.floatValue() - 4.8f * scale1.floatValue(),
					startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 4.8f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 34.6f * scale1.floatValue(),
					startYPos.floatValue() - 4.8f * scale1.floatValue(),
					startXPos.floatValue() + 43.2f * scale1.floatValue(),
					startYPos.floatValue() - 13.4f * scale1.floatValue(),
					startXPos.floatValue() + 43.2f * scale1.floatValue(),
					startYPos.floatValue() - 24f * scale1.floatValue());
			contentStream.curveTo(startXPos.floatValue() + 43.2f * scale1.floatValue(),
					startYPos.floatValue() - 34.6f * scale1.floatValue(),
					startXPos.floatValue() + 34.6f * scale1.floatValue(),
					startYPos.floatValue() - 43.2f * scale1.floatValue(),
					startXPos.floatValue() + 24f * scale1.floatValue(),
					startYPos.floatValue() - 43.2f * scale1.floatValue());
			contentStream.fillAndStroke();
			contentStream.restoreGraphicsState();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeDataToPage(PDDocument document, int pageNo, JSONObject jsonObj) {
		JSONArray fieldsList = (JSONArray) jsonObj.get("fields");
		boolean multiLine = false;
		if (fieldsList.size() <= 0) {
			return;
		}

		try {
			PDPage pdPage = document.getPage(pageNo);
			PDPageContentStream contentStream = new PDPageContentStream(document, pdPage,
					PDPageContentStream.AppendMode.APPEND, true, true);

			for(Object fieldObj: fieldsList) {
				JSONObject jsonFieldObj = (JSONObject) fieldObj;
				Object isLineBreak = jsonFieldObj.get("m_lineBreak");
				multiLine = isLineBreak != null ? true : false; 

				String fieldType = jsonFieldObj.get("m_type").toString();
				if ("TextField".equals(fieldType) ||
					"DateField".equals(fieldType) ||
					"NumberField".equals(fieldType) ||
					"LabelField".equals(fieldType) ||
					"ComboboxField".equals(fieldType))  {
					writeTextFieldData(document, contentStream, jsonFieldObj, multiLine);
				} else if ("MultiLineTextField".equals(fieldType)) {
					multiLine = true;
					writeTextFieldData(document, contentStream, jsonFieldObj, multiLine);
				} else if ("SignField".equals(fieldType)) {
					writeSignFieldData(document, pdPage, contentStream, jsonFieldObj);
				} else if ("ImageField".equals(fieldType)) {
					writeImageFieldData(document, contentStream, jsonFieldObj);
				} else if ("CheckboxField".equals(fieldType)) {
					writeCheckboxFieldPathData(contentStream, pdPage, jsonFieldObj);
				} else if ("RadioField".equals(fieldType)) {
					writeRadioFieldPathData(contentStream, pdPage, jsonFieldObj);
				}
			}
			contentStream.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String writeDataToPdf(FileItem pdfFile, JSONObject jsonObject) {
		try {
			PDDocument document = PDDocument.load(pdfFile.toByteArray());
			JSONArray ownersList = (JSONArray) jsonObject.get("ownerList");
			Object ownerObj = ownersList.get(0);
			JSONObject jsonOwnerObj = (JSONObject) ownerObj;
			JSONArray pagesList = (JSONArray) jsonOwnerObj.get("pages");
			for (Object page : pagesList) {
				JSONObject jsonPageObj = (JSONObject) page;
				int pageNo = this.getIntegerValue(jsonPageObj.get("pageNo"));
				writeDataToPage(document, pageNo, jsonPageObj);
			}
			
			// pdf 파일 저장
			String fileFullPath = fileUploadPath + UUID.randomUUID().toString();
			File saveFile = new File(fileFullPath);
			document.save(saveFile);
			document.close(); // Closing the document
			
			return fileFullPath;
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}

	public String writeDataToPdf(FileItem pdfFile, String jsonData) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(jsonData);
			JSONObject jsonObject = (JSONObject) obj;
			return this.writeDataToPdf(pdfFile, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
