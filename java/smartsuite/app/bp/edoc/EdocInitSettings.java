package smartsuite.app.bp.edoc;

import com.aspose.pdf.FontRepository;
import com.aspose.words.FolderFontSource;
import com.aspose.words.FontSettings;
import com.aspose.words.FontSourceBase;
import com.aspose.words.PhysicalFontInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/***
 * 전자계약  초기세팅관련 클래스
 */
public class EdocInitSettings {
	
	private final static Logger LOG = LoggerFactory.getLogger(EdocInitSettings.class);

	@Value("#{edoc['font.path']}")
	private String fontList;

	@PostConstruct
	private void setup(){ // NOPMD
		List<String> fontPathList  = Arrays.asList(fontList.split(","));
		applyAsposePdfLicense(); // Aspose.PDF 라이센스 적용
		applyAsposeWordLicense();// Aspose.WORD 라이센스 적용
		loadFont(fontPathList);  // 폰트폴더 적용
		//applyKicaLicense();	//한국정보인증 툴킷인 경우 라이센스 적용
	}
	
	/**
	 * Aspose.PDF 라이센스 적용
	 */
	private void applyAsposePdfLicense(){
		final String LICENSE = "<License><Data><LicensedTo>EMRO INC.</LicensedTo><EmailTo>phatchul@emro.co.kr</EmailTo><LicenseType>Developer OEM</LicenseType><LicenseNote>Limited to 1 developer, unlimited physical locations</LicenseNote><OrderID>190211022642</OrderID><UserID>135009502</UserID><OEM>This is a redistributable license</OEM><Products><Product>Aspose.Pdf for Java</Product></Products><EditionType>Enterprise</EditionType><SerialNumber>b1a5e9c5-13e1-4958-add3-176e9e5809a1</SerialNumber><SubscriptionExpiry>20200211</SubscriptionExpiry><LicenseVersion>3.0</LicenseVersion><LicenseInstructions>https://purchase.aspose.com/policies/use-license</LicenseInstructions></Data><Signature>A3CTAMFNKAXlnhYsiIMhp68Lmmn2wPH311b0Tf8TAZbgBrSgHltZOwSUsW1T8yKV4zLldor9/I4i4fWDW/D6BP39TshIwsx3Wqv3o8CLz0NSv86DoPCaMz2rEGVllBl8N9XMasovFt5+Kl0B/n0gy30OXoHv6iIEsOcxGjIdxj0=</Signature></License>";
		
		try{
			if(!com.aspose.pdf.Document.isLicensed()){
				InputStream is = new ByteArrayInputStream(LICENSE.getBytes());
				com.aspose.pdf.License license = new com.aspose.pdf.License();
				license.setLicense(is);
				LOG.info("####################################");
				LOG.info("## Aspose.PDF's license is applied## ");
				LOG.info("####################################");
			}
		}catch(Exception e){
			LOG.error("#######################################");
			LOG.error("###Aspose.PDF license's Exception.###");
			LOG.error("#########" + e.getMessage() + "#########");
			LOG.error("#######################################");
		}
	}
	
