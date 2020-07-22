package com.isunland.permission_lib.single;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 处理双击间隔，防止连续点击
 * 如果需要自定义点击时间间隔，自行传入毫秒值即可
 *
 * [       // @SingleClick(2000)
 * [        @SingleClick
 * [        @Override
 * public void onClick(View v) {
 * // do something
 * }
 */

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface SingleClick {
  long value() default 1000;
}
