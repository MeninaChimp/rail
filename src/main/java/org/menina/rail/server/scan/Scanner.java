package org.menina.rail.server.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author zhenghao
 * @Data 2019/1/8
 */
public interface Scanner {

    Set<Class<?>> findTypeByAnnotation(Class<? extends Annotation> annotation);

    Set<Method> findMethodByAnnotation(Class<? extends Annotation> annotation);

    Set<Method> getAllMethods(Class<?> clazz);
}
