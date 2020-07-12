//package gitee.hongzihao.ejpa.mongodb;
//
//import cn.hutool.core.date.DateUtil;
//import com.mongodb.DBObject;
//import com.mongodb.util.JSON;
//import gitee.hongzihao.ejpa.Jpa.JpaImpl;
//import gitee.hongzihao.ejpa.annottation.ModelQuery;
//import gitee.hongzihao.ejpa.annottation.MongodbModelQuery;
//import gitee.hongzihao.ejpa.module.pojo.QueryData;
//import gitee.hongzihao.ejpa.run.EjpaRun;
//import gitee.hongzihao.ejpa.service.SlowQueryService;
//import gitee.hongzihao.ejpa.util.ClassUtil;
//import org.apache.commons.beanutils.ConvertUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.repository.query.Param;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.AnnotatedType;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * JPA拦截器
// */
//@Aspect
//public class  MongodbJpaSqlAspect {
//
//    //公用线程池
//    public static ExecutorService publicScheduled = Executors.newFixedThreadPool(5);
////    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    @Lazy
//    private MongoTemplate mongoTemplate;
//
//    @Autowired
//    @Lazy
//    private EjpaRun ejpaRun ;
//
//    @Autowired
//    @Lazy
//    private SlowQueryService slowQueryService;
//
//    拦截方法上有@Query
//    @Around("@annotation(gitee.hongzihao.ejpa.annottation.MongodbModelQuery)||@annotation(org.springframework.data.jpa.repository.Query)")
//    public Object doAround(ProceedingJoinPoint point) throws Throwable {
//        ejpaRun.run();
//        long startTime =System.currentTimeMillis();
//
//        Object o = null;
//        //如果是自定义查询
////        boolean checkModelQuery = checkModelQuery(point);
//        MethodSignature methodSignature = (MethodSignature) point.getSignature();
//        Method method = methodSignature.getMethod();
//        MongodbModelQuery modelQuery =  method.getAnnotation(MongodbModelQuery.class);
//        QueryData queryData = new QueryData();
//        //使用ModelQuery
//        if (modelQuery!=null) {
//            //如果是自定义查询，也就是Class return的 sql或者Jsql
//            if(modelQuery.value()==null||modelQuery.value().equals("")){
//                o = point.proceed(point.getArgs());
//                queryData = modelQuery(point,o.toString());
//                o = queryData.getReturnData();
//            }else {
//                queryData =   modelQuery(point,null);
//                o = queryData.getReturnData();
//            }
//        }else{
//            Query query =  method.getAnnotation(Query.class);
//            queryData.setSql(query.value());
//            o = point.proceed(point.getArgs());
//        }
//
//        long endTime =System.currentTimeMillis();
//        saveSlowQuery(methodSignature.toString().split(" ")[1],queryData.getSql(),(endTime-startTime)/1000 );
//        return o;
//    }
//
//    private void saveSlowQuery(String method ,String sql,long time){
//        if(sql==null||sql.equals("")){
//            return;
//        }
//        publicScheduled.execute(new Runnable() {
//            @Override
//            public void run() {
//                slowQueryService.save(method,sql,time );
//            }
//        });
//    }
//
//    private Object getSQLParam(Object object){
//        if(object instanceof  Date){
//            object =  DateUtil.format((Date) object,"yyyy-MM-dd HH:mm:ss.sss");
//        }
//        if(object instanceof  String){
//            object = " '"+object.toString()+"' ";
//        }else{
//            object =  ConvertUtils.convert(object, String.class) ;
//        }
//        return  object;
//    }
//    private QueryData modelQuery(JoinPoint joinPoint,String queryValue) throws  Exception{
//
//        QueryData queryData = new QueryData();
//        Map<String,Object> parameter = new HashMap<>();
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        method.getParameters();
//        ModelQuery query =  method.getAnnotation(ModelQuery.class);
////        Query query =  method.getAnnotation(Query.class);
//        //如果有声明好的countSql就用声明的
//        String countQuery =  query.countQuery().replace(" ","");
//        String countSql = null;
//        if(countQuery.length()>0){
//           countSql = query.countQuery();
//        }
//        if(queryValue==null){
//            queryValue= query.value();
//        }
//        Object[]  args =   joinPoint.getArgs();
//        // 参数名
//        Annotation[][] annotationss= method.getParameterAnnotations();
//
//
//
//        for ( int i=0;i<annotationss.length;i++) {
//            if (annotationss[i]==null||annotationss[i].length<=0) {
//                continue;
//            }
//            Param param = (Param) annotationss[i][0];
//            //判断参数是否是数组
//            String pValue = null;
////            if(args[i]==null){
////                continue;
////            }
//            parameter.put(param.value(),args[i]);
////            if ( args[i] instanceof  Iterable){
////                Iterable arrayParam = (Iterable) args[i];
////
////                if(!arrayParam.iterator().hasNext()){
////                    throw new Exception("参数不能为空数组.");
////                }
////                String sqlParam ="(";
////                for (Object p:arrayParam) {
////                    sqlParam =sqlParam+ getSQLParam( p).toString()+",";
////                }
////                if(sqlParam.endsWith(",")){
////                    sqlParam = sqlParam.substring(0,sqlParam.length()-1);
////                }
////                sqlParam =sqlParam +")";
////                pValue = sqlParam;
////            }else{
////                 pValue =  getSQLParam( args[i]).toString();
////            }
////            queryValue = queryValue+" ";
////            String queryValues[] = queryValue.split(":"+param.value());
////            queryValue="";
////            for (int ii=0 ;ii<queryValues.length;ii++){
////                if(ii<queryValues.length-1){
////                    queryValue =queryValue+ queryValues[ii]+pValue;
////                }else{
////                    queryValue =queryValue+ queryValues[ii];
////                }
////            }
////            if(countSql!=null){
////                String countSqls[] = countSql.split(":"+param.value());
////                countSql="";
////                for (int ii=0 ;ii<countSqls.length;ii++){
////                    if(ii<countSqls.length-1){
////                        countSql =countSql+ countSqls[ii]+pValue;
////                    }else{
////                        countSql =countSql+ countSqls[ii];
////                    }
////                }
////            }
//        }
//        queryData.setSql(queryValue);
//
//
//        return queryData;
//
//    }
//
//
//    private String ModelQueryClassString( Method method){
//        ModelQuery modelQuery =  method.getAnnotation(ModelQuery.class);
//        if(modelQuery!=null&&modelQuery.retrunClass()!=null){
//            return modelQuery.retrunClass().replace("Page","org.springframework.data.domain.Page")
//                    .replace("List","java.util.List").replace("Set","java.util.Set");
//        }
//        return null;
//    }
//
//    private  Class getSubClass(Method method ) throws Exception{
//        try {
//            String returnType =ModelQueryClassString(method)==null?method.getAnnotatedReturnType().getType().getTypeName():ModelQueryClassString(method);
//            returnType = returnType.replace("org.springframework.data.domain.Page","")
//                    .replace("<","").replace(">","").replace("java.util.List","")
//                    .replace("java.util.Set","");
//            if(returnType.equals("Integer")||returnType.equals("String")||returnType.equals("Double")||returnType.equals("Float")||returnType.equals("Long")){
//                returnType ="java.lang."+returnType;
//            }
//            Class returnClass =  Class.forName(returnType.replace(" ",""));
//            return returnClass;
//        }catch (Exception e){
//            throw new Exception("查询参数类型有问题");
//        }
//    }
//
//    private PageRequest getPageable(JoinPoint joinPoint){
//        Object[]  args =   joinPoint.getArgs();
//        if(args==null||args.length<=0){
//            return  null;
//        }
//        for (Object object: args) {
//            if(object  instanceof PageRequest){
//                return (PageRequest)object;
//            }
//        }
//        return  null;
//    }
//
//    /**
//     *
//     * @param joinPoint
//     * @return false 走正常查询, true 走自定义查询
//     */
//    private boolean checkModelQuery(JoinPoint joinPoint){
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        AnnotatedType[]  annotatedTypes = method.getDeclaringClass().getAnnotatedInterfaces();
//        String returnType = method.getAnnotatedReturnType().getType().getTypeName().replace("org.springframework.data.domain.Page","")
//                .replace("<","").replace(">","");
//
//        if(  method.getAnnotation(ModelQuery.class)!=null&&method.getAnnotation(ModelQuery.class).modify()==true){
//            return  false;
//        }
//        if(  method.getAnnotation(ModelQuery.class)!=null||annotatedTypes==null||annotatedTypes.length<=0){
//            return  true;
//        }
//        if(ClassUtil.checkIsBasicsType(returnType)){
//            return false;
//        }
//        for (AnnotatedType  annotatedType : annotatedTypes ){
//            String name =  annotatedType.getType().getTypeName();
//            System.out.println(name);
//            if(name.indexOf(returnType)>=0){
//                return  false;
//
//            }
//        }
//        return  true;
//    }
//
//
//
//
//
//
//
//
//}