package gitee.hongzihao.ejpa.Jpa;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.Jpa.enhance.util.ClassUtil;
import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

//@Service
//@Lazy
public class JpaImpl<T>  {


    @PersistenceContext
    @Lazy
    protected EntityManager manager;



    /**
     * 查询单个对象
     * @param sql
     * @param t
     * @param isNativeQuery
     * @return
     * @throws Exception
     */
    public T findSql(String sql, Class t,boolean isNativeQuery) throws Exception {
        List  returnList  = findBySql(sql, t,isNativeQuery);
        if(returnList==null||returnList.size()<=0){
            return null;
        }
        if(returnList.size()>1){
            throw  new Exception("查询数据多于一条");
        }
        if(ClassUtil.checkIsBasicsType(t)){
            return (T) ConvertUtils.convert(returnList.get(0), t);
        }


        return (T)returnList.get(0);
    }

    /**
     * 查询分页
     * @param sql
     * @param countSql
     * @param c
     * @param isNativeQuery
     * @param pageRequest
     * @return
     * @throws Exception
     */
    public Page<?> findBySql(String sql, String countSql, Class c, boolean isNativeQuery, PageRequest pageRequest) throws Exception {
        // 第几页
        int cpage = pageRequest.getPageNumber();
        // 每页显示多少行
        int size = pageRequest.getPageSize();
        if(size<=0){
            size =20;
        }
        if(cpage<0){
            cpage =0;
        }
        Sort sort = pageRequest.getSort();
        if(sort!=null&&!"UNSORTED".equals(sort.toString())){
            String order =" order by "+sort.toString().replace(":"," ");
            sql=  sql+order;
        }
        sql=  sql.replace("?#{#pageable}","");
        countSql =countSql.replace("?#{#pageable}","");
        String limit = " limit "+(cpage*size) +","+size;
        sql = sql+limit;
        Map<String,Object> data2 = (Map) findSql( countSql,  HashMap.class, isNativeQuery);
        Set<String> key = data2.keySet();
        int count = Integer.parseInt(data2.get(key.iterator().next()).toString());
        //总数如果=0就不执行下面数据的查询了
        if(count<=0){
            return new PageImpl<>(new ArrayList<>(), pageRequest,count);
        }
        List<?> data =findBySql( sql,  c, isNativeQuery);
        return new PageImpl<>(data, pageRequest,count);
    }

    /**
     * 查询数组
     * @param sql
     * @param c
     * @param isNativeQuery
     * @return
     * @throws Exception
     */
    public List<?> findBySql(String sql, Class c,boolean isNativeQuery ) throws Exception {
        Query query=null;
        if(!isNativeQuery){
            String limit = null;
            if(sql.indexOf("limit")>=0){
                limit = sql.substring(sql.lastIndexOf("limit"));
                sql =sql.substring(0,sql.lastIndexOf("limit"));
            }
            query = manager.createQuery(sql);
            if(limit!=null){
                String limitArray[]=limit.replace("limit","").replace(" ","").split(",");
                int firstResult=Integer.parseInt(limitArray[0]);
                int maxResults=Integer.parseInt(limitArray[1]);
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
            }
            query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        }else{
            query = manager.createNativeQuery(sql);
            query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        }

        List<Map<String,Object>> queryObject = query.getResultList();
        List returnObject = new ArrayList();
        if(queryObject==null||queryObject.size()<=0){
            return returnObject;
        }
        for (Map<String,Object> map:queryObject) {
            if(c.getTypeName().equals("java.util.HashMap")){
                returnObject.add(map);
                continue;
            }

            if(ClassUtil.checkIsBasicsType(c)){
                Object o = map.get(map.keySet().iterator().next());
                returnObject.add(o );
                return returnObject;
            }
            returnObject.add(getObjectByClass(map,c));
        }
        return (List<?>)returnObject;
    }

    private  Object getObjectByClass(Map<String,Object> map,Class c){
        JSONObject object = new JSONObject();
        List<Field> fields = ClassUtil.getAllField(c);
        Set<String> keys =  map.keySet();
        for (String key:keys) {
            Object value = map.get(key);
            if(value==null){
                continue;
            }
            String checkKey =key.replace("_","").toUpperCase();

            List<Field> checkField   = fields.stream().filter(f->f.getName().toUpperCase().equals(checkKey)).collect(Collectors.toList());
            if(checkField==null||checkField.size()<=0){
                continue;
            }
            Field field = checkField.get(0);
            if(key.indexOf("_")>=0){
                key = field.getName();
            }
            String dateType = "java.util.Date";
            if( field.getType().toString().endsWith(dateType)){
                Timestamp time = (Timestamp) value;
                object.put(key,new Date(time.getTime()));
                continue;
            }
            object.put(key, ConvertUtils.convert(value, field.getType()));
        }
        return JSONUtil.toBean(object,c);
    }
}
