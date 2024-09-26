package smartsuite.config.workplace.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import smartsuite.config.workplace.WorkplaceProvider;
import smartsuite.config.workplace.service.WorkplaceProviderService;
import smartsuite.config.workplace.lib.WorkplaceCacheConst;
import smartsuite.config.workplace.lib.WorkplaceCacheManager;
import smartsuite.config.workplace.vo.WorkTaskMethodSetting;
import smartsuite.security.authentication.Auth;

@Aspect
@Service
public class ExecutionWorkplaceAdvisor {

	private static Logger logger = LoggerFactory.getLogger(ExecutionWorkplaceAdvisor.class);

	@Value("#{loggingProperties['logging.level']}")
	SLF4JLogLevel logLevel;

	@Inject
	WorkplaceProvider workplaceProvider;

	@Inject
	WorkplaceCacheManager workplaceCache;

	@Inject
	WorkplaceProviderService workplaceProviderService;

	@AfterReturning(value = "@target(org.springframework.stereotype.Service) && (within(smartsuite.app.bp.pro..*) " +
			"||  within(smartsuite.app.bp.rfx..*) || within(smartsuite.app.sp.rfx..*)"+ "||  within(smartsuite.app.bp.edoc..*)" + "||  within(smartsuite.app.bp.contract..*) || within(smartsuite.app.sp.contract..*)" + "|| within(smartsuite.app.shared.status..*)" +
			"|| within(smartsuite.app.bp.srm..*) || within(smartsuite.app.sp.pro..*) || within(smartsuite.app.common.status..*)) && (bean(*Service) || bean(*Processor)) "
			, returning = "returnValue")
	public void excutionAfterService(JoinPoint joinPoint, Object returnValue) {

		StringBuffer log = new StringBuffer();
		writeLog("###################### check workplace method settings :: "+joinPoint.getSignature().getDeclaringTypeName() +","+joinPoint.getSignature().getName());

		try {
			//aop의 개념에서의 pre와 post가 아닙니다. 트랜잭션 처리를 통한 pre와 post로 remove와 insert가 구분되어있습니다.
			//afterReturning을 통하여 메소드가 리턴된 시점에 해당aop가 호출되고 트랜잭션 처리에따라 remov(pre) , insert(post)가 구분되어집니다.헷갈릴수있어 적어둡니다.
			String occurKey = makeCacheKey(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), WorkplaceCacheConst.TECH_CLS_POST);
			String remvKey  = makeCacheKey(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), WorkplaceCacheConst.TECH_CLS_PRE);
			Map<String, Object> param = makeParameters(joinPoint);

			Map<String, Object> data = null;

			WorkTaskMethodSetting remv = workplaceCache.getWorkTaskMethodSetting(Auth.getCurrentTenantId(), remvKey);
			if(remv != null){
				String remvString = ObjectUtils.getDisplayString(remv) == null? "" : ObjectUtils.getDisplayString(remv);

				log.append("\r\n###################### settings :: ");
				log.append(remvString);
				data = param;

				if(data != null) {
					workplaceProviderService.deleteTask(remv, data);

					String serviceString = joinPoint.getSignature().getDeclaringTypeName() == null? "" : joinPoint.getSignature().getDeclaringTypeName();
					String nameString = joinPoint.getSignature().getName() == null? "" : joinPoint.getSignature().getName();
					String paramString = ObjectUtils.getDisplayString(param) == null? "" : ObjectUtils.getDisplayString(param);
					String dataString = ObjectUtils.getDisplayString(data) == null? "" : ObjectUtils.getDisplayString(data);

					log.append("\r\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ :: remv");
					log.append("\r\n service :: ");
					log.append(serviceString);
					log.append("\r\n method :: " );
					log.append(nameString);
					log.append("\r\n parameter :: ");
					log.append(paramString);
					log.append("\r\n data :: ");
					log.append(dataString);
					log.append("\r\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ :: ");
				}
			}

