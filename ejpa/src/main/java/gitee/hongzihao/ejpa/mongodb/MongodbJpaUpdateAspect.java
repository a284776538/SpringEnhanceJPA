//package gitee.hongzihao.ejpa.mongodb;
//
//import cn.hutool.core.date.DateUtil;
//import gitee.hongzihao.ejpa.run.EjpaRun;
//import gitee.hongzihao.ejpa.util.ClassUtil;
//import gitee.hongzihao.ejpa.util.GenerateIdUtil;
//import org.apache.commons.beanutils.ConvertUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.data.annotation.Transient;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Id;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Table;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * JPA拦截器
// */
//@Aspect
//public class  MongodbJpaUpdateAspect {
//
//    @Autowired
//    @Lazy
//    private GenerateIdUtil generateIdUtil;
//    @PersistenceContext
//    @Lazy
//    protected EntityManager manager;
//    @Autowired
//    @Lazy
//    private EjpaRun ejpaRun ;
//    //拦截方法上有@Query
//    @Around("execution(* *..EjpaRepository.update*(..))")
//    @Transactional(rollbackFor = Exception.class)
//    public Object doAround(ProceedingJoinPoint point) throws Throwable {
//        ejpaRun.run();
//        boolean isExecution = checkIsExecution(point);
//        if(isExecution){
//           return getUpdateSql(point.getArgs()[0]);
//        }
//       throw  new Exception("找不到表名或者ID");
//    }
//
//    private   List<Field> getTransientFields(Object obj){
//        List<Field> returnFields = new ArrayList<>();
//        returnFields.addAll(ClassUtil.getAllFieldByAnnotation(obj.getClass(), Transient.class ));
//        returnFields.addAll(ClassUtil.getAllFieldByAnnotation(obj.getClass(), javax.persistence.Transient.class ));
//        return  returnFields;
//    }
//
//    private  int  executeSql(Object obj) throws  Exception{
//        String tableName =obj.getClass().getName().substring(obj.getClass().getName().lastIndexOf(".")+1);
//        String sql ="update " +tableName+"  ";
//        List<Field> fields =  getTransientFields(obj);
//        List<Field> idFields =  ClassUtil.getAllFieldByAnnotation(obj.getClass(), Id.class );
//        int checkUpdate = 0;
//        for (Field field: ClassUtil.getAllField(obj.getClass())){
//            Object value = ClassUtil.getFieldValue(obj,field);
//            if(fields.contains(field)||value==null||idFields.contains(field)){
//                continue;
//            }
//            Object sqlvalue =getSQLParam(value).toString();
//            String setSQL = checkUpdate==0?" set "+field.getName()+" = "+ sqlvalue:" , "+field.getName()+" = "+ sqlvalue;
//            sql =sql+setSQL;
//            checkUpdate++;
//        }
//        //如果没有一个字段需要修改放回0
//        if(checkUpdate==0){
//            return  0;
//        }
//        for (int i=0; i< idFields.size();i++){
//            if(ClassUtil.getFieldValue(obj,idFields.get(i))==null){
//               throw  new Exception( "update 的对象ID 不能为 null");
//            }
//            String id =  getSQLParam( ClassUtil.getFieldValue(obj,idFields.get(i))).toString();
//            sql =i==0?sql+" where "+idFields.get(i).getName()+" = "+ id :
//                    sql+" and "+idFields.get(i).getName() +" = "+ id;
//        }
//        return  manager.createQuery(sql).executeUpdate();
//    }
//    private int  getUpdateSql(Object obj) throws  Exception{
//        int i =0;
//        if (obj instanceof Iterable) {
//            Iterable arrayParam = (Iterable) obj;
//            for(Object param:arrayParam){
//                i=i+ executeSql(param);
//            }
//        }else{
//            i=i+ executeSql(obj);
//        }
//        return i;
//    }
//
//
//    private Object getSQLParam(Object object){
//        if(object instanceof Date){
//            object =  DateUtil.format((Date) object,"yyyy-MM-dd HH:mm:ss.sss");
//        }
//        if(object instanceof  String){
//            object = " '"+object.toString()+"' ";
//        }else{
//            object =  ConvertUtils.convert(object, String.class) ;
//        }
//        return  object;
//    }
//
//    private boolean checkIsExecution(ProceedingJoinPoint point){
//        String name = point.getSignature().toString().toUpperCase();
//        if(name.indexOf("MONGOREPOSITORY.UPDATE")<0){
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
//                Document table = param.getClass().getAnnotation(Document.class);
//                if(table==null||table.collection()==null){
//                    return false;
//                }
//                List<Field> fields =  ClassUtil.getAllFieldByAnnotation(param.getClass(), Id.class );
//                if(fields==null||fields.size()<=0){
//                    return false;
//                }
//            }
//        }else{
//            Document table = obj.getClass().getAnnotation(Document.class);
//            if(table==null||table.collection()==null){
//                return false;
//            }
//            List<Field> fields =  ClassUtil.getAllFieldByAnnotation(obj.getClass(), Id.class );
//            if(fields==null||fields.size()<=0){
//                return false;
//            }
//        }
//        return  true;
//    }
//
//
//}