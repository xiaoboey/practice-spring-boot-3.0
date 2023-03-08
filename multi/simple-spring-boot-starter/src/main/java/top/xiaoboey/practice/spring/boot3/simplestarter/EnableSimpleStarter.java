package top.xiaoboey.practice.spring.boot3.simplestarter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xiaoqb
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SimpleImportSelector.class})
public @interface EnableSimpleStarter {
    boolean value() default true;
}
