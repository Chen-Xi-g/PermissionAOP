# AOP 动态权限申请

> 为了保护用户隐私，Google在Android 6.0 添加了危险权限申请，这个对于开发来说是真的烦。
>
> 每次申请权限都需要进行大量的重复代码，对于我来说根本没有必要做这些多余的工作，在查阅一些资料后写了这个AOP权限申请框架，AOP是什么请自行百度。

## 一、导入

1. 首先你需要下载 *Permission-lib* 导入项目

2. 在 *Settings.gradle* 中添加 ':permission-lib'

   ```
   include ':app', ':permission-lib'
   ```

3. *build.gradle* 添加 aspectjx

   ```
   dependencies {
       ...
       /*
           如果出现这个错误
           Cannot cast object 'com.android.build.gradle.internal.pipeline.TransformTask$2$1@590ffd3a' with class 'com.android.build.gradle.internal.pipeline.TransformTask$2$1' to class 'com.android.build.gradle.internal.pipeline.TransformTask'
           更新aspectjx版本
        */
       classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
       ...
     }
   ```

4. app-build.gradle 中添加 apply plugin: 'android-aspectjx' 和 implementation project(path: ':permission-lib')

   ```
   apply plugin: 'android-aspectjx'
   ...
   dependencies {
     ...
     implementation project(path: ':permission-lib')
     ...
   }
   ```

5. Sync Now

## 二、使用

1. 初始化权限申请失败的监听

   ```java
   PermissionUtil.init(new PermissionUtil.PermissionGlobalConfigCallback() {
   
         //直接申请权限
         @Override public void shouldShowRational(String permission, int ration) {
           ...
         }
   
         //跳转到设置申请权限
         @Override public void onPermissionReject(String permission, int reject) {
           ...
         }
       });
   ```

2. 定义全局Dialog提示权限申请失败信息。

   > showRationaleDialog()是没有勾选不再提示被拒绝的Dialog。
   > showRejectDialog()是被勾选不再提示的Dialog，需要手动跳转到设置页开启权限。

   ```java
   //直接申请权限的Dialog
     private void showRationaleDialog(int ration) {
       new AlertDialog.Builder(this).setTitle("权限申请").setMessage(getString(ration)).setNegativeButton("取消", null).show();
     }
   
     //需要跳转到设置的Dialog
     private void showRejectDialog(int reject) {
       new AlertDialog.Builder(this).setTitle("权限申请")
           .setMessage(getString(reject))
           .setPositiveButton("跳转到设置页", new DialogInterface.OnClickListener() {
             @Override public void onClick(DialogInterface dialog, int which) {
               //手动跳转到设置同意权限
               Intent intent = new Intent();
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
               intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
               startActivity(intent);
               dialog.dismiss();
             }
           })
           .setNegativeButton("取消", null)
           .show();
     }
   ```

3. 在PermissionUtil中定义申请权限的失败时提示的常量值

   ```java
     public static final int READ_EXTERNAL_STORAGE_RATIONALE = 100;//允许应用程序从外部存储读取被拒绝
     public static final int READ_EXTERNAL_STORAGE_REJECT = 1000;//允许应用程序从外部存储读取勾选不再提示被拒绝提
   
     public static final int WRITE_EXTERNAL_STORAGE_RATIONALE = 101;//允许应用程序写入外部存储被拒绝
     public static final int WRITE_EXTERNAL_STORAGE_REJECT = 1010;//允许应用程序写入外部存储勾选不再提示被拒绝提
   ```

   

4. 在需要申请权限的方法上添加注解@Permission，这个方法只有所有权限同意后才会执行。

   ```java
   @Permission(
       permissions = { Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },
       rationales = { PermissionUtil.READ_EXTERNAL_STORAGE_RATIONALE,PermissionUtil.WRITE_EXTERNAL_STORAGE_RATIONALE },
       rejects = {PermissionUtil.READ_EXTERNAL_STORAGE_REJECT, PermissionUtil.WRITE_EXTERNAL_STORAGE_REJECT })
   private void getStorage(){
     //只有获取全部权限才会执行
     Toast.makeText(this,"获取存储权限成功",Toast.LENGTH_LONG).show();
   }
   ```

   

5. 在PermissionUtil.init(PermissionGlobalConfigCallback)重写的两个方法会在权限被拒绝后返回@Permission中rationales和rejects传入的常量值，获取到常量值后可以通过常量值进行匹配提示权限申请失败的信息。

   ```java
   private void permissionInit() {
       //初始化权限申请监听
       PermissionUtil.init(new PermissionUtil.PermissionGlobalConfigCallback() {
   
         //直接申请权限被拒绝
         @Override public void shouldShowRational(String permission, int ration) {
          	//通过@Permission传入的ration判断要提示的信息，如果没有匹配到ration就使用默认的提示信息。
           switch (ration) {
             //获取写入权限失败      
             case PermissionUtil.WRITE_EXTERNAL_STORAGE_RATIONALE:
               //显示Dialog提示权限被拒绝  图2    
               showRationaleDialog(R.string.write_external_storage_permission_rationale);
               break;
             case PermissionUtil.READ_EXTERNAL_STORAGE_RATIONALE:
               showRationaleDialog(R.string.read_external_storage_permission_rationale);
               break;
             case PermissionUtil.CAMERA_RATIONALE:
               showRationaleDialog(R.string.camera_permission_rationale);
               break;
             case PermissionUtil.READ_PHONE_STATE_RATIONALE:
               showRationaleDialog(R.string.read_phone_state_permission_rationale);
               break;
             case PermissionUtil.CALL_PHONE_RATIONALE:
               showRationaleDialog(R.string.call_phone_permission_rationale);
               break;
             default://如果没有设置RATIONALE就使用默认提示
               showRationaleDialog(R.string.permission_rationale);
               break;
           }
         }
   
         //跳转到设置申请权限
         @Override public void onPermissionReject(String permission, int reject) {
           //通过@Permission传入的ration判断要提示的信息，如果没有匹配到reject就使用默认的提示信息。
           switch (reject) {
             //获取写入权限失败      
             case PermissionUtil.WRITE_EXTERNAL_STORAGE_REJECT:
               //显示Dialog提示权限被拒绝  图3
               showRejectDialog(R.string.write_external_storage_permission_reject);
               break;
             case PermissionUtil.READ_EXTERNAL_STORAGE_REJECT:
               showRejectDialog(R.string.read_external_storage_permission_reject);
               break;
             case PermissionUtil.CAMERA_REJECT:
               showRejectDialog(R.string.camera_permission_reject);
               break;
             case PermissionUtil.READ_PHONE_STATE_REJECT:
               showRejectDialog(R.string.read_phone_state_permission_reject);
               break;
             case PermissionUtil.CALL_PHONE_REJECT:
               showRejectDialog(R.string.call_phone_permission_reject);
               break;
             default://如果没有设置REJECT就使用默认提示
               showRejectDialog(R.string.permission_reject);
               break;
           }
         }
       });
     }
   ```

<img src="http://www.minlukj.com/wp-content/uploads/2020/07/permission-camera.png" alt="图1" style="zoom:50%;" />

<img src="http://www.minlukj.com/wp-content/uploads/2020/07/permission-rationales.png" alt="图2" style="zoom:50%;" />

<img src="http://www.minlukj.com/wp-content/uploads/2020/07/permission-rejects.png" alt="图3" style="zoom:50%;" />

 ### 如何联系我(How to contact me)

 **QQ:** 1217056667

 **WeChat：**Alvin-_-G

 **邮箱(Email):** a912816369@gmail.com

 **小站:** www.minlukj.com

