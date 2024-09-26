package smartsuite.config.workplace.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.config.workplace.vo.WorkTaskMethodSetting;

/**
 * WorkplaceProvider 관련 처리하는 서비스 Class입니다.
 *
 * @see 
 * @FileName WorkplaceProviderService.java
 * @package smartsuite.workplace
 * @Since 2018. 12. 11
 */
@Service
@EnableCaching(proxyTargetClass=true)
public class WorkplaceProviderService implements ApplicationContextAware{
	
	/** The Constant logger. */
	private static final Logger LOG = LoggerFactory.getLogger(WorkplaceProviderService.class);
	
	/** The actx. */
	ApplicationContext actx;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		actx = applicationContext;
	}
	
	/**
	 * task 입력한다.
	 *
	 * @author : Yeon-u Kim
	 * @param settings the settings
	 * @param data the data
	 * @return the int
	 * @Date : 2018. 12. 11
	 * @Method Name : insertTask
	 */
	public int insertTask(WorkTaskMethodSetting settings, Map<String,Object> data){

		int retval = 0;
		
		//List<TaskMethod> taskMakers = settings.getTaskMethods();
		
		//for(TaskMethod taskMaker: taskMakers){
			String classNm = (String)settings.getWkplClassNm();
			String methodNm = (String)settings.getWkplcMethodNm();
			
			if(classNm != null && methodNm != null){
				classNm = classNm.substring(0, 1).toLowerCase() + classNm.substring(1);
				try {
	            	Object instance = actx.getBean(classNm);
	            	Class<?>[] cArg = new Class[1];
	                cArg[0] = Map.class;

		            Method methods = instance.getClass().getMethod(methodNm,cArg);
		            methods.invoke(instance, data);
	                //invoke()를 통해 메소드 호출 가능
	                //첫번째 파라미터는 해당 메소드를 가진 인스턴스, 두번쨰 파라미터는 해당 메소드의 파라미터, 세번째 파라미터는 해당 메소드의 리턴 데이터
		        } catch (IllegalAccessException e) {
					LOG.error(e.getMessage());
				} catch (IllegalArgumentException e) {
					LOG.error(e.getMessage());
				} catch (InvocationTargetException e) {
					LOG.error(e.getMessage());
				} catch (NoSuchMethodException e) {
					LOG.error(e.getMessage());
				} catch (SecurityException e) {
					LOG.error(e.getMessage());
				} catch (DataAccessException e) {
					LOG.error(e.getMessage());
				}
				retval++;
			}
		//}
		return retval;
		
	}

	public int insertTask(WorkTaskMethodSetting settings, Map<String,Object> data, final Object returnData){

		int retval = 0;

		//List<TaskMethod> taskMakers = settings.getTaskMethods();

		//for(TaskMethod taskMaker: taskMakers){
		String classNm = (String)settings.getWkplClassNm();
		String methodNm = (String)settings.getWkplcMethodNm();

		if(classNm != null && methodNm != null){
			classNm = classNm.substring(0, 1).toLowerCase() + classNm.substring(1);
			try {
				Object instance = actx.getBean(classNm);
				Class<?>[] cArg = new Class[2];
				cArg[0] = Map.class;
				cArg[1] = Object.class;
				Method methods = instance.getClass().getMethod(methodNm,cArg);
				methods.invoke(instance, new Object[] {data, returnData});
				//invoke()를 통해 메소드 호출 가능
				//첫번째 파라미터는 해당 메소드를 가진 인스턴스, 두번쨰 파라미터는 해당 메소드의 파라미터, 세번째 파라미터는 해당 메소드의 리턴 데이터
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage());
			} catch (IllegalArgumentException e) {
				LOG.error(e.getMessage());
			} catch (InvocationTargetException e) {
				LOG.error(e.getMessage());
			} catch (NoSuchMethodException e) {
				LOG.error(e.getMessage());
			} catch (SecurityException e) {
				LOG.error(e.getMessage());
			} catch (DataAccessException e) {
				LOG.error(e.getMessage());
			}
			retval++;
		}
		//}
		return retval;

	}
	
	/**
	 * task 삭제한다.
	 *
	 * @author : Yeon-u Kim
	 * @param settings the settings
	 * @param data the data
	 * @return the int
	 * @Date : 2018. 12. 11
	 * @Method Name : deleteTask
	 */
	public int deleteTask(WorkTaskMethodSetting settings, Map<String,Object> data){
		int retval = 0;
		
		//List<TaskMethod> taskRemovers = settings.getTaskMethods();
		
		//for(TaskMethod taskRemover: taskRemovers){
			String classNm = (String)settings.getTaskClassNm();
			String methodNm = (String)settings.getTaskMethodNm();
			
			if(classNm != null && methodNm != null){
				classNm = classNm.substring(0, 1).toLowerCase() + classNm.substring(1);
				try {
	            	Object instance = actx.getBean(classNm);
	            	Class<?>[] cArg = new Class[1];
	                cArg[0] = Map.class;
		            Method methods = instance.getClass().getMethod(methodNm,cArg);
		            methods.invoke(instance,data);
	                //invoke()를 통해 메소드 호출 가능
	                //첫번째 파라미터는 해당 메소드를 가진 인스턴스, 두번쨰 파라미터는 해당 메소드의 파라미터
		        } catch (IllegalAccessException e) {
					LOG.error(e.getMessage());
				} catch (IllegalArgumentException e) {
					LOG.error(e.getMessage());
				} catch (InvocationTargetException e) {
					LOG.error(e.getMessage());
				} catch (NoSuchMethodException e) {
					LOG.error(e.getMessage());
				} catch (SecurityException e) {
					LOG.error(e.getMessage());
				}
				retval++;
			}
		//}
		return retval;
	}
}
