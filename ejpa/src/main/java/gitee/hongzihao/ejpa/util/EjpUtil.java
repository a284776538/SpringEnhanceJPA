package gitee.hongzihao.ejpa.util;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;


public class EjpUtil {

    public static String system;

    //替换/的关键字
    public static String xg = "xg132gxnpp";
    //项目域名
    public static String domain = "localhost:8084/";






    /**
     * 判断2个字符串是否相等，会trim，
     *
     * @param p1
     * @param p2
     * @param matchCase 是否区分大小写
     * @return
     */
    public static boolean checkIsStringEquals(String p1, String p2, boolean matchCase) {
        p1 = p1.trim();
        p2 = p2.trim();
        if (matchCase) {
            p1 = p1.toLowerCase();
            p2 = p2.toLowerCase();
        }
        if (p1.equals(p2)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是不是空
     *
     * @param string
     * @return true 空 ，false 不为空
     */
    public static boolean checkStringIsEmpty(Object string) {
        try {
            if (string.toString().replace(" ", "").trim().length() > 0) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return true;
    }


    /**
     * 判断数组是不是或者null
     *
     * @param iterable2 数组
     * @return true 空 ，false 不为空
     */
    public static boolean checkArrayIsEmpty(Object iterable2) {
        try {
            int i = 0;
            Iterable iterable = null;
            if (Iterable.class.isInstance(iterable2)) {
                iterable = (Iterable) iterable2;
                for (Object object : iterable) {
                    i++;
                    break;
                }
            }
            if (iterable2.getClass().getName().startsWith("[L")) {
                Object[] array = (Object[]) iterable2;
                for (Object object : array) {
                    i++;
                    break;
                }
            }
            if (i > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return true;
    }


    /**
     * 对象克隆
     *
     * @param object
     * @param classobject
     * @return
     */
    public static Object copyObject(Object object, Class classobject) {
        Object o = null;
        JSONObject jsonObject = JSONUtil.parseObj(object);
        o = JSONUtil.toBean(jsonObject, classobject);
        return o;


    }


    /**
     * 获取字符串中的数字
     *
     * @param string
     * @return
     */
    public static String getNumberFromString(String string) {
        String returnString = "";
        if (checkStringIsEmpty(string)) {
            return null;
        }
        for (int i = 0; i < string.length(); i++) {
            int ii = i + 1;
            if (ii >= string.length()) {
                ii = string.length();
            }
            String a = string.substring(i, i + 1);
            try {
                Long.parseLong(a);
            } catch (Exception e) {
                continue;
            }
            returnString = returnString + a;
        }
        return returnString;

    }


    /**
     *  把字符串中的数字变成int
     *
     * @param string
     * @return
     */
    public static int getIntFromString(String string) {
        return checkStringIsEmpty(getNumberFromString(string))?0:Integer.parseInt(getNumberFromString(string));

    }


    /**
     * 获取字符串中的非数字字符
     *
     * @param string
     * @return
     */
    public static String getNotNumberFromString(String string) {
        String returnString = "";
        if (checkStringIsEmpty(string)) {
            return null;
        }
        for (int i = 0; i < string.length(); i++) {
            int ii = i + 1;
            if (ii >= string.length()) {
                ii = string.length();
            }
            String a = string.substring(i, i + 1);
            try {
                long num = Long.parseLong(a);
                continue;
            } catch (Exception e) {

            }
            returnString = returnString + a;
        }
        return returnString;

    }


    /**
     * 检查是不是数字
     *
     * @param string
     * @return
     */
    public static boolean checkIsNumber(String string) {
        try {
            Double.parseDouble(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }




    public static boolean checkIsJSONorJSONarray(String string) {
        boolean istrue = false;
        try {
            JSONObject jsonobject = JSONUtil.parseObj(string);
            jsonobject.get("id");
            istrue = true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (!istrue) {
            try {
                JSONArray jsonobject = JSONUtil.parseArray(string);
                jsonobject.size();
                istrue = true;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return istrue;

    }

    /**
     * 描述:获取 post 请求内容
     *
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }


    /**
     * 读取 request body 内容作为字符串
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String readRequest(HttpServletRequest request) throws IOException {

        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String str;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        inputStream.close();
        return sb.toString();
    }




    /**
     * 保留几位数
     * @param price
     * @param scale
     * @param roundingMode
     * @return
     * @throws IOException
     */
    public  static BigDecimal getBigDecimal(Object price ,int scale, int roundingMode) {
        BigDecimal dailyVisitsRatio = new BigDecimal(price.toString()).divide(new BigDecimal("1"),scale,roundingMode);
        return dailyVisitsRatio;
    }

    /**
     * 获取数据流
     * @param request
     * @return
     * @throws IOException
     */
    public  static  byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 把字符串中的空字符串去掉
     * @return
     */
    public static String changeJson(String json){
        boolean istrue = checkIsJSONorJSONarray(json);
        if(istrue){
//			json.replace("\":\"\"", newChar)
        }
        return json;

    }

    //计算时间和现在时间的秒差
    public static int calLastedTime(Date endDate) {
        long a = System.currentTimeMillis();
        long b = endDate.getTime();
        int c = (int)((a - b) / 1000);
        return c;
    }

    //计算两时间和现在时间的秒差
    public static int calStartTimeAndLastedTime(Date startDate,Date endDate) {
        long a = endDate.getTime();
        long b = startDate.getTime();
        int c = (int)((a - b) / 1000);
        return c;
    }







    /**
     * 根据对象获取List
     * @param object
     * @return
     */
    public  static   List<Object> getListByObject(Object object){
       List<Object> list =  new ArrayList<>();
       if(null ==object){
           return list;
       }

        if(Iterable.class.isInstance(object)){
            Iterable array = (Iterable) object;
            for (Object o :array) {
                list.add(o);
            }
        }

        if( object.getClass().getName().startsWith("[L")){
            Object[] array = (Object[]) object;
            for (Object o :array) {
                list.add(o);
            }
        }
        if(checkArrayIsEmpty(list)){
            list.add(object);
        }

        return list;
    }


    /**
     * 判断2个对象是否相等
     * @param one
     * @param second
     * @return
     */
    public static boolean matchObject(Object one, Object second) {
        String oneString = JSONUtil.toJsonStr(one);
        String secondString = JSONUtil.toJsonStr(second);
        List<String> oneNumber = StringToArray(getNumberFromString(oneString));
        oneNumber.sort((String a, String b) -> a.compareTo(b));
        List<String> secondNumber = StringToArray(getNumberFromString(secondString));
        secondNumber.sort((String a, String b) -> a.compareTo(b));
        if (!oneNumber.toString().equals(secondNumber.toString())) {
            return false;
        }
        oneNumber = StringToArray(getNotNumberFromString(oneString));
        oneNumber.sort((String a, String b) -> a.compareTo(b));
        secondNumber = StringToArray(getNotNumberFromString(secondString));
        secondNumber.sort((String a, String b) -> a.compareTo(b));
        if (!oneNumber.toString().equals(secondNumber.toString())) {
            return false;
        }
        return true;
    }

    /**
     * 字符串转成数组
     * @param numStr
     * @return
     */
    public static List<String> StringToArray(String numStr) {
        List<String> returnList = new ArrayList<>();
        String returnString = "";
        if(checkStringIsEmpty(numStr)){
            return null;
        }
        for (int i=0;i<numStr.length();i++) {
            int ii = i+1;
            if(ii>=numStr.length()){
                ii=numStr.length();
            }
            String a = numStr.substring(i,i+1);
            returnList.add(a);
        }
        return returnList;
    }



    private static void check(Object o){
        System.out.println("\uD83C\uDC04️Ed\uD83C\uDC04️");
    }





    public static void main(String[] args) throws Exception {





    }
}