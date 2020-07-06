package gitee.hongzihao.ejpa.module.pojo;

public class QueryData {

    /**
     * 查询放回的值
     */
    private Object returnData;


    /**
     * 查询的sql
     */
    private String  sql;

    public Object getReturnData() {
        return returnData;
    }

    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
