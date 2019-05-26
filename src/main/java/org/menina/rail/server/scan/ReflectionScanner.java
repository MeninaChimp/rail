package org.menina.rail.server.scan;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author zhenghao
 * @date 2019/1/8
 */
@Slf4j
public class ReflectionScanner implements Scanner {

    private Reflections reflections;

    public ReflectionScanner(String basePackage) {
        Preconditions.checkNotNull(basePackage);
        log.debug("Current scan package path [{}]", basePackage);
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner()));
    }

    @Override
    public Set<Class<?>> findTypeByAnnotation(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }

    @Override
    public Set<Method> findMethodByAnnotation(Class<? extends Annotation> annotation) {
        return reflections.getMethodsAnnotatedWith(annotation);
    }

    @Override
    public Set<Method> getAllMethods(Class<?> clazz) {
        return ReflectionUtils.getAllMethods(clazz);
    }
}
