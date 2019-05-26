package org.menina.rail.common.annotation;

import org.menina.rail.common.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhenghao
 * @date 2019/1/13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActiveFilter {

    Scope[] scope();
}
