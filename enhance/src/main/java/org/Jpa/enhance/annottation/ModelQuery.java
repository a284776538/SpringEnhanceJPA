package org.Jpa.enhance.annottation;

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

	/**
	 * sql ,jsql
	 * @return
	 */
	String value() default "";

	String retrunClass() default "";


	/**
	 * 分页可以自定义countQuery
	 * @return
	 */
	String countQuery() default "";

	/**
	 * 分页可以自定义count的字段默认是*
	 * @return
	 */
	String countColumn() default "*";


	/**
	 * false 为jsql,true sql
	 * @return
	 */
	boolean nativeQuery() default false;

}