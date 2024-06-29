package org.example.common.aspect;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.common.annotation.MVCLoggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

@Component
@Aspect
@Order(0)
public class MVCLoggingAspect extends BaseAspect {

	private static final Logger applicationLogger = LogManager.getLogger("applicationLogs." + MVCLoggingAspect.class.getName());

	@Autowired
	protected Environment environment;

	@Before(value = "classAnnotatedWithMVCLoggable(mvcLoggable) && publicMethod()", argNames = "joinPoint,mvcLoggable")
	public void beforeMethod(JoinPoint joinPoint, MVCLoggable mvcLoggable) {
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

	@AfterReturning(value = "classAnnotatedWithMVCLoggable(mvcLoggable) && publicMethod()", returning = "serverResponse", argNames = "joinPoint,mvcLoggable,serverResponse")
	public void afterReturnMethod(JoinPoint joinPoint, MVCLoggable mvcLoggable, Object serverResponse) {
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
		applicationLogger.info(LOG_PREFIX + "Servlet Request : ==> " + sbMethod + " of '" + className + "' class has been successfully initiated from server and navigate to view [" + serverResponse + "]" + LOG_SUFFIX);
		applicationLogger.info(LOG_BREAKER_CLOSE);
	}

	@Pointcut("@within(mvcLoggable)")
	public void classAnnotatedWithMVCLoggable(MVCLoggable mvcLoggable) {
	}
}
