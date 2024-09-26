package smartsuite.config.workplace.vo;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TaskMethod implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6431761559843842227L;
	
	/* task method id */
	String taskMethodId;
	
	/* task class name */
	String taskClssNm;
	
	/* task method name */
	String taskMethodNm;
	
	public String getTaskMethodId() {
		return taskMethodId;
	}

	public void setTaskMethodId(String taskMethodId) {
		this.taskMethodId = taskMethodId;
	}

	public String getTaskClssNm() {
		return taskClssNm;
	}

	public void setTaskClssNm(String taskClssNm) {
		this.taskClssNm = taskClssNm;
	}

	public String getTaskMethodNm() {
		return taskMethodNm;
	}

	public void setTaskMethodNm(String taskMethodNm) {
		this.taskMethodNm = taskMethodNm;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public TaskMethod clone() throws CloneNotSupportedException {
		  return (TaskMethod)super.clone();
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
		} catch (JsonGenerationException e) {
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
