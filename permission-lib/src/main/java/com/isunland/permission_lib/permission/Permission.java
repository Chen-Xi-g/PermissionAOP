package com.isunland.permission_lib.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface Permission {
  //权限
  String[] permissions();

  //需要获取的权限提示信息
  int[] rationales() default {};

  //需要获取的权限转到设置提示信息
  int[] rejects() default {};
}
