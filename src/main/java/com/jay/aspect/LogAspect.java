package com.jay.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect  //定义切面
@Component  //容器运行时生成对象，必须添加
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class); //logger日志类

    @Before("execution(* com.jay.controller.IndexController.*(..))")  //在监控方法执行之前执行
    public void beforeMethod(JoinPoint joinPoint){  //观察者设计模式，  JoinPoint连接点，getArgs()获取连接点方法运行时的入参列表
        StringBuilder sb = new StringBuilder();
        for(Object arg : joinPoint.getArgs()){
            sb.append("arg:" + arg.toString() + "|");
        }

        logger.info("before method:" + sb.toString());
    }

    @After("execution(* com.jay.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        logger.info("after method");
    }

}
