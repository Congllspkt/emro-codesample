package smartsuite.app.util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.shared.CntrConst;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class PdfMakerJsoup {

	@Inject
	PdfMakerService pdfMakerService;

	@Value("#{eform['eform.audit.trail.template.path.file']}")
	String filePathFile;

	private static final Logger LOG = LoggerFactory.getLogger(PdfMakerJsoup.class);

	/**
	 * 감사추적 html template 가져와서 history 추적 및 서명값 가져와서 PDF 생성
	 * 
	 * @param 계약ID
	 * @return resultMap
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makePdfJsoup(Map<String, Object> param) {
		Map<String, Object> cntrInfo = (Map<String, Object>) param.get("cntrInfo"); // 계약Master정보
		Map<String, Object> fileInfo = (Map<String, Object>) param.get("fileInfo"); // 계약file정보
		List<Map<String, Object>> cntrHisList = (List<Map<String, Object>>) param.get("cntrHisList"); // 계약히스토리 list
		List<Map<String, Object>> signTargetList = (List<Map<String, Object>>) param.get("signTargetList"); // 계약서명정보 list
		
		Map<String, Object> pdfInfo = Maps.newHashMap();
		try {
			// HTML 파일 가져오기
			Document document = Jsoup.parse(new File(filePathFile), "UTF-8");
			// 계약Master정보 setting
			document = this.setMapDataToDocument(cntrInfo, document, null);
			// 계약file정보 setting
			document = this.setMapDataToDocument(fileInfo, document, null);

			// 계약 최초생성(임시저장), 완료(계약완료)일 때 데이터 setting
			for (Map<String, Object> cntrHis : cntrHisList) {
				if (CntrConst.CNTR_STATUS.CREATION.equals(cntrHis.get("cntr_sts_ccd"))) {
					document = this.setMapDataToDocument(cntrHis, document, "_wr");
				} else if (CntrConst.CNTR_STATUS.COMPLETE.equals(cntrHis.get("cntr_sts_ccd"))) {
					document = this.setMapDataToDocument(cntrHis, document, "_cp");
				}
			}

			// 서명 데이터 setting
			if (signTargetList.size() > 0) {
				Element signDiv = document.getElementById("sign_div"); // 서명정보 위치할 div

				int idx = 1;
				for (Map<String, Object> signTarget : signTargetList) {
					// 서명자 정보 tag
					Element divInfo1 = new Element(Tag.valueOf("div"), "");
					Element pInfo1 = new Element(Tag.valueOf("p"), "");
					Element tbInfo1 = new Element(Tag.valueOf("table"), "");
					Element trInfo1 = new Element(Tag.valueOf("tr"), "");
					Element trInfo2 = new Element(Tag.valueOf("tr"), "");
					Element trInfo3 = new Element(Tag.valueOf("tr"), "");
					divInfo1.addClass("box_bd");
					pInfo1.addClass("number");
					pInfo1.text(Integer.toString(idx + 1));
					tbInfo1.addClass("tb_gray");
					String tbColGpInfo = "<colgroup><col style='width: 50%;'><col style='width: 50%;'></colgroup>";
					String tbHdInfo = "<tr><th>외부자 승인 - WEB</th><th>외부처리</th></tr><tr>";

					// table HD
					tbInfo1.append(tbColGpInfo);
					tbInfo1.append(tbHdInfo);
					// table data
					String hisRgInfo = "<td id='his_reg_info'>" + signTarget.get("his_reg_info")
							+ "</td><td id='his_reg_dt'>" + signTarget.get("his_reg_dt") + "</td>";
					String hisAccInfo = "<td id='his_acc_info' colspan='2'>" + signTarget.get("his_acc_info") + "</td>";
					String signAhInfo = "<td id='sign_auth_typ' colspan='2'>" + signTarget.get("sign_auth_typ")
							+ "</td>";
					trInfo1.append(hisRgInfo);
					trInfo2.append(hisAccInfo);
					trInfo3.append(signAhInfo);

					tbInfo1.appendChild(trInfo1);
					tbInfo1.appendChild(trInfo2);
					tbInfo1.appendChild(trInfo3);

					divInfo1.appendChild(pInfo1);
					divInfo1.appendChild(tbInfo1);

					// 서명정보 넣기
					divInfo1.appendTo(signDiv);

					// 서명 Img tag
					Element divImg1 = new Element(Tag.valueOf("div"), "");
					Element divImg2 = new Element(Tag.valueOf("div"), "");
					Element divImg3 = new Element(Tag.valueOf("div"), "");
					Element pImg1 = new Element(Tag.valueOf("p"), "");
					Element pImg2 = new Element(Tag.valueOf("p"), "");
					divImg1.addClass("box_bd");
					divImg2.addClass("box_sign");
					divImg3.addClass("sign");
					pImg1.addClass("name");
					pImg2.addClass("sign");

					String imgTag = "<img src='" + signTarget.get("sgn_val") + "'>";
					pImg1.text("서명 " + Integer.toString(idx));
					pImg2.html(imgTag);
					divImg3.appendChild(pImg1);
					divImg3.appendChild(pImg2);
					divImg2.appendChild(divImg3);
					divImg1.appendChild(divImg2);

					// 서명img 넣기
					divImg1.appendTo(signDiv);

					idx = idx + 1;
				}
			}

			if (signTargetList.size() > 1) {
				// 최종완료 table 서명대상자 여러명일때 넘버링
				Element idxElement = document.getElementById("p_index_end");
				int idxEnd = signTargetList.size() + 2;
				idxElement.text(Integer.toString(idxEnd));
			}

			LOG.info("::::::::::::::::::::::::::Audit Trail PDF HTML::::::::::::::::::::::::::");
			LOG.info(document.toString());

			// HTML -> PDF File 저장
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("orgn_file_nm", cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + "-at.pdf");
			paramMap.put("cntrdoc_tmpl_cont", document.toString());
			paramMap.put("cntrdoc_tmpl_nm", cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + "-감사추적");
			pdfInfo = pdfMakerService.generateCntrFormPdf(paramMap, null);

		} catch (IOException ioe) {
			LOG.error(this.getClass().getName() + ".makePdfJsoup(Map param) : IOException", ioe);
			throw new CommonException(this.getClass().getName() + ".makePdfJsoup(Map param) : file paser filed. : " + ioe.toString());
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".makePdfJsoup(Map param) : Exception", e);
			throw new CommonException(this.getClass().getName() + ".makePdfJsoup(Map param) : file upload filed. : " + e.toString());
		}

		return pdfInfo;
	}

	private Document setMapDataToDocument(Map<String, Object> param, Document document, String keyId) {
		Iterator<String> keys = param.keySet().iterator();

		while (keys.hasNext()) {
			String paramKey = keys.next();
			String kId = !Strings.isNullOrEmpty(keyId) ? keyId : ""; // element ID 다를경우 사용
			String elementKey = paramKey + kId;
			String value = String.valueOf(param.get(paramKey));
			Element element = document.getElementById(elementKey);

			if (Strings.isNullOrEmpty(value)) { // 값 없으면 pass
				continue;
			} else if (element == null) {
				continue;
			}
			element.text(value);
		}

		return document;
	}

}