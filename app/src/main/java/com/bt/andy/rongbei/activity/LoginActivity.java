package com.bt.andy.rongbei.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bt.andy.rongbei.BaseActivity;
import com.bt.andy.rongbei.MainActivity;
import com.bt.andy.rongbei.MyAppliaction;
import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.LoginInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 9:05
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdit_num;
    private EditText mEdit_psd;
    private Button   mBt_submit;
    private Dialog   dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_actiivty);
        getView();
        setData();
    }

    private void getView() {
        mEdit_num = (EditText) findViewById(R.id.edit_num);
        mEdit_psd = (EditText) findViewById(R.id.edit_psd);
        mBt_submit = (Button) findViewById(R.id.bt_login);
        dialog = new Dialog(this);
    }

    private void setData() {
        mBt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                String number = mEdit_num.getText().toString().trim();
                String pass = mEdit_psd.getText().toString().trim();
                if ("".equals(number) || "请输入工号".equals(number)) {
                    ToastUtils.showToast(LoginActivity.this, "请输入工号");
                    return;
                }
                if ("请输入密码".equals(pass) || "".equals(pass)) {
                    ToastUtils.showToast(LoginActivity.this, "请输入密码");
                    return;
                }
                new LoginTask(number, pass).execute();
                //                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //                startActivity(intent);
                break;
        }
    }

    class LoginTask extends AsyncTask<Void, String, String> {
        String username;
        String password;

        LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(LoginActivity.this, "正在登录");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("fuserno", username);
            map.put("fuserpsw", password);
            return SoapUtil.requestWebService(Consts.Login, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            LoginInfo info = gson.fromJson(s, LoginInfo.class);
            String status = info.getStatus();
            String message = info.getMessage();
            if ("1".equals(status)) {
                MyAppliaction.uerName = username;
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            ToastUtils.showToast(LoginActivity.this, message);
        }
    }
}
