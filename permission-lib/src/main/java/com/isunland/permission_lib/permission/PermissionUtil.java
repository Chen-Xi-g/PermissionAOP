package com.isunland.permission_lib.permission;

import android.content.Context;

public class PermissionUtil {
  public static final int READ_EXTERNAL_STORAGE_RATIONALE = 100;//允许应用程序从外部存储读取被拒绝
  public static final int READ_EXTERNAL_STORAGE_REJECT = 1000;//允许应用程序从外部存储读取勾选不再提示被拒绝提

  public static final int WRITE_EXTERNAL_STORAGE_RATIONALE = 101;//允许应用程序写入外部存储被拒绝
  public static final int WRITE_EXTERNAL_STORAGE_REJECT = 1010;//允许应用程序写入外部存储勾选不再提示被拒绝提

  public static final int CAMERA_RATIONALE = 102;//访问摄像头设备被拒绝
  public static final int CAMERA_REJECT = 1020;//访问摄像头设备勾选不再提示被拒绝提

  public static final int READ_PHONE_STATE_RATIONALE = 103;//允许以只读方式访问电话状态，包括当前的蜂窝网络信息，所有正在进行的呼叫的状态以及PhoneAccount在设备上注册的所有电话的列表被拒绝。
  public static final int READ_PHONE_STATE_REJECT = 1030;//允许以只读方式访问电话状态，包括当前的蜂窝网络信息，所有正在进行的呼叫的状态以及PhoneAccount在设备上注册的所有电话的列表勾选不再提示被拒绝提

  public static final int CALL_PHONE_RATIONALE = 104;//允许应用程序在不通过Dialer用户界面的情况下发起电话呼叫，以使用户确认呼叫被拒绝。
  public static final int CALL_PHONE_REJECT = 1040;//允许应用程序在不通过Dialer用户界面的情况下发起电话呼叫，以使用户确认呼叫勾选不再提示被拒绝提

  private static PermissionGlobalConfigCallback globalConfigCallback;
  private PermissionCallback callback;
  private String[] permissions;
  private Context context;

  private PermissionUtil(Context context) {
    this.context = context;
  }

  public static void init(PermissionGlobalConfigCallback callback) {
    globalConfigCallback = callback;
  }

  static PermissionGlobalConfigCallback getGlobalConfigCallback() {
    return globalConfigCallback;
  }

  //初始化
  public static PermissionUtil with(Context context) {
    return new PermissionUtil(context);
  }

  //权限
  public PermissionUtil permission(String[] permissions) {
    this.permissions = permissions;
    return this;
  }

  //回调
  public PermissionUtil callback(PermissionCallback callback) {
    this.callback = callback;
    return this;
  }

  //发起请求
  public void request() {
    if (permissions == null || permissions.length <= 0) {
      return;
    }
    PermissionActivity.request(context, permissions, callback);
  }

  /**
   * 将申请被拒绝的两种情况
   */
  public abstract static class PermissionGlobalConfigCallback {

    //权限被拒绝 但是没有勾选不再提示
    abstract public void shouldShowRational(String permission, int ration);

    //被拒绝 勾选了不再提示
    abstract public void onPermissionReject(String permission, int reject);
  }
}
