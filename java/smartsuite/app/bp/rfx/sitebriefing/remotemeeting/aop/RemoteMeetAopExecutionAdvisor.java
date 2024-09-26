package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.aop;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RemoteMeetAopExecutionAdvisor {

    public static final Logger LOGGER = LoggerFactory.getLogger("remoteMeetAopExecution");

    @Value("#{loggingProperties['logging.level']}")
    SLF4JLogLevel logLevel;

    public RemoteMeetAopExecutionAdvisor(){
        //RemoteMeetAopExecutionAdvisor
    }

    @SuppressWarnings("PMD")
    @AfterReturning("remoteMeetMethod()")
    private void loggingApprovalBeforeSuc(JoinPoint joinPoint) throws Throwable{

        Object[] args = joinPoint.getArgs() == null? null : joinPoint.getArgs();
        StringBuffer log = new StringBuffer();
        String className = joinPoint.getTarget().getClass().getName() == null? "" : joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName() == null? "" : joinPoint.getSignature().getName();
        log.append("\r\n*************************************************************************");
        log.append("\r\n********     REMOTE MEETING LOGGING BEFORE JOIN POINT LOGGING          ********");
        log.append("\r\n controller-class               = ");
        log.append(className);
        log.append("\r\n method-name                    = ");
        log.append(methodName);


        /*  JDK 1.8 에서는 아래와 같은 코드로 사용이 가능하다 하니, 필요하신 분들은 사용하세요.
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            parameterName = method.getParameters()[i].getName();
            if (parameterName.equals("id"))
                id = (Long) parameterValues[i];
            if (parameterName.equals("token"))
                token = (String) parameterValues[i];
        }
        */

        if(null != args) {
            for(int i=0; i < args.length; i++){
                log.append("\r\n REMOTE MEETING PARAMETER          ="  );
                log.append(args[i].toString());
            }
        }
        log.append("\r\n*************************************************************************\r\n");
        this.writeLog(log.toString());

    }


    @SuppressWarnings("PMD")
    @AfterThrowing(value = "remoteMeetMethod()" , throwing = "exception")
    private void loggingApprovalBeforeSuc(JoinPoint joinPoint, Exception exception) throws Throwable{

        Object[] args = joinPoint.getArgs() == null? null : joinPoint.getArgs();
        StringBuffer log = new StringBuffer();
        String className = joinPoint.getTarget().getClass().getName() == null? "" : joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName() == null? "" : joinPoint.getSignature().getName();

        log.append("\r\n*************************************************************************");
        log.append("\r\n********     REMOTE MEETING LOGGING BEFORE JOIN POINT LOGGING          ********");
        log.append("\r\n controller-class               = ");
        log.append(className);
        log.append("\r\n method-name                    = ");
        log.append(methodName);

        if(null != args) {
            for(int i=0; i < args.length; i++){
                log.append("\r\n REMOTE MEETING PARAMETER          =");
                log.append(args[i].toString());
            }
        }
        log.append("\r\n REMOTE MEETING ERROR EXCEPTION LOG =");
        log.append(exception.getMessage());

        log.append("\r\n*************************************************************************\r\n");
        this.writeLog(log.toString());
    }


    /**
     * point cut
     */
    @Pointcut("execution(* smartsuite.app.bp.rfx.sitebriefing.remotemeeting.*.*(..))")
    public void remoteMeetMethod() {
        LOGGER.info("**********************RemoteMeetAopExecutionAdvisor**********************");
    }


    private void writeLog(String log) {
        switch(this.logLevel) {
            case TRACE:
                LOGGER.trace(log);
                break;
            case DEBUG:
                LOGGER.debug(log);
                break;
            case INFO:
                LOGGER.info(log);
                break;
            case WARN:
                LOGGER.warn(log);
                break;
            case ERROR:
                LOGGER.error(log);
                break;
            default:
                LOGGER.info(log);
                break;
        }

    }

}
