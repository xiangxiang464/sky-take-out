package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
    }
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充");
        // 获取方法签名和@AutoFill注解中的操作类型（INSERT或UPDATE）
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取操作实体
        Object [] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0]; // 因为这里获取到的实体是一个object，像调set方法就需要后面的反射操作
        // 获取赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if (operationType == OperationType.INSERT) {
            // 插入操作，为插入的实体对象填充创建时间、更新时间和创建人、修改人属性
            try {
                entity.getClass().getMethod("setCreateTime", LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod("setCreateUser", Long.class).invoke(entity, currentId);
                entity.getClass().getMethod("setUpdateUser", Long.class).invoke(entity, currentId);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }else{
            // 更新操作，为更新实体对象填充更新时间和修改人属性
            try {
                entity.getClass().getMethod("setUpdateTime", LocalDateTime.class).invoke(entity, now);
                entity.getClass().getMethod("setUpdateUser", Long.class).invoke(entity, currentId);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }


    }


}
