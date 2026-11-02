package hu.mik.pte.prog5.mvc.aspect;

import hu.mik.pte.prog5.mvc.LoggingInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(public * hu.mik.pte.prog5.mvc.service.StudentService.*(..))")
    public Object logArroundServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[AOP] Enter: "+methodName + "params:"+ Arrays.toString(joinPoint.getArgs()));
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("[AOP] Exit:"+methodName + " ("+duration+" ms), result: "+result);

        return result;
    }
}
