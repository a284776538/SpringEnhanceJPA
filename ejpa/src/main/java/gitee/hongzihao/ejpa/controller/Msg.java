package gitee.hongzihao.ejpa.controller;

import java.io.Serializable;

/**
 * 消息返回类
 * 
 * @author Cay
 * 
 */
public class Msg implements Serializable {

	public static final String SUCCESS_CODE = "200";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3270665804855015222L;
	private String code;// 编号
	private String message;// 消息
	private Object data;// 返回值

	/**
	 * 消息构造方法
	 * 
	 * @param code
	 *            传入编号
	 * @param message
	 *            传入消息
	 * @param data
	 *            返回值
	 */
	public Msg(String code, String message, Object data) {
		this.code = code;
		this.message = message;
		if(null != data){
			this.data = data;
		}

	}

	/**
	 * 操作成功返回的信息
	 * @author Cay
	 * @data 2016年12月28日
	 * @return
	 */
	public static Msg MsgSuccess(String code, String message) {

		return new Msg(code, message, "{}");
	}

	/**
	 * 操作成功返回的信息
	 * @author Cay
	 * @data 2016年12月28日
	 * @return
	 */
	public static Msg MsgSuccess(String code, String message,Object data) {

		return new Msg(code, message, data);
	}



	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Msg [code=" + code + ", message=" + message + ", data=" + data
				+ "]";
	}
}
