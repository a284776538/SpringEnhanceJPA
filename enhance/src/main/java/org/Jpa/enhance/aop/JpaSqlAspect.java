package org.Jpa.enhance.aop;

import cn.hutool.core.date.DateUtil;
import org.Jpa.enhance.Jpa.JpaImpl;
import org.apache.commons.beanutils.ConvertUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * JPA拦截器
 */
@Aspect
//@Component
public class JpaSqlAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    @Lazy
    private JpaImpl baseJpa;

    //拦截方法上有@Query
    @Around("@annotation(org.springframework.data.jpa.repository.Query)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Object o = null;
        //如果是自定义查询
        boolean checkModelQuery = checkModelQuery(point);
        if (checkModelQuery) {
            o = modelQuery(point);
            return o;
        }
        o = point.proceed(point.getArgs());

        return o;
    }

    private Object getSQLParam(Object object){
        if(object instanceof  Date){
            object =  DateUtil.format((Date) object,"yyyy-MM-dd HH:mm:ss.sss");
        }
        if(object instanceof  String){
            object = " '"+object.toString()+"' ";
        }else{
            object =  ConvertUtils.convert(object, String.class) ;
        }
        return  object;
    }
    private Object modelQuery(JoinPoint joinPoint) throws  Exception{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Query query =  method.getAnnotation(Query.class);
        //如果有声明好的countSql就用声明的
        String countQuery =  query.countQuery().replace(" ","");
        String countSql = null;
        if(countQuery.length()>0){
           countSql = query.countQuery();
        }
       String  queryValue  = "";
        if(query!=null){
            queryValue= query.value();
        }
        Object[]  args =   joinPoint.getArgs();
        // 参数名
        Annotation[][] annotationss= method.getParameterAnnotations();
        for ( int i=0;i<annotationss.length;i++) {
            if (annotationss[i]==null||annotationss[i].length<=0) {
                continue;
            }
            Param param = (Param) annotationss[i][0];
            //判断参数是否是数组
            String pValue = null;
            if ( args[i] instanceof  Iterable){
                Iterable arrayParam = (Iterable) args[i];
                if(!arrayParam.iterator().hasNext()){
                    throw new Exception("参数不能为空数组.");
                }
                String sqlParam ="(";
                for (Object p:arrayParam) {
                    sqlParam =sqlParam+ getSQLParam( p).toString()+",";
                }
                if(sqlParam.endsWith(",")){
                    sqlParam = sqlParam.substring(0,sqlParam.length()-1);
                }
                sqlParam =sqlParam +")";
                pValue = sqlParam;
            }else{
                 pValue =  getSQLParam( args[i]).toString();
            }
            queryValue = queryValue+" ";
            queryValue =queryValue.replace(":"+param.value()+" ",pValue+" ");
            countSql =countSql==null?null:countSql.replace(":"+param.value()+" ",pValue+" ");
        }
        Signature s = joinPoint.getSignature();
        String str = s.toLongString().split(" ")[2];


        if(str.endsWith(".List")||str.endsWith(".Set")){
            List<Object> o = baseJpa.findBySql(queryValue,getSubClass(method),query.nativeQuery());
            return  str.endsWith(".Set")?new HashSet(o ):o;
        }
        if(str.endsWith("org.springframework.data.domain.Page")){
            PageRequest pageable = getPageable(joinPoint);
            String sqlWords [] = queryValue.split(" ");
            String fastSelectWord =null;
            String fastFromWord =null;
            // 获取第一个 select 和from 的全拼，避免大小写导致无法匹配
            for (String word: sqlWords) {
                if(fastSelectWord ==null && word.toUpperCase().equals("SELECT")){
                    fastSelectWord = word;
                }
                if(fastFromWord ==null && word.toUpperCase().equals("FROM")){
                    fastFromWord = word;
                }
                if(fastFromWord!=null&&fastSelectWord!=null){
                    break;
                }
            }
            //获取查询字段，用于替换count 字段
            String replaceFields = queryValue.substring(queryValue.indexOf(fastSelectWord),queryValue.indexOf(fastFromWord)).replace(fastSelectWord,"");

            countSql = countSql==null?queryValue.replace(replaceFields,"*"):countSql;
            Object o = baseJpa.findBySql(queryValue, countSql,getSubClass(method),query.nativeQuery(),pageable);
            return  o;
        }

        Object o = baseJpa.findSql(queryValue, method.getReturnType(), query.nativeQuery());
        return o;

    }


    private  Class getSubClass(Method method ){
        try {
            String returnType = method.getAnnotatedReturnType().getType().getTypeName().replace("org.springframework.data.domain.Page","")
                    .replace("<","").replace(">","").replace("java.util.List","")
                    .replace("java.util.Set","");
            Class returnClass =  Class.forName(returnType);
            return returnClass;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    private PageRequest getPageable(JoinPoint joinPoint){
        Object[]  args =   joinPoint.getArgs();
        if(args==null||args.length<=0){
            return  null;
        }
        for (Object object: args) {
            if(object  instanceof PageRequest){
                return (PageRequest)object;
            }
        }
        return  null;
    }

    /**
     *
     * @param joinPoint
     * @return false 走正常查询, true 走自定义查询
     */
    private boolean checkModelQuery(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AnnotatedType[]  annotatedTypes = method.getDeclaringClass().getAnnotatedInterfaces();
        String returnType = method.getAnnotatedReturnType().getType().getTypeName().replace("org.springframework.data.domain.Page","")
                .replace("<","").replace(">","");

        if(returnType.equals("long")||returnType.equals("int")||returnType.startsWith("java.lang")){
            return false;
        }
        if(annotatedTypes==null||annotatedTypes.length<=0){
            return  true;
        }
        for (AnnotatedType  annotatedType : annotatedTypes ){
            String name =  annotatedType.getType().getTypeName();
            System.out.println(name);
            if(name.indexOf(returnType)>=0){
                return  false;

            }
        }
        return  true;
    }








}