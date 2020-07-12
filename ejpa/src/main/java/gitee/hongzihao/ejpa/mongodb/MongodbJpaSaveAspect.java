//package gitee.hongzihao.ejpa.mongodb;
//
//import gitee.hongzihao.ejpa.annottation.GeneratedId;
//import gitee.hongzihao.ejpa.run.EjpaRun;
//import gitee.hongzihao.ejpa.util.ClassUtil;
//import gitee.hongzihao.ejpa.util.EjpUtil;
//import gitee.hongzihao.ejpa.util.GenerateIdUtil;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//
//import javax.persistence.Table;
//import java.lang.reflect.Field;
//import java.util.List;
//
///**
// * JPA拦截器
// */
//@Aspect
//public class MongodbJpaSaveAspect {
//
//    @Autowired
//    @Lazy
//    private GenerateIdUtil generateIdUtil;
//
//    @Autowired
//    @Lazy
//    private EjpaRun ejpaRun ;
//    //拦截方法上有@Query
//    @Around("execution(* save*(..))")
//    public Object doAround(ProceedingJoinPoint point) throws Throwable {
//        Object o = null;
//        ejpaRun.run();
//        boolean isExecution = checkIsExecution(point);
//        if(isExecution){
//            setIdToArgs(point.getArgs()[0]);
//        }
//        o = point.proceed(point.getArgs());
//        return o;
//    }
//    private void  setIdToArgs(Object obj) throws  Exception{
//        if (obj instanceof Iterable) {
//            Iterable arrayParam = (Iterable) obj;
//            for(Object param:arrayParam){
//                List<Field> fields =  ClassUtil.getAllFieldByAnnotation(param.getClass(), GeneratedId.class );
//                for (Field field: fields){
//                    if(ClassUtil.getFieldValue(param,field)==null||EjpUtil.getIntFromString(ClassUtil.getFieldValue(param,field).toString())>0){
//                        continue;
//                    }
//                    String id = generateIdUtil.getId(param.getClass());
//                    if(EjpUtil.checkIsNumber(id)){
//                        ClassUtil.setValueByField(param,field.getName(),Long.parseLong(id));
//                    }else{
//                        ClassUtil.setValueByField(param,field.getName(),id);
//                    }
//                }
//            }
//        }else{
//            List<Field> fields =  ClassUtil.getAllFieldByAnnotation(obj.getClass(), GeneratedId.class );
//            for (Field field: fields){
//                if(ClassUtil.getFieldValue(obj,field)==null||EjpUtil.getIntFromString(ClassUtil.getFieldValue(obj,field).toString())>0){
//                    continue;
//                }
//                String id = generateIdUtil.getId(obj.getClass());
//                if(EjpUtil.checkIsNumber(id)){
//                    ClassUtil.setValueByField(obj,field.getName(),Long.parseLong(id));
//                }else{
//                    ClassUtil.setValueByField(obj,field.getName(),id);
//                }
//
//            }
//        }
//    }
//
//
//
//
//    private boolean checkIsExecution(ProceedingJoinPoint point){
//        String name = point.getSignature().toString().toUpperCase();
//        if(name.indexOf("MONGOREPOSITORY.SAVE")<0){
//            return false;
//        }
//        Object[]  objs=  point.getArgs();
//        if(objs==null||objs.length!=1){
//            return  false;
//        }
//        Object obj = objs[0];
//        if (obj instanceof Iterable) {
//            Iterable arrayParam = (Iterable) obj;
//            //如果是空数组就报错
//            if (!arrayParam.iterator().hasNext()) {
//                return false;
//            }
//            for(Object param:arrayParam){
//                Document table = obj.getClass().getAnnotation(Document.class);
//                if(table==null||table.collection()==null){
//                    return false;
//                }
//                List<Field> fields =  ClassUtil.getAllFieldByAnnotation(param.getClass(), GeneratedId.class );
//                if(fields==null||fields.size()<=0){
//                    return false;
//                }
//            }
//        }else{
//            Document table = obj.getClass().getAnnotation(Document.class);
//            if(table==null||table.collection()==null){
//                return false;
//            }
//            List<Field> fields =  ClassUtil.getAllFieldByAnnotation(obj.getClass(), GeneratedId.class );
//            if(fields==null||fields.size()<=0){
//                return false;
//            }
//        }
//        return  true;
//    }
//
//
//}