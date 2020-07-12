package gitee.hongzihao.ejpa.util;

import cn.hutool.core.util.RandomUtil;
import gitee.hongzihao.ejpa.Jpa.JpaImpl;
import gitee.hongzihao.ejpa.annottation.GeneratedId;
import gitee.hongzihao.ejpa.module.pojo.GenerateIdPojo;
import gitee.hongzihao.ejpa.service.MysqlService;
import org.apache.commons.collections.list.SynchronizedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class GenerateIdUtil {

    public static  String TYPE_IDENTITY ="IDENTITY";

    public static  String TYPE_UUID ="UUID";

    @Autowired
    @Lazy
    private RedisTemplate redisTemplate;

    @Autowired
    @Lazy
    private MysqlService mysqlService;

    @Autowired
    @Lazy
    private JpaImpl jpaImpl;

    @Value("${ejpa.identity-size}")
    private  long size;

    //保存每个表的ID
    private static ConcurrentMap<String, Vector<Long>> idMap= new ConcurrentHashMap<>();

    //分配出去的ID,用户ID回收使用
    public static ConcurrentMap<String, Vector<GenerateIdPojo>> getIdMap= new ConcurrentHashMap();

    /**
     * 获取redis的IdList到JVM内存
     * @param tableClass
     * @throws Exception
     */
    private    Vector<Long>  addIdentityList(Class tableClass ) throws  Exception{
        String name = tableClass.getName();
        size=size==0?1:size;
        Vector<Long> returnList ;
        synchronized (name.intern()){
            returnList = idMap.get(name)==null?new Vector<>():idMap.get(name);
            if(returnList.size()>0){
                return returnList;
            }
            Table table = (Table) tableClass.getAnnotation(Table.class);
            String type=  getIdType(tableClass);
            if(type==null||!type.equals(TYPE_IDENTITY)){
                return returnList;
            }
            //如果没有设置表的起始值，主动去查询数据ID的最大值设置
            String id =  redisTemplate.opsForValue().get(name)+"";
            if(id.equals("null")){
                boolean  isLock =false;
                //重复初始化10次，每次失败暂停500毫秒。
                for (int i = 0; i < 10; i++) {
                    isLock = initId(table, name);
                    if (isLock) {
                        id =  redisTemplate.opsForValue().get(name)+"";
                        break;
                    }
                    Thread.sleep(500);
                }
                //如果初始化都失败报错
                if (!isLock) {
                    throw new Exception("请设置"+table.name()+"的Id起始值");
                }
            }
            //如果初始化都失败报错
            if(id.equals("null")){
                throw new Exception("请设置"+table.name()+"的Id起始值");
            }
            Long  ids = redisTemplate.opsForValue().increment(name,size);
            System.out.println("add id redis ："+ids+":"+(ids-size));
            for(long i = ids-size+1;ids>=i;i++){
                returnList.add(i);
            }
            idMap.put(name,returnList);
        }
        return returnList;
    }
    private String getIdType(Class tableClass) throws  Exception{
        List<Field>  fields =  ClassUtil.getAllField(tableClass);
        for (Field field: fields){
            GeneratedId generatedId = field.getAnnotation(GeneratedId.class);
            if(generatedId==null){
                continue;
            }
//            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
//            if(generatedValue==null||!generatedValue.strategy().equals(GenerationType.AUTO)){
//                throw  new Exception(tableClass.getName()+"@GeneratedValue必须是GenerationType.AUTO");
//            }
            if(generatedId.type().equals(TYPE_IDENTITY)&&!field.getType().getName().toUpperCase().endsWith("LONG")){
                throw  new Exception(tableClass.getName()+"自增类型必须是long类型");
            }
            if(generatedId.type().equals(TYPE_UUID)&&!field.getType().getName().toUpperCase().endsWith("STRING")){
                throw  new Exception(tableClass.getName()+"UUID类型必须是String类型");
            }
            return generatedId.type();
        }
        return null;
    }
    //获取ID
    public String getId(Class tableClass) throws  Exception{
        String type=  getIdType(tableClass);
        String name = tableClass.getName();
        if(type!=null&&type.equals(TYPE_UUID)){
            return UUID.randomUUID()+"-"+ RandomUtil.randomString(4);
        }
        if(type!=null&&type.equals(TYPE_IDENTITY)){
            synchronized (name.intern()){
                Vector<Long> returnList = idMap.get(name)==null?new Vector<>():idMap.get(name);
                if(returnList.size()>0){
                    return returnId(returnList,tableClass)+"";
                }
                returnList =  addIdentityList( tableClass );
                return   returnId(returnList,tableClass)+"";
            }
        }
        throw new Exception("获取ID失败");
    }


    private long returnId( Vector<Long> returnList,Class tableClass){
        long returnValue = returnList.get(0);
        returnList.remove(0);
        idMap.put(tableClass.getName(),returnList);
        Vector<GenerateIdPojo> getIds = getIdMap.get(tableClass.getName()) == null ? new Vector<GenerateIdPojo>(): getIdMap.get(tableClass.getName());
        GenerateIdPojo generateIdPojo =new GenerateIdPojo();
        generateIdPojo.setDate(new Date());
        generateIdPojo.setId(returnValue);
        getIds.add(generateIdPojo);
        getIdMap.put(tableClass.getName(),getIds);
        return returnValue;
    }


    /**
     * 初始化ID。
     * @param table 实体类的注解Table
     * @param name 实体类的getClass().getName();
     * @return
     */
    private boolean initId(Table table,String name){
        Boolean isLock  = false;
        String lock = table.name()+"lock";
        try {
            //添加分布式锁
//            isLock  = redisTemplate.opsForValue().setIfAbsent(lock,true,30, TimeUnit.SECONDS);

            isLock  = mysqlService.lock(lock,3,1,false);
                    System.out.println(isLock+"");
            if(!isLock){
                return false;
            }
            //如果还没设置ID
            String id =  redisTemplate.opsForValue().get(name)+"";
            if(id.equals("null")){
                String sql = "select Max(id) from "+table.name();
                long maxId = (long) jpaImpl.findSql(sql,Long.class,true,null);
                redisTemplate.opsForValue().increment(name,maxId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(isLock){
                mysqlService.unLock(lock);
            }
        }
        return true;
    }

}
