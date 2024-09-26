package smartsuite.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

@Service
public class ExcelUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);

	@Inject
	StdFileService fileService;
	@Value ("#{eform['eform.excel.download.maxhdno']}")
	int MAX_HD_INDEX;

	/**
	 * 특정 엑셀 파일을 List에 Object[]로 컨버트 한다.
	 * 
	 * @param grpCd 엑셀파일 파일그룹코드
	 * @param fixedColunm 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultMap getExcelToList(String grpCd, Map<String, Object> fixedColunm) {
		Map<String, Object> resultData = Maps.newHashMap();
		List<Map<String, Object>> fixedColList = Lists.newArrayList();
		
		// Excel File
		FileList fileList = null;
		FileItem fileItem = null;
		InputStream inputStream = null;
		try {
			fileList = fileService.findFileListWithContents(grpCd);
			fileItem = fileList.getItems().get(0);
			inputStream = fileItem.toInputStream();
		} catch(Exception e) {
			LOG.error("### ERROR FILE ### ", e);
			return ResultMap.FAIL();
		}

		// 엑셀 로드
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(inputStream);
		} catch(IOException ie) {
			LOG.error("### ERROR EXCEL IO ### ", ie);
			return ResultMap.FAIL();
		}

		// Required Fixed Colunms
		int fixedColLen = 0; // Fixed Colunm length
		if(fixedColunm != null && ((List<Map<String, Object>>) fixedColunm.get("fixedColList")).size() > 0) {
			fixedColList = ((List<Map<String, Object>>) fixedColunm.get("fixedColList"));
			fixedColLen = fixedColList.size();
		}

		// 시트 로드 0, 첫번째 시트 로드
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowItr = sheet.iterator();
		List<Map<String, Object>> headerList = Lists.newArrayList(); // Excel Header List
		List<Map<String, Object>> dataList = Lists.newArrayList(); // Excel Data List

		// row만큼 반복
		int nullCnt = 0;
		int hdNullCnt = 0;
		int rowIndex = 0; // Excel row index
		while (rowItr.hasNext()) {
			Map<String, Object> rowMap = Maps.newHashMap();
			int cellIndex = 0; // Excel cell index
			boolean isNull = false; // null check

			// row의 cell씩 읽기
			Row row = rowItr.next();
			Iterator<Cell> cellItr = row.cellIterator();
			while (cellItr.hasNext()) {
				Cell cell = cellItr.next(); // Excel cell 1개씩 읽어오기
				rowIndex = cell.getRowIndex(); // Cell colunm index
				cellIndex = cell.getColumnIndex(); // Cell colunm index

				// 안내 메시지 첫번째줄 PASS
				if(rowIndex == 0) {
					continue;
				}

				// 헤더컬럼 최대 30개 까지
				if(cellIndex >= MAX_HD_INDEX) {
					continue;
				}

				// Header는 NULL/Valid 맞지 않으면 return, Data는 전체 Row 제외
				String cellValue = getValueFromCell(cell) != null ? getValueFromCell(cell).toString() : "";

				// Header Cell NULL/Valid check
				if(headerList.size() == 0) {
					// Header 차례에 필수값이 비어있을 때
					if(Strings.isNullOrEmpty(cellValue)) {
						if(cellIndex < fixedColLen) { // Required Header
							try {
								throw new InvalidFormatException("getExcelToList() : 필수입력 항목 헤더명이 없습니다.");
							} catch(InvalidFormatException e) {
								LOG.error("### ERROR EXCEL HEADER NAME INVALID FORMAT ### ", e);
								return ResultMap.builder()
										.resultStatus(ResultMap.STATUS.FAIL)
										.resultMessage("STD.EDO2078") //필수입력 항목 헤더명이 없습니다.
										.build();
							}
						} else {
							// Hd null count 10 이면 프로세스 끝내기
							if(hdNullCnt == 10) {
								try {
									throw new InvalidFormatException("getExcelToList() : 항목 헤더명이 없습니다.");
								} catch (InvalidFormatException e) {
									LOG.error("### ERROR EXCEL HEADER NAME INVALID FORMAT ### ", e);
									return ResultMap.builder()
											.resultStatus(ResultMap.STATUS.FAIL)
											.resultMessage("STD.EDO2079") //항목 헤더명이 없습니다.
											.build();
								}
							}
							hdNullCnt++;
						}
					} else {
						// Header 차례에 고정컬럼의 headerText값과 다를 때
						if((cellIndex < fixedColLen) && !cellValue.equals(fixedColList.get(cellIndex).get("headerText"))) {
							try {
								throw new InvalidFormatException("getExcelToList() : 필수입력 항목 헤더명이 올바르지 않습니다.");
							} catch (InvalidFormatException e) {
								LOG.error("### ERROR EXCEL HEADER NAME INVALID FORMAT ### ", e);
								return ResultMap.builder()
										.resultStatus(ResultMap.STATUS.FAIL)
										.resultMessage("STD.EDO2080") //필수입력 항목 헤더명이 올바르지 않습니다.
										.build();
							}
						}
					}
				} else if(headerList.size() > 0 && (cellIndex < fixedColLen)) { // Data Cell NULL check
					// Data 차례에 필수값이 비어있을 때
					if(Strings.isNullOrEmpty(cellValue)) {
						isNull = true;
						try {
							throw new InvalidFormatException("getExcelToList() : 필수입력 항목값이 없습니다.");
						} catch (InvalidFormatException e) {
							LOG.error("### ERROR EXCEL HEADER VALUE INVALID FORMAT ### ", e);
							// null count 30 이면 프로세스 끝내기
							if(nullCnt == 30) {
								return ResultMap.builder()
										.resultStatus(ResultMap.STATUS.FAIL)
										.resultMessage("STD.EDO2081") //필수입력 항목 값이 없습니다.
										.build();
							}
						}
						nullCnt++;
					}
				}
				
				if(headerList.size() == 0) { // header
					rowMap.put(Integer.toString(cellIndex), getValueFromCell(cell));
				} else { // data
					if(cellIndex < fixedColLen) {
						rowMap.put(fixedColList.get(cellIndex).get("dataField").toString(), getValueFromCell(cell));
					} else {
						rowMap.put("header_" + Integer.toString(cellIndex), getValueFromCell(cell));
					}
				}
			}

			if (headerList.size() == 0 && !rowMap.isEmpty()) { // headerList
				headerList.add(rowMap);
				resultData.put("cellLength", cellIndex); // Excel Header
			} else {
				// 필수값 없으면 Data row 한 줄 전체를 제외
				if(!rowMap.isEmpty() && !isNull) { // dataList
					dataList.add(rowMap);
				}
			}
		}
		
		resultData.put("headerList", headerList); 	// Excel Header
		resultData.put("dataList", dataList);		// Excel Data
		resultData.put("maxHdIndex", MAX_HD_INDEX);	// Excel Data
		return ResultMap.builder()
				.resultStatus(ResultMap.STATUS.SUCCESS)
				.resultData(resultData)
				.build();
	}

	private static Object getValueFromCell(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return cell.getNumericCellValue();
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return "";
		}
	}
}