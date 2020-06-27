package org.Jpa.enhance.util;

import org.apache.commons.beanutils.ConvertUtils;

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
        if(checkIsBasicsType(t.getTypeName())){
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
            field.get(executeObj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
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
}