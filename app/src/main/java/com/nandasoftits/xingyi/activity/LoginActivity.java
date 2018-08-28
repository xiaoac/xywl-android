package com.nandasoftits.xingyi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.nandasoftits.xingyi.R;
import com.nandasoftits.xingyi.sharedperferences.UserSharedPreferences;
import com.nandasoftits.xingyi.utils.ActivityJumpHelper;
import com.nandasoftits.xingyi.utils.PushUtils;
import com.nandasoftits.xingyi.view.MapPicker;

public class LoginActivity extends BasicActivity {

    private EditText mTestPathEdit;

    private Button mGoBtn;

    private Button mPushTestBtn;

    private View mLocationSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initH5Test();
        initMapTest();
    }

    private MapPicker mMapPicker;


    private void initMapTest() {
        mLocationSelect = findViewById(R.id.new_map_test);

        mMapPicker = new MapPicker(this);

        mLocationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapPicker.show();
            }
        });
    }

    private void initH5Test() {

        mTestPathEdit = findViewById(R.id.test_path);

        String testPath = UserSharedPreferences.getMsg(LoginActivity.this, "TEST_PATH");

        if (TextUtils.isEmpty(testPath)) {
            testPath = "http://192.168.2.103:8080/views/test/android/main-view.html";
        }

        mTestPathEdit.setText(testPath);

        mGoBtn = findViewById(R.id.go_h5_test);

        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mTestPathEdit.getText().toString();
                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(LoginActivity.this, "请输入测试地址", Toast.LENGTH_LONG).show();
                    return;
                }

                UserSharedPreferences.saveMsg(LoginActivity.this, "TEST_PATH", path);
                ActivityJumpHelper.goH5Activity(LoginActivity.this, path);
                finish();
            }
        });


        //推送测试数据写入
        mPushTestBtn = findViewById(R.id.push_test_info);
        mPushTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSharedPreferences.saveMsg(LoginActivity.this, UserSharedPreferences.SPHelp.PUSH_ALIAS, "xy_test_alias");
                UserSharedPreferences.saveMsg(LoginActivity.this, UserSharedPreferences.SPHelp.PUSH_TAG, "xy_test_tag");

                PushUtils.initJPush(LoginActivity.this);

                Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapPicker.dismiss();
    }
}
