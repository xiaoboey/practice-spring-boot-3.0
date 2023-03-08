package top.xiaoboey.practice.spring.boot3.simplestarter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.AuthAndLogFilter;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.CaffeineConfiguration;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.SecurityConfig;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.SimpleConfiguration;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleLogService;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoqb
 */
public class SimpleImportSelector implements ImportSelector, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        importingClassMetadata.getAnnotationTypes().forEach(System.out::println);

        System.out.println(beanFactory);

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSimpleStarter.class.getName())
        );

        Object object = annotationAttributes.get("value");
        boolean isTrue = object == null ? false : Boolean.parseBoolean(object.toString());
        if (isTrue) {
            return new String[]{
                    CaffeineConfiguration.class.getName(),
                    SimpleConfiguration.class.getName(),
                    AuthAndLogFilter.class.getName(),
                    SecurityConfig.class.getName(),
                    SimpleLogService.class.getName()
            };
        }

        return new String[0];
    }
}
