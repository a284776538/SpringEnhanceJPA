package gitee.hongzihao.ejpa.annottation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by charlie on 2018/12/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelQuery {







//（注：使用方法的return可以自定义平拼接sql）
//（注：使用long，Long等基本类型不需要指定retrunClass见下图方法2）


	/**
	 * value = 查询语句，或者方法return的值2选一也是查询语句（如2个都写value 优先与return） 见3，4个方法
	 * @return
	 */
	String value() default "";

	/**
	 * retrunClass = 添加返回值的类型，见下图findByVoteActivitieIdAndIsDeleteIsNull 第一个方法
	 * @return
	 */
	String retrunClass() default "";


	/**
	 * countQuery = 自己写count的sql与 countColumn 2选一，countQuery 优先与countColumn
	 * @return
	 */
	String countQuery() default "";

	/**
	 * 	countColumn = 分页指定count的字段，默认是*号，如sql select id from xxx 默认会自动使用 id。只用使用Page返回值才会执行count
	 * @return
	 */
	String countColumn() default "*";


	/**
	 * nativeQuery =是否是sql 或者 是jsql false 为jsql,true sql，默认false
	 * @return
	 */
	boolean nativeQuery() default false;


	/**
	 * 是否修改
	 * @return
	 */
	boolean modify() default false;


	Class<?> test() default Object.class;

}