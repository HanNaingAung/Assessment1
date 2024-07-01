package org.example.common.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.common.annotation.RestLoggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@Component
@Aspect
@Order(0)
public class RestLoggingAspect extends BaseAspect {
	private static final Logger applicationLogger = LogManager.getLogger("applicationLogs." + RestLoggingAspect.class.getName());

	@Autowired
	protected Environment environment;

	@Before(value = "classAnnotatedWithRestLoggable(restLoggable) && publicMethod()", argNames = "joinPoint,restLoggable")
	public void beforeMethod(JoinPoint joinPoint, RestLoggable restLoggable) {
		applicationLogger.info(LOG_BREAKER_OPEN);
		Method method = getMethod(joinPoint);

		// get RequestMapping annotation of target class
		RequestMapping rootMapper = getRootMapper(joinPoint);
		// skip for non-servlet request methods.
		if (rootMapper == null) {
			return;
		}
		// get class level request mapping URL
		String rootMappingURL = rootMapper.value()[0];

		String className = getClassName(joinPoint);

		String mappingURL = rootMappingURL;

		MethodMappingInfo methodMapping = getMethodMappingInfo(method);
		mappingURL += methodMapping.getMethodMappingUrl();

		// create for method String
		String sbMethod = " '" + getMethodName(joinPoint) + "'" + " Method [" + "mappingURL ('" + mappingURL + "') ," + "requestMethod ('" + methodMapping.getRequestMethod() + "') " + "] ";
		applicationLogger.info(LOG_PREFIX + "Client request for" + sbMethod + " of '" + className + "' class." + LOG_SUFFIX);

		logRequestParams(joinPoint.getArgs());
	}

	@AfterReturning(value = "classAnnotatedWithRestLoggable(restLoggable) && publicMethod()", returning = "serverResponse", argNames = "joinPoint,restLoggable,serverResponse")
	public void afterReturnMethod(JoinPoint joinPoint, RestLoggable restLoggable, Object serverResponse) {
		Method method = getMethod(joinPoint);

		// get RequestMapping annotation of target class
		RequestMapping rootMapper = getRootMapper(joinPoint);
		// skip for non-servlet request methods.
		if (rootMapper == null) {
			return;
		}
		// get class level request mapping URL
		String rootMappingURL = rootMapper.value()[0];

		String className = getClassName(joinPoint);

		String mappingURL = rootMappingURL;

		MethodMappingInfo methodMapping = getMethodMappingInfo(method);
		mappingURL += methodMapping.getMethodMappingUrl();

		// create for method String
		String sbMethod = " '" + getMethodName(joinPoint) + "'" + " Method [" + "mappingURL ('" + mappingURL + "') ," + "requestMethod ('" + methodMapping.getRequestMethod() + "') " + "] ";
		applicationLogger.info(LOG_PREFIX + "Ajax Request : ==> " + sbMethod + " of '" + className + "' class has been done." + LOG_SUFFIX);
		if (applicationLogger.isDebugEnabled()) {
			applicationLogger.debug("==================== Ajax Response from server ====================");
			if (serverResponse instanceof ResponseEntity<?> responseDetail) {
				applicationLogger.debug("Response Status ==> " + responseDetail.getStatusCode());
				applicationLogger.debug("Response Header ==> " + responseDetail.getHeaders());
				applicationLogger.debug("Response Body ==> ....");
				if (responseDetail.getBody() instanceof Map<?, ?> map) {
					map.forEach((key, value) -> {
						applicationLogger.debug(key + " ==> ");
						if (value instanceof Collection) {
							showEntriesOfCollection((Collection<?>) value);
						}
						else {
							applicationLogger.debug(" >>> " + value);
						}
					});
				}
				else if (responseDetail.getBody() instanceof Collection) {
					showEntriesOfCollection((Collection<?>) responseDetail.getBody());
				}
				else {
					applicationLogger.debug(" >>> " + responseDetail.getBody());
				}
			}
			else {
				applicationLogger.debug(" >>> " + serverResponse);
			}
		}
		applicationLogger.info(LOG_BREAKER_CLOSE);
	}

	@Pointcut("@within(restLoggable)")
	public void classAnnotatedWithRestLoggable(RestLoggable restLoggable) {
	}

	private <T> void showEntriesOfCollection(Collection<T> collection) {
		if (collection != null) {
			for (Object obj : collection) {
				if (obj != null) {
					applicationLogger.info(" >>> " + obj);
				}
			}
		}
	}
}
