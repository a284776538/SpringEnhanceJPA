package gitee.hongzihao.ejpa.annottation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface GeneratedId {

    /**
     * 设置ID的类型，IDENTITY （自增），UUID(UUID+随机）
     * @return
     */
    String type() default "UUID";

}