	/**
	 * Aspose.WORD 라이센스 적용
	 */ 
	private void applyAsposeWordLicense(){
		//final String LICENSE = "<License><Data><LicensedTo>EMRO INC.</LicensedTo><EmailTo>ksh901016@emro.co.kr</EmailTo><LicenseType>Developer OEM</LicenseType><LicenseNote>Limited to 1 developer, unlimited physical locations</LicenseNote><OrderID>190409024120</OrderID><UserID>135014053</UserID><OEM>This is a redistributable license</OEM><Products><Product>Aspose.Words for Java</Product></Products><EditionType>Enterprise</EditionType><SerialNumber>7cb86116-e542-48fd-8be6-66ba452f1df7</SerialNumber><SubscriptionExpiry>20200409</SubscriptionExpiry><LicenseVersion>3.0</LicenseVersion><LicenseInstructions>https://purchase.aspose.com/policies/use-license</LicenseInstructions></Data><Signature>OcGC8JLrkXQ8F2pIuzKDjOXR/t2xYmBuyl95it2NPGcg63XWe8qCZKk7t+iVXajvXiPDAypUYy3O7Tq4zXLA6RvARDyXbLttBbfrFVdQHtbTXfiQPUa6GUk+AAGImQer5ZS1ph4//w66jYDcNvvFSMeKtvff49KGTmmRKCvdpgE=</Signature></License>";
		final String LICENSE = "<License><Data><LicensedTo>daesunglee </LicensedTo><EmailTo>daesung86.lee@emro.co.kr</EmailTo><LicenseType>Developer OEM</LicenseType><LicenseNote>1 Developer And Unlimited Deployment Locations</LicenseNote><OrderID>220811034758</OrderID><UserID>933973</UserID><OEM>This is a redistributable license</OEM><Products><Product>Aspose.Words for Java</Product></Products><EditionType>Professional</EditionType><SerialNumber>c5004439-efd6-430a-b11b-921b4e420af3</SerialNumber><SubscriptionExpiry>20230811</SubscriptionExpiry><LicenseVersion>3.0</LicenseVersion><LicenseInstructions>https://purchase.aspose.com/policies/use-license</LicenseInstructions></Data><Signature>ba9YWLms0A0tSgaNoZcfYzrfpu3w9k/DrSnveJdzr8XC2DHot6E1tyIbQuivH4J+aSFt0N61qRriHd020ZgRYKC54glGmeiWEJnnmZwI0vj/OLtyaUDeH6Mo0hrHJc4TXxxb06HgR3EFyjga23aSJayzgSVe+48ImjODP3BWhIjzlCkkGF9ndM5omn0fdK7H2jr3zui3geAmc9saIqmWkA/pCWBCFwbCTfuouE3d0oqfLvSYk9y/FbNy9xqMDZhRq/zNdw8YiIdmxmdq0l5K86qMVBT8xQVa35los0FsKHEALvr2V6uq2ScRx6wMV+AqQP0hKNTSHGMr1fxl32pIzA==</Signature></License>";
		try{
			com.aspose.words.License license = new com.aspose.words.License();
			InputStream is = new ByteArrayInputStream(LICENSE.getBytes());
			license.setLicense(is);
			LOG.info("####################################");
			LOG.info("## Aspose.WORD's license is applied## ");
			LOG.info("####################################");
		}catch(Exception e){
			LOG.error("#######################################");
			LOG.error("###Aspose.WORD's license's Exception.###");
			LOG.error("#########" + e.getMessage() + "#########");
			LOG.error("#######################################");
		}
	}

	/**
	 * 특정폴더에 있는 Font를 load
	 */
	private void loadFont(List<String> fontPathList){
		for(String fontPath : fontPathList){
			if(!new File(fontPath).exists()){
				LOG.info("##################################");
				LOG.info("########font path is wrong.#######");
				LOG.info("###font setting does not apply.###");
				LOG.info("###path : {}###", fontPath);
				LOG.info("##################################");
				continue;
			}
			
			// Aspose.PDF
			FontRepository.setThreadStaticConfigEnabled(false); // global한 FontRepository 설정
			FontRepository.addLocalFontPath(fontPath);
			
			// Aspose.WORD
			ArrayList<FolderFontSource> fontSources = new ArrayList(Arrays.asList(FontSettings.getDefaultInstance().getFontsSources()));
			FolderFontSource folderFontSource = new FolderFontSource(fontPath, true);
			fontSources.add(folderFontSource);
			FontSourceBase[] updatedFontSources = fontSources.toArray(new FontSourceBase[fontSources.size()]);
			FontSettings.getDefaultInstance().setFontsSources(updatedFontSources);
			FontSettings.getDefaultInstance().getSubstitutionSettings().getDefaultFontSubstitution().setDefaultFontName("gulim");
			FontSettings.getDefaultInstance().getSubstitutionSettings().getFontInfoSubstitution().setEnabled(false);
			
			LOG.info("##################################");
			LOG.info("###font setting complete.###");
			LOG.info("###path : {}###", fontPath);
			LOG.info("##################################");
		}
	}
	
	/**
	 * 적용된 폰트리스트 출력 (필요시 사용)
	 */
	public static void printFontList(){
		// Aspose.WORD
		FontSourceBase[] sources = FontSettings.getDefaultInstance().getFontsSources();
		for(FontSourceBase base : sources){
			List<PhysicalFontInfo> fontList = base.getAvailableFonts();
			Iterator<PhysicalFontInfo> iter = fontList.iterator();
			while(iter.hasNext()){
				PhysicalFontInfo font = iter.next();
				LOG.info("fontFamilyName : {}", font.getFontFamilyName());
				LOG.info("fullFontName : {}", font.getFullFontName());
				LOG.info("Aspose.Word Font : {}", font.getFilePath());
			}
		}
		
		// Aspose.PDF
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts();
		for(Font font : fonts){
			try{
				FontRepository.findFont(font.getName(), true);
				LOG.info("I can find the {}", font.getName());
			}catch(Exception e1){
				LOG.error("I can't find the {}", font.getName());
			}
		}
	}
	
	/**
	 * 라이센스 적용 여부 반환 (필요시 사용)
	 */
	public static boolean getAppliedLicenseYn(){
		if(com.aspose.pdf.Document.isLicensed()){
			return true;
		}
		
		return false;
	}

}
