package smartsuite.app.bp.pro.demo.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.printout.service.PrintoutService;
import smartsuite.app.bp.pro.demo.repository.PoDemoRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;

/**
 * PO 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName PoService.java
 * @package smartsuite.app.bp.pro.po
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoDemoService {

	@Inject
	PoDemoRepository poDemoRepository;

	@Inject
	PrintoutService printoutService;

	private static final String DOC_ID = "po_doc";
	private static final String PROJECT_NAME = "printout";
	private static final String FORM_NAME = "po_doc";

	public FloaterStream findListPoDemo(Map param) {
		return poDemoRepository.findListPoDemo(param);
	}

	public ResultMap saveInfoPoIfSts(Map param) {
		poDemoRepository.saveInfoPoIfSts(param);
		return ResultMap.SUCCESS();
	}

	public ResultMap findInfoIfPoDemo(Map param) {
		Map resultMap = Maps.newHashMap();
		Map result = poDemoRepository.findInfoIfPoDemo(param);
		resultMap.put("poIfInfo", result);

		Map sendInfo = Maps.newHashMap();
		Map poData = poDemoRepository.findInfoIfPoHeaderDemo(param);
		List poItem = poDemoRepository.findInfoIfPoItemDemo(param);
		sendInfo.put("poData", removeEmptyValues(poData));
		sendInfo.put("poItems", removeEmptyValuesFromListOfMaps(poItem));
		resultMap.put("sendInfo", sendInfo);

		Map rcptInfo = Maps.newHashMap();
		rcptInfo.put("RESPONSE_CODE", "FAIL");
		rcptInfo.put("RESPONSE_DATA", null);
		rcptInfo.put("RESPONSE_MESSAGE", poData.get("if_msg"));
		resultMap.put("rcptInfo", rcptInfo);

		return ResultMap.SUCCESS(resultMap);
	}

	private Map<String, Object> removeEmptyValues(Map<String, Object> param) {
		Map<String, Object> returnMap = Maps.newHashMap(param);
		Iterator<Map.Entry<String, Object>> iterator = returnMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			Object value = entry.getValue();
			if (value == null || (value instanceof String && ((String) value).isEmpty()) ||
					(value instanceof List && ((List<?>) value).isEmpty())) {
				iterator.remove();
			}
		}

		return returnMap;
	}


	private List<Map<String, Object>> removeEmptyValuesFromListOfMaps(List<Map<String, Object>> paramlist) {
		List<Map<String, Object>> resultList = Lists.newArrayList();

		for (Map<String, Object> param : paramlist) {
			Map<String, Object> returnMap = removeEmptyValues(param);
			resultList.add(returnMap);
		}

		return resultList;
	}

	public void updatePoIfError(Map<String, Object> param) {
		// Random 객체 생성
		Random random = new Random();

		// 0에서 9 사이의 랜덤 숫자 생성
		int randomNo = random.nextInt(10);

		Map updateMap = Maps.newHashMap();
		updateMap.put("if_msg", createErrorPoif());
		updateMap.put("random_no", randomNo);
		poDemoRepository.updatePoIfError(updateMap);
	}

	public void updateCntrIfError(Map<String, Object> param) {
		// Random 객체 생성
		Random random = new Random();

		// 0에서 9 사이의 랜덤 숫자 생성
		int randomNo = random.nextInt(10);

		Map updateMap = Maps.newHashMap();
		updateMap.put("if_msg", createErrorPoif());
		updateMap.put("random_no", randomNo);
		poDemoRepository.updateCntrIfError(updateMap);
	}


	private String createErrorPoif() {
		// 1 ~ 18 개의 메세지 중 랜덤으로 골라서 저장한다.
		List<String> errorList = Lists.newArrayList();
		errorList.add("Exception: com.sap.conn.jco.JCoException: (104) RFC_ERROR_SYSTEM_FAILURE: System failure in SAP system");
		errorList.add("Exception: com.sap.conn.jco.JCoException: (102) RFC_ERROR_COMMUNICATION: Communication failure in SAP system");
		errorList.add("Exception: java.net.SocketTimeoutException: Read timed out");
		errorList.add("Exception: javax.xml.ws.soap.SOAPFaultException: Client received SOAP Fault from server: <SOAP-ENV:Fault>...</SOAP-ENV:Fault>");
		errorList.add("Exception: AxisFault: HTTP (405) Method Not Allowed");
		errorList.add("Exception: AxisFault: Transport error: 404 Error: Not Found");
		errorList.add("\"error\": \"Bad Request\",\n" +
				"    \"message\": \"Invalid email format and password is too short\",\n" +
				"    \"status\": 400");
		errorList.add("\"error\": \"Unauthorized\",\n" +
				"    \"message\": \"Invalid or expired token\",\n" +
				"    \"status\": 401");
		errorList.add("\"error\": \"Forbidden\",\n" +
				"    \"message\": \"You do not have the necessary permissions to perform this action\",\n" +
				"    \"status\": 403");
		errorList.add("\"error\": \"Not Found\",\n" +
				"    \"message\": \"Product with ID 9999 not found\",\n" +
				"    \"status\": 404");
		errorList.add(" \"error\": \"Method Not Allowed\",\n" +
				"    \"message\": \"PUT method is not supported for this endpoint\",\n" +
				"    \"status\": 405");
		errorList.add(" \"error\": \"Conflict\",\n" +
				"    \"message\": \"A user with this email already exists\",\n" +
				"    \"status\": 409");
		errorList.add("\"error\": \"Unsupported Media Type\",\n" +
				"    \"message\": \"Content type 'text/plain' not supported\",\n" +
				"    \"status\": 415");
		errorList.add("\"error\": \"Unprocessable Entity\",\n" +
				"    \"message\": \"Quantity must be a positive number\",\n" +
				"    \"status\": 422");
		errorList.add("\"error\": \"Too Many Requests\",\n" +
				"    \"message\": \"You have exceeded the rate limit. Please try again after 60 seconds.\",\n" +
				"    \"status\": 429");
		errorList.add("\"error\": \"Internal Server Error\",\n" +
				"    \"message\": \"An unexpected error occurred on the server\",\n" +
				"    \"status\": 500");
		errorList.add("\"error\": \"Bad Gateway\",\n" +
				"    \"message\": \"The server was acting as a gateway or proxy and received an invalid response from the upstream server\",\n" +
				"    \"status\": 502");
		errorList.add("\"error\": \"Service Unavailable\",\n" +
				"    \"message\": \"The server is currently unable to handle the request due to temporary overloading or maintenance\",\n" +
				"    \"status\": 503");

		Random random = new Random();

		// 1에서 18 사이의 랜덤 숫자 생성 (1을 더해서 1부터 시작하도록 함)
		int randomNumber = random.nextInt(18) + 1;

		String errorMessage = errorList.get(randomNumber);
		return errorMessage;
	}

	/**
	 * 문서 출력을 위한 po정보(단건) 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findInfoDocumentOutputPo(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> docOutpDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> docOutpDataByPoDetail = Lists.newArrayList();

		Map<String, Object> callDocOutpDataInfo = printoutService.findDocumentOutputManagement(parameter);            // 문서 출력 정보 조회
		Map<String, Object> paramGroupList = callDocOutpDataInfo.get("paramGroupList") == null ? new HashMap<String, Object>() : (Map<String, Object>) callDocOutpDataInfo.get("paramGroupList");

		Map<String, Object> poHeader = poDemoRepository.findInfoDocumentOutputPoHeader(param);
		List<Map<String, Object>> poDetail = poDemoRepository.findListDocumentOutputPoDetail(param);

		for(String documentGroupName : paramGroupList.keySet()) {
			if(("poHeader").equals(documentGroupName)) {
				docOutpDataByPoHeader.add(poHeader);
			} else if(("poDetail").equals(documentGroupName)) {
				docOutpDataByPoDetail.addAll(poDetail);
			}
		}

		// Converting JSON
		Gson gson = new Gson();
		String docOutpDataInfoByPoHeader = gson.toJson(docOutpDataByPoHeader);
		String docOutpDataInfoByPoDetail = gson.toJson(docOutpDataByPoDetail);

		Map<String, Object> docOutpInfo = Maps.newHashMap();
		docOutpInfo.put("poHeader", docOutpDataInfoByPoHeader);
		docOutpInfo.put("poDetail", docOutpDataInfoByPoDetail);

		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);            // 유비폼 호출 파라미터 셋팅
		documentOutputDataInfo.put("datasetList", docOutpInfo);
		documentOutputDataInfo.put("param", responseInfo.get("param"));

		resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	/**
	 * 문서 출력을 위한 po정보(복수건) 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findListDocumentOutputPo(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> docOutpDataInfo = Maps.newHashMap();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		List<Map<String, Object>> docOutpDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> docOutpDataByPoDetail = Lists.newArrayList();

		List<String> poUuidList = (List<String>) param.get("po_uuid");
		for(String poUuid : poUuidList) {
			Map<String, Object> poUuidInfo = Maps.newHashMap();
			poUuidInfo.put("po_uuid", poUuid);

			Map<String, Object> callDocOutpDataInfo = printoutService.findDocumentOutputManagement(parameter);            // 문서 출력 정보 조회
			Map<String, Object> paramGroupList = callDocOutpDataInfo.get("paramGroupList") == null ? new HashMap<String, Object>() : (Map<String, Object>) callDocOutpDataInfo.get("paramGroupList");

			Map<String, Object> poHeader = poDemoRepository.findInfoDocumentOutputPoHeader(poUuidInfo);
			List<Map<String, Object>> poDetail = poDemoRepository.findListDocumentOutputPoDetail(poUuidInfo);

			for(String documentGroupName : paramGroupList.keySet()) {
				if(("poHeader").equals(documentGroupName)) {
					docOutpDataByPoHeader.add(poHeader);
				} else if(("poDetail").equals(documentGroupName)) {
					docOutpDataByPoDetail.addAll(poDetail);
				}
			}
		}

		// Converting JSON
		Gson gson = new Gson();
		String docOutpDataInfoByPoHeader = gson.toJson(docOutpDataByPoHeader);
		String docOutpDataInfoByPoDetail = gson.toJson(docOutpDataByPoDetail);

		Map<String, Object> docOutpInfo = Maps.newHashMap();
		docOutpInfo.put("poHeader", docOutpDataInfoByPoHeader);
		docOutpInfo.put("poDetail", docOutpDataInfoByPoDetail);

		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);            // 유비폼 호출 파라미터 셋팅
		docOutpDataInfo.put("datasetList", docOutpInfo);
		docOutpDataInfo.put("param", responseInfo.get("param"));

		resultMap.setResultData(docOutpDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	/**
	 * 문서 출력 정보 조회를 위한 파라미터 셋팅
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> documentOutputParameterSet(Map<String, Object> param) {
		param.put("doc_id", DOC_ID);
		param.put("projectName", PROJECT_NAME);
		param.put("formName", FORM_NAME);
		return param;
	}
}
