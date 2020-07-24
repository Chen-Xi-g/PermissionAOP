package com.minlukj.permissionaop;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.isunland.permission_lib.permission.Permission;
import com.isunland.permission_lib.permission.PermissionUtil;
import com.isunland.permission_lib.single.SingleClick;

public class SettingsActivity extends AppCompatActivity {

  private TextView mTvStorage;
  private TextView mTvCamera;
  private TextView mTvPhone;

  private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    //@SingleClick(2000) 自定义间隔时间，默认1000ms
    @SingleClick
    @Override public void onClick(View v) {
      if (v == mTvStorage){
        getStorage();
      }else if (v == mTvCamera){
        getCamera();
      }else if (v == mTvPhone){
        getPhone();
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    mTvStorage = findViewById(R.id.tv_storage);
    mTvCamera = findViewById(R.id.tv_camera);
    mTvPhone = findViewById(R.id.tv_phone);

    permissionInit();

    onClickInit();

  }

  @Permission(permissions = { Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },
      rationales = { PermissionUtil.READ_EXTERNAL_STORAGE_RATIONALE,PermissionUtil.WRITE_EXTERNAL_STORAGE_RATIONALE },
      rejects = {PermissionUtil.READ_EXTERNAL_STORAGE_REJECT, PermissionUtil.WRITE_EXTERNAL_STORAGE_REJECT })
  private void getStorage(){
    //只有获取全部权限才会执行
    Toast.makeText(this,"获取存储权限成功",Toast.LENGTH_LONG).show();
  }

  @Permission(permissions = Manifest.permission.CAMERA , rationales = PermissionUtil.CAMERA_RATIONALE, rejects = PermissionUtil.CAMERA_REJECT )
  private void getCamera(){
    Toast.makeText(this,"获取相机权限成功",Toast.LENGTH_LONG).show();
  }

  @Permission(permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
      Manifest.permission.WRITE_CALL_LOG})
  private void getPhone(){
    Toast.makeText(this,"获取手机组权限成功",Toast.LENGTH_LONG).show();
  }

  private void onClickInit() {
    mTvStorage.setOnClickListener(mOnClickListener);
    mTvCamera.setOnClickListener(mOnClickListener);
    mTvPhone.setOnClickListener(mOnClickListener);
  }

  private void permissionInit() {
    //初始化权限申请监听
    PermissionUtil.init(new PermissionUtil.PermissionGlobalConfigCallback() {

      //直接申请权限
      @Override public void shouldShowRational(String permission, int ration) {
        switch (ration) {
          case PermissionUtil.WRITE_EXTERNAL_STORAGE_RATIONALE:
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
        switch (reject) {
          case PermissionUtil.WRITE_EXTERNAL_STORAGE_REJECT:
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
}