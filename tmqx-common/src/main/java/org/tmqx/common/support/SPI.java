package org.tmqx.common.support;

import java.lang.annotation.*;

/**
 * SPI装载器注解
 *
 * @author tf
 * @version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SPI {

    // Default SPI name
    String value() default "";
}
