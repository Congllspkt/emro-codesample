package smartsuite.config.workplace.vo;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Lists;

/**
 * The Class WorkplaceSetting.
 */
public class WorkTaskMethodSetting implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8345783427760505762L;
	
	/* tenant id */
	String tenId;

	/* workplace data create method UUID */
	String wkplcMethodId;

	/* task method class name */
	String taskClassNm;
	
	/* task method name */
	String taskMethodNm;

	/* workplace class name */
	String wkplcClassNm;

	/* workplace method name */
	String wkplcMethodNm;

	/*  */
	String techCls = "POST";
	
	/* task method list */
	//List<TaskMethod> taskMethods = Lists.newArrayList();
	
	public String getTenId() {
		return tenId;
	}

	public void setTenId(String tenId) {
		this.tenId = tenId;
	}

	public String getWkplcMethodId() {
		return wkplcMethodId;
	}

	public void setWkplcMethodId(String wkplcMethodId) {
		this.wkplcMethodId = wkplcMethodId;
	}

	public String getTaskClassNm() {
		return taskClassNm;
	}

	public void setTaskClassNm(String taskClassNm) {
		this.taskClassNm = taskClassNm;
	}

	public String getTaskMethodNm() {
		return taskMethodNm;
	}

	public void setTaskMethodNm(String taskMethodNm) {
		this.taskMethodNm = taskMethodNm;
	}

	public String getWkplClassNm() {
		return wkplcClassNm;
	}

	public void setWkplcClassNm(String wkplcClassNm) {
		this.wkplcClassNm = wkplcClassNm;
	}

	public String getWkplcMethodNm() {
		return wkplcMethodNm;
	}

	public void setWkplcMethodNm(String wkplcMethodNm) {
		this.wkplcMethodNm = wkplcMethodNm;
	}
	
	public String getTechCls() {
		return techCls;
	}

	public void setTechCls(String techCls) {
		//this.techCls = techCls;
		this.techCls = "POST";
	}
	
	//public List<TaskMethod> getTaskMethods() {
//		return taskMethods;
//	}
	
	//public void setTaskMethods(List<TaskMethod> taskMethods) {
//		this.taskMethods = taskMethods;
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public WorkTaskMethodSetting clone() throws CloneNotSupportedException {
		  return (WorkTaskMethodSetting)super.clone();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String str = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			str = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
}
