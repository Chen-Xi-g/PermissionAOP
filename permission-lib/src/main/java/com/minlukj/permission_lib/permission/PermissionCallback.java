package com.minlukj.permission_lib.permission;

/**
 * 权限申请的回调类
 */
public interface PermissionCallback {
  void onPermissionGranted();

  void shouldShowRational(String permission);

  void onPermissionReject(String permission);
}
