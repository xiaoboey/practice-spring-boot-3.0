package top.xiaoboey.practice.single.log;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author xiaoqb
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    @AliasFor("operation")
    String value() default "";

    @AliasFor("value")
    String operation() default "";

    /**
     * @return
     */
    boolean ignoreParams() default false;

    /**
     * @return
     */
    boolean ignoreResult() default true;
}
