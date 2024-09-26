package smartsuite.app.bp.contract.common;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 계약 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName ContractController.java
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/contract/**")
public class ContractController {
	
	@Inject
	ContractService contractService;
	
	
	/**
	 * 계약 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "largeFindListContract.do")
	public @ResponseBody FloaterStream largeFindListContract(@RequestBody Map param) {
		// 대용량 처리
		return contractService.largeFindListContract(param);
	}
	
	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findContract.do")
	public @ResponseBody ResultMap findContract(@RequestBody Map param) {
		return contractService.findContractDetail(param);
	}
	
	/**
	 * 변경 계약 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findChangeContract.do")
	public @ResponseBody ResultMap findChangeContract(@RequestBody Map param) {
		return contractService.findChangeContractDetail(param);
	}
	
	/**
	 * 계약 서명방법 공통코드 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListCommonCodeCntrSgnMeth.do")
	public @ResponseBody List findListCommonCodeCntrSgnMeth(@RequestBody Map param) {
		return contractService.findListCommonCodeCntrSgnMeth(param);
	}
	
	/**
	 * 계약 임시 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value="saveDraftContract.do")
	public @ResponseBody ResultMap saveDraftContract(@RequestBody Map param) {
		return contractService.saveDraftContract(param);
	}
	
	/**
	 * 계약서 생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value="createContractDocument.do")
	public @ResponseBody ResultMap createContractDocument(@RequestBody Map param) {
		return contractService.createContractDocument(param);
	}

	/**
	 * 일괄 계약 생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value="createContractDocumentList.do")
	public @ResponseBody ResultMap createContractDocumentList(@RequestBody Map param) {
		return contractService.createContractDocumentList(param);
	}

	/**
	 * 계약서 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value="deleteContractDocument.do")
	public @ResponseBody ResultMap deleteContractDocument(@RequestBody Map param) {
		return contractService.deleteContractDocument(param);
	}
	
	/**
	 * 협력사 부속서류 요청
	 * @param param
	 * @return
	 */
	@RequestMapping(value="requestAppxToVendor.do")
	public @ResponseBody ResultMap requestAppxToVendor(@RequestBody Map param) {
		return contractService.requestAppxToVendor(param);
	}
	
	/**
	 * 협력사 부속서류 거부
	 * @param param
	 * @return
	 */
	@RequestMapping(value="rejectAppxToVendor.do")
	public @ResponseBody ResultMap rejectAppxToVendor(@RequestBody Map param) {
		return contractService.rejectAppxToVendor(param);
	}
	
	/**
	 * 계약 발신
	 * @param param
	 * @return
	 */
	@RequestMapping(value="sendContract.do")
	public @ResponseBody ResultMap sendContract(@RequestBody Map param) {
		return contractService.sendContract(param);
	}
	
	/**
	 * 계약 이력 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListCntrHistory.do")
	public @ResponseBody List findListCntrHistory(@RequestBody Map param) {
		return contractService.findListCntrHistory(param);
	}
	
	/**
	 * 계약자 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListContractor.do")
	public @ResponseBody List findListContractor(@RequestBody Map param) {
		return contractService.findListContractor(param);
	}
	
	/**
	 * 계약 대상 협력사 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListCntrVendor.do")
	public @ResponseBody List findListCntrVendor(@RequestBody Map param) {
		return contractService.findListCntrVendor(param);
	}
	
	/**
	 * Docusign Template 생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "createDocusignTemplate.do")
	public @ResponseBody ResultMap createDocusignTemplate(@RequestBody Map param) {
		return contractService.createDocusignTemplate(param);
	}
	
	/**
	 * Docusign Template 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findDocusignTemplate.do")
	public @ResponseBody ResultMap findDocusignTemplate(@RequestBody Map param) {
		return contractService.findDocusignTemplate(param);
	}
	
	/**
	 * Docusign Template 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteDocusignTemplate.do")
	public @ResponseBody ResultMap deleteDocusignTemplate(@RequestBody Map param) {
		return contractService.deleteDocusignTemplate(param);
	}
	
	/**
	 * Docusign Envelope 생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "createDocusignEnvelope.do")
	public @ResponseBody ResultMap createDocusignEnvelope(@RequestBody Map param) {
		return contractService.createDocusignEnvelope(param);
	}
	
	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findDocusignEnvelope.do")
	public @ResponseBody ResultMap findDocusignEnvelope(@RequestBody Map param) {
		return contractService.findDocusignEnvelope(param);
	}
	
	/**
	 * Docusign Envelope 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteDocusignEnvelope.do")
	public @ResponseBody ResultMap deleteDocusignEnvelope(@RequestBody Map param) {
		return contractService.deleteDocusignEnvelope(param);
	}
	
	/**
	 * 간편 서명 Template 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteEFormTemplate.do")
	public @ResponseBody ResultMap deleteEFormTemplate(@RequestBody Map param) {
		return contractService.deleteEFormTemplate(param);
	}
	
	/**
	 * AdobeSign 계약서 생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "onCreateAdobeSign.do")
	public @ResponseBody ResultMap onCreateAdobeSign(@RequestBody Map param) {
		return contractService.onCreateAdobeSign(param);
	}

	/**
	 * AdobeSign 계약서 재생성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "onReCreateAdobeSign.do")
	public @ResponseBody ResultMap onReCreateAdobeSign(@RequestBody Map param) {
		return contractService.onReCreateAdobeSign(param);
	}

	/**
	 * AdobeSign 계약서 보기
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "getAdobeSignUrlInfo.do")
	public @ResponseBody ResultMap getAdobeSignUrlInfo(@RequestBody Map param) {
		return contractService.getAdobeSignUrlInfo(param);
	}

	/**
	 * AdobeSign 진행상태 체크
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "checkAdobeSignStatus.do")
	public @ResponseBody ResultMap checkAdobeSignStatus(@RequestBody Map param) {
		return contractService.checkAdobeSignStatus(param);
	}

	/**
	 * AdobeSign 계약서 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "onDeleteAdobeSign.do")
	public @ResponseBody ResultMap onDeleteAdobeSign(@RequestBody Map param) {
		return contractService.onDeleteAdobeSign(param);
	}

	/**
	 * AdobeSign 구매사 서명 url 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "getBpAdobeSignUrlInfo.do")
	public @ResponseBody ResultMap getBpAdobeSignUrlInfo(@RequestBody Map param) {
		return contractService.getBpAdobeSignUrlInfo(param);
	}

	/**
	 * 계약 해지
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "terminateContract.do")
	public @ResponseBody ResultMap terminateContract(@RequestBody Map param) {
		return contractService.terminateContract(param);
	}

	/**
	 * 계약서 회수
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "withdrawalContractDoc.do")
	public @ResponseBody ResultMap withdrawalContract(@RequestBody Map param) {
		return contractService.withdrawalContractDoc(param);
	}

	/**
	 * 일괄 계약 작성 > 업체 검증
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "validateCompanyList.do")
	public @ResponseBody ResultMap validateCompanyList(@RequestBody Map param) {
		return contractService.validateCompanyList(param);
	}

	/**
	 * 계약 일괄 다운로드
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "downloadAllCntr.do")
	public @ResponseBody void downloadAllCntr(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "cntr_uuids", required = false) String cntrUUIDs) {
		contractService.downloadAllCntr(request, response, cntrUUIDs);
	}

}