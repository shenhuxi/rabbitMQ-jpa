package com.zpself.jpa.utils;



import java.lang.annotation.*;

/**
 * 字段加密
 * @author shixh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Encryption {
    public static final String ENCRYPTION_A = "encryption_A";//A级加密
    String name() default ENCRYPTION_A;

 }
