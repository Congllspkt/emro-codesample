package smartsuite.app.bp.eform.seal.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import smartsuite.app.bp.eform.seal.repository.EFormSealRepository;
import smartsuite.app.common.shared.ResultMap;

/**
 * 간편서명 관련 처리하는 서비스 Class입니다.
 *
 * @FileName EFormSealService.java
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EFormSealService {

	@Inject
	EFormSealRepository eFormSealRepository;

	/**
	 * 인장 목록 조회
	 * @param param
	 * @return
	 */
	public List findListSeal(Map param) {
		return eFormSealRepository.findListSeal(param);
	}

	/**
	 * 인장 단건 조회
	 * @param param
	 * @return
	 */
	public Map findSeal(Map param) {
		return eFormSealRepository.findSeal(param);
	}

	/**
	 * 인장 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveSeal(Map param) {
		String imgSrc = this.replaceImgSrc((String) param.get("seal_img_enc_dat"));
		param.put("seal_img_enc_dat", imgSrc);
		
		eFormSealRepository.insertSeal(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 인장 수정
	 * @param param
	 * @return
	 */
	public ResultMap updateSeal(Map param) {
		String imgSrc = this.replaceImgSrc((String) param.get("seal_img_enc_dat"));
		param.put("seal_img_enc_dat", imgSrc);
		
		eFormSealRepository.updateSeal(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 인장 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteSeal(Map param) {
		eFormSealRepository.deleteSeal(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 이미지경로의 html 특수문자를 text로 변경
	 * @param fromStr
	 * @return
	 */
	private String replaceImgSrc(String fromStr) {
		if(Strings.isNullOrEmpty(fromStr)) {
			return "";
		}
		
		String toStr = fromStr.replace("&lt;", "<");
		toStr = toStr.replace("&gt;", ">");
		toStr = toStr.replace("&quot;", "\"");
		return toStr;
	}

	/**
	 * 회사 등록 인장 리스트
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findCompanySealList(Map param) {
		return eFormSealRepository.findCompanySealList(param);
	}

	/**
	 * 유저 등록 인장 리스트
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findUserSealList(Map param) {
		return eFormSealRepository.findUserSealList(param);
	}
	
}