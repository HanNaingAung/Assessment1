//package org.example.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class MyAspect {
//
//    @Around("execution(* org.example..*(..))") // Adjust the pointcut expression as needed
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("Before method: " + joinPoint.getSignature().getName());
//        Object result = joinPoint.proceed();
//        System.out.println("After method: " + joinPoint.getSignature().getName());
//        return result;
//    }
//}