			WorkTaskMethodSetting occur = workplaceCache.getWorkTaskMethodSetting(Auth.getCurrentTenantId(), occurKey);
			if(occur != null){
				log.append("\r\n###################### settings :: ");
				log.append(ObjectUtils.getDisplayString(occur));

				data = param;

				if(data != null) {

					writeLog("###################### check workplace method return value :: " + returnValue);
					//workplaceProviderService.insertTask(occur, data, returnValue);
					workplaceProviderService.insertTask(occur, data);

					String serviceString = joinPoint.getSignature().getDeclaringTypeName() == null? "" : joinPoint.getSignature().getDeclaringTypeName();
					String nameString = joinPoint.getSignature().getName() == null? "" : joinPoint.getSignature().getName();
					String paramString = ObjectUtils.getDisplayString(param) == null? "" : ObjectUtils.getDisplayString(param);
					String dataString = ObjectUtils.getDisplayString(data) == null? "" : ObjectUtils.getDisplayString(data);

					log.append("\r\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ :: occur");
					log.append("\r\n service :: ");
					log.append(serviceString);
					log.append("\r\n method :: " );
					log.append(nameString);
					log.append("\r\n parameter :: ");
					log.append(paramString);
					log.append("\r\n data :: ");
					log.append(dataString);
					log.append("\r\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ :: ");
				}
			}
			writeLog(log.toString());
		} catch (Exception e) {
			logger.error(e.getStackTrace().toString());
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> makeParameters(JoinPoint joinPoint) {
		Map<String, Object> param = Maps.newHashMap();
		Map<String, Object> arg = Maps.newHashMap();
		Object[] args = joinPoint.getArgs();

		if (args != null && args.length > 0 && args[0] != null) {
			if (args[0] instanceof Map) {
				// Object로 받은 args[0]이 Map일 경우
				arg = (Map<String, Object>) args[0];
			} else if (args[0] instanceof List) {
				// Object로 받은 args[0]이 List일 경우
				List<Map> list = (List<Map>) args[0];
				for(Map obj : list) {
					arg.putAll(obj);
				}
			} else if(args[0].getClass().getSimpleName().equals("ContractReq")) {
				// 계약 요청인 경우
				try {
					arg = convertObjectToMap(args[0]);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else if(args[0].getClass().getSimpleName().equals("String")) {
				arg.put(joinPoint.getSignature().getName(), args[0].toString());
			}

			if(arg != null) {
				Iterator<String> i = arg.keySet().iterator();
				while(i.hasNext()){
					String key = i.next();
					//param 안에 {data:{}}형태로 들어있는경우.
					if(arg.get(key) instanceof Map){
						param = (Map<String, Object>)arg.get(key);
						break;
					}else{
						param = arg;
						//param에 data가 바로 바인딩되어있는 경우처리.
						break;
					}
				}
			}
		}
		writeLog("###################### parameters ::: " + ObjectUtils.getDisplayString(param));
		return param;
	}

	private Map<String, Object> convertObjectToMap(Object object) throws Exception {
		Method[] methods = object.getClass().getMethods();

		Map<String, Object> map = Maps.newHashMap();
		for (Method m : methods) {
			if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
				Object value = m.invoke(object);
				map.put(m.getName().substring(3).toLowerCase(),value);
			}
		}

		return map;
	}

	private String makeCacheKey(String fullClass, String method, String type){
		String key = WorkplaceCacheConst.PREFIX_CACHE_KEY.concat("_" + type).concat("_"+fullClass.substring(fullClass.lastIndexOf(".")+1)).concat("_"+method);
		key = key.toUpperCase();
		writeLog("###################### workplace method setting key ::: "+key);
		return key;
	}

	private void writeLog(String log) {
		switch(logLevel) {
			case TRACE:
				logger.trace(log);
				break;
			case DEBUG:
				logger.debug(log);
				break;
			case INFO:
				logger.info(log);
				break;
			case WARN:
				logger.warn(log);
				break;
			case ERROR:
				logger.error(log);
				break;
			default:
				break;
		}
	}
}
