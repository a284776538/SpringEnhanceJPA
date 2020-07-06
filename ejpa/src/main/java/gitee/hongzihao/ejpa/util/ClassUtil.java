package gitee.hongzihao.ejpa.util;

import gitee.hongzihao.ejpa.annottation.GeneratedId;
import org.apache.commons.beanutils.ConvertUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {


    /**
     * 获取方法
     * @param executeObj
     * @param executeMethodName
     * @param parameterTypes
     * @return
     */
    public static  Method getMethodByObject(Object executeObj ,String executeMethodName, Class[] parameterTypes ){
        return getMethodByClass(executeObj.getClass(),executeMethodName,parameterTypes);
    }


    /**
     * 判断类型是不是基础类型
     * @param t char，float，double，String ，char
     * @return
     */
    public static  boolean checkIsBasicsType(Class t){
        if(checkIsBasicsType(t.getTypeName().toLowerCase())){
            return true;
        }
        return false;
    }
    /**
     * 判断类型是不是基础类型
     * @param t char，float，double，String ，char
     * @return
     */
    public static  boolean checkIsBasicsType(String t){
        if(t.startsWith("java.lang")||t.equals("long")||t.equals("int")||
                t.equals("float")||t.equals("double")||t.equals("char")
        ){
            return true;
        }
        return false;
    }

    /**
     * 获取方法
     * @param executeObj
     * @param executeMethodName
     * @param parameterTypes
     * @return
     */
    public static  Method getMethodByClass(Class executeObj ,String executeMethodName, Class[] parameterTypes ){
        Method method = null;
        try {
            method = executeObj.getMethod(executeMethodName, parameterTypes);
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            method = method == null ? executeObj.getDeclaredMethod(executeMethodName, parameterTypes): method;
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            method = method == null ? executeObj.getSuperclass().getDeclaredMethod(executeMethodName, parameterTypes): method;
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            method = method == null ? executeObj.getSuperclass().getMethod(executeMethodName, parameterTypes): method;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return method;
    }

    /**
     * 获取方法
     * @param executeObj
     * @param executeMethodName
     * @return
     */
    public static  Method getMethodByClass(Class executeObj ,String executeMethodName ){
        return 	getMethodByClass(executeObj,executeMethodName,null);
    }


    /**
     * 获取方法
     * @param executeObj
     * @param executeMethodName
     * @return
     */
    public static  Method getMethodByMethodName(Class executeObj ,String executeMethodName ){
        Method[]  method = executeObj.getMethods();
        for (Method method2 : method) {
            String methodName = method2.getName();
            if(methodName.equals(executeMethodName)){
                return method2;
            }

        }
        method = executeObj.getDeclaredMethods();
        for (Method method2 : method) {
            String methodName = method2.getName();
            if(methodName.equals(executeMethodName)){
                return method2;
            }
        }

        method = executeObj.getSuperclass().getDeclaredMethods();
        for (Method method2 : method) {
            String methodName = method2.getName();
            if(methodName.equals(executeMethodName)){
                return method2;
            }
        }

        method = executeObj.getSuperclass().getMethods();
        for (Method method2 : method) {
            String methodName = method2.getName();
            if(methodName.equals(executeMethodName)){
                return method2;
            }
        }
        return null;
    }




    /**
     * 获取一个参数方法
     * @param executeObj
     * @param executeMethodName
     * @param parameterTypes
     * @return
     */
    public static  Method getMethodByObject(Object executeObj ,String executeMethodName, Class parameterTypes ){
        Class []a = new Class[]{parameterTypes};
        return getMethodByObject(executeObj,executeMethodName,a);
    }

    /**
     * 获取没有参数的方法
     * @param executeObj
     * @param executeMethodName
     * @return
     */
    public static  Method getMethodByObject(Object executeObj ,String executeMethodName){
        return getMethodByClass(executeObj.getClass(),executeMethodName,null);
    }





    /**
     * 获取字段
     * @param executeObj
     * @param fieldName
     * @return
     */
    public static Field getField(Object executeObj, String fieldName) {
        Field field = null;
        try {
            field = executeObj.getClass().getDeclaredField(fieldName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            field = executeObj.getClass().getSuperclass().getDeclaredField(fieldName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return field;
    }

    /**
     * 根据注解获取字段
     * @param c 检查的类
     * @param annotation 过滤的注解
     * @return
     */
    public static List<Field> getAllFieldByAnnotation(Class c, Class annotation) {
        List<Field> fields = getAllField( c);
        List<Field> returnFields = new ArrayList<>();
        for (Field field: fields){
            Annotation[]  annotations = field.getAnnotations();
            if(annotations==null||annotations.length<=0){
                continue;
            }
            for(Annotation anno:annotations){
                if(anno.annotationType().equals(annotation) ){
                    returnFields.add(field);
                    break;
                }
            }
        }
        return returnFields;
    }

    /**
     * 获取所有字段
     * @param c
     * @return
     */
    public static List<Field> getAllField(Class c) {
        List<Field> returnField = new ArrayList<>();
        try {
            Field field [] = c.getDeclaredFields();
            if(field!=null&&field.length>0){
                for (Field field1 : field) {
                    returnField.add(field1);

                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            Field field []  =c.getSuperclass().getDeclaredFields();
            if(field!=null&&field.length>0){
                for (Field field1 : field) {
                    returnField.add(field1);

                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return returnField;
    }

    /**
     * 获取字段的值
     * @param executeObj
     * @param field
     * @return
     */
    public static Object getFieldValue(Object executeObj, Field field) {
        Object value = null;
        try {
            field.setAccessible(true);
            value=  field.get(executeObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取字段的值
     * @param executeObj
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object executeObj, String fieldName) {
        Object obj =null;
        try {
            Field field = getField(executeObj,fieldName);
            field.setAccessible(true);
            obj = field.get(executeObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
        return obj;
    }
    /**
     * 获取字段的值
     * @param executeObj
     * @param fieldName
     * @return
     */
    public static void setValueByField(Object executeObj,  String fieldName,Object... args ) throws  Exception {
        Method[] methods = executeObj.getClass().getMethods();
        String setMethod = "set"+fieldName;
        if(methods!=null&&methods.length<0){
            throw  new Exception(executeObj.getClass().getName()+"找不到"+fieldName+"字段的set方法");
        }
        for(Method method:methods){
            if(method.getName().toUpperCase().equals(setMethod.toUpperCase())){
                try {
                    method.invoke(executeObj,args);
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        throw  new Exception(executeObj.getClass().getName()+"找不到"+fieldName+"字段的set方法");
    }

}