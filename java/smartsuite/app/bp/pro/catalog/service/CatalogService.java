package smartsuite.app.bp.pro.catalog.service;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.catalog.repository.CatalogRepository;
import smartsuite.app.bp.pro.pr.service.PrService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileGroup;
import smartsuite.upload.entity.FileItem;
import smartsuite5.attachment.core.util.MimeTypeUtil;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class CatalogService {


	@Inject
	private CatalogRepository catalogRepository;
	@Inject
	StdFileService stdFileService;
	@Inject
	MimeTypeUtil mimeTypeUtil;

    public List<Map<String, Object>> findListUprcItemWithCatalog(Map param) throws RuntimeException {
		List<Map<String, Object>> ctlg_list = catalogRepository.findListUprcItemWithCatalog(param);

		if(!ctlg_list.isEmpty() && !ctlg_list.get(0).containsKey("cnt")){
			for(Map ctlg : ctlg_list){
				if(ctlg.get("thnl_athg_uuid") != null && !"".equals(ctlg.get("thnl_athg_uuid"))) {
					FileGroup result = null;
					try {
						result = stdFileService.findFileGroup((String) ctlg.get("thnl_athg_uuid"));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					ctlg.put("thnl_img_addr", result.getFileList().getItems().get(0).getId());
				}
			}
		}
		return ctlg_list;
    }

	public List<Map<String, Object>> findListUprcItemWithoutCatalog(Map param) {
		return catalogRepository.findListUprcItemWithoutCatalog(param);
	}

	public Map<String, Object> getImage(String id) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		FileItem fileItem = stdFileService.findFileItemWithContents(id);
		String mimeType = mimeTypeUtil.mimeTypeForFileExtension(fileItem.getExtension());

		if (mimeType.contains("image")) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtils.copy(fileItem.toInputStream(), outputStream);

			// outputStream에는 이미지가 들어있어 이를 통하여 이미지 라이브러리로 이미지 조작이 가능합니다.

			// 본 예제는 리턴값으로 data 키에 base64인코딩된 이미지만 넣습니다.
			resultMap.put("data", "data:" + mimeType + ";base64," + Base64.encodeBase64String(outputStream.toByteArray()));
		} else {
			resultMap.put("request_error", "request file is not image");
		}
		return resultMap;
	}
}
