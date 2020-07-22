package com.isunland.permission_lib.permission;

import android.app.Activity;
import android.app.ListFragment;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 切面处理类
 */

@Aspect public class PermissionAspect {

  /**
   * 定义一个切面方法
   */
  @Around("execution(@com.isunland.permission_lib.permission.Permission * *(..))") public void aroundJoinPoint(
      final ProceedingJoinPoint joinPoint) {
    try {
      // 获取方法注解
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Method method = methodSignature.getMethod();
      Permission annotation = method.getAnnotation(Permission.class);
      // 获取注解参数，这里我们有3个参数需要获取
      final String[] permissions = annotation.permissions();
      final int[] rationales = annotation.rationales();
      final int[] rejects = annotation.rejects();
      final List<String> permissionList = Arrays.asList(permissions);

      // 获取上下文
      Object object = joinPoint.getThis();
      Context context = null;
      if (object instanceof FragmentActivity) {
        context = (FragmentActivity) object;
      } else if (object instanceof Fragment) {
        context = ((Fragment) object).getContext();
      } else if (object instanceof Service) {
        context = (Service) object;
      } else if (object instanceof ListFragment) {
        context = ((ListFragment) object).getActivity();
      } else if (object instanceof Activity) {
        context = (Activity) object;
      } else if (object instanceof View) {
        View view = (View) object;
        context = view.getContext();
      }

      // 申请权限
      PermissionUtil.with(context).permission(permissions).callback(new PermissionCallback() {
        @Override public void onPermissionGranted() {
          try {
            // 权限申请通过，执行原方法
            joinPoint.proceed();
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        }

        @Override public void shouldShowRational(String permission) {
          // 申请被拒绝，但没有勾选“不再提醒”，这里我们让外部自行处理
          int index = permissionList.indexOf(permission);
          int rationale = -1;
          if (rationales.length > index) {
            rationale = rationales[index];
          }
          PermissionUtil.getGlobalConfigCallback().shouldShowRational(permission, rationale);
        }

        @Override public void onPermissionReject(String permission) {
          // 申请被拒绝，且勾选“不再提醒”，这里我们让外部自行处理
          int index = permissionList.indexOf(permission);
          int reject = -1;
          if (rejects.length > index) {
            reject = rejects[index];
          }
          PermissionUtil.getGlobalConfigCallback().onPermissionReject(permission, reject);
        }
      }).request();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
