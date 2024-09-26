package smartsuite.app.bp.rfx.rfx;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.excel.ExcelReaderUtil;
import smartsuite.app.common.mail.MailReceiveHandler;
import smartsuite.app.common.mail.MailWorkExcelHandler;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class RFXMailReceiveHandler implements MailReceiveHandler {

//	@Inject
//	private SqlSession sqlSession;
	
	/** Mapper Namespace */
//	private static final String NAMESPACE = "mail.";
	
	@Inject
	StdFileService stdFileService;

	@Inject
	ExcelReaderUtil excelReaderUtil;

	@Autowired(required = false)
	private MailWorkExcelHandler mailWorkExcelHandler;




	private void handleSheets(Map<String, Object> dataResultMap) throws Exception{
		if(mailWorkExcelHandler != null){
			mailWorkExcelHandler.excelParsingAfterAppUploadService(dataResultMap);
		}
	}

	/**
	 * 메일에 첨부된 EXCEL이 발송 전 맞는지 확인하는 로직
	 * @param mailInfo
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void handleMail(Map<String, Object> mailInfo) {
		String emailWorkTargId = mailInfo.get("eml_task_subj_uuid").toString();
		if(StringUtils.isEmpty(emailWorkTargId)) {
			throw new CommonException("eml_task_subj_uuid 가 존재하지 않습니다.");
		}


		if(mailInfo.get("athg_uuid") == null){
			throw new CommonException("첨부된 파일이 없는 이메일 입니다!");
		}
		
		FileList fileList;
		try {
			fileList = stdFileService.findFileListWithContents(mailInfo.get("athg_uuid").toString());
		} catch(Exception e) {
			throw new CommonException("첨부된 파일이 없는 이메일 입니다!");
		}

		FileItem excelFileItem = null;
		for(FileItem fileItem : fileList.getItems()){
			if("xlsx".equals(fileItem.getExtension())){
				excelFileItem = fileItem;
				break;
			}else if(fileItem.getName().indexOf("xlsx") > -1){
				excelFileItem = fileItem;
				break;
			}

		}

		if(excelFileItem == null){
			throw new CommonException("엑셀파일이 없습니다");
		}

		/*try {
			excelFileItem = stdFileService.findFileItemWithContents(excelFileItem.getId());
		} catch (Exception e1) {
			throw new CommonException("파일을 가져오는 중 오류발생!", e1);
		}*/
 


		//파일을 읽어서.. email_snd_log_id / email_work_targ_id 를 끌고와서..
		Map<String, Object> resultDataMap = new HashMap<String, Object>();

		try {
			resultDataMap = excelReaderUtil.excelReadAndMappingSheetBean(excelFileItem, mailInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultDataMap.put("email_work_targ_id",emailWorkTargId);

		try{
			//엑셀에서 취합한 데이터를 가져와, 업무단으로 넘겨준다.
			handleSheets(resultDataMap);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
