package com.bt.andy.rongbei.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.rongbei.MyAppliaction;
import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.activity.SaomiaoUIActivity;
import com.bt.andy.rongbei.adapter.RecyCheckAdapter;
import com.bt.andy.rongbei.messegeInfo.CheckInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2019/2/25 14:07
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Check_F extends Fragment implements View.OnClickListener {
    private View               mRootView;
    private TextView           mTv_title;
    private EditText           et_orderid;
    private ImageView          img_scan0;
    private TextView           tv_sure0;
    private SwipeRefreshLayout swipe;
    private RecyclerView       recy_check;
    private RecyCheckAdapter   checkAdapter;
    private List<CheckInfo>    mData;
    private String             orderID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.check_f, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mTv_title = mRootView.findViewById(R.id.tv_title);
        et_orderid = mRootView.findViewById(R.id.et_orderid);//输入项目id
        img_scan0 = mRootView.findViewById(R.id.img_scan0);//扫描
        tv_sure0 = mRootView.findViewById(R.id.tv_sure0);//确认输入的项目id

        swipe = mRootView.findViewById(R.id.swipe);
        recy_check = mRootView.findViewById(R.id.recy_check);
    }

    private void initData() {
        mTv_title.setText("流程检验");
        //初始化列表
        initRecyList();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新列表
                refreshCheckList(orderID);
            }
        });
        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_scan0:
                //动态申请照相机权限,开启照相机
                scanningCode();
                break;
            case R.id.tv_sure0:
                orderID = String.valueOf(et_orderid.getText()).trim();
                refreshCheckList(orderID);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE0) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //订单id
                    orderID = result;
                    et_orderid.setText(orderID);
                    refreshCheckList(orderID);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initRecyList() {
        mData = new ArrayList();
        recy_check.setLayoutManager(new LinearLayoutManager(getContext()));
        checkAdapter = new RecyCheckAdapter(R.layout.rec_item_check, getContext(), mData);
        recy_check.setAdapter(checkAdapter);

        checkAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.line_write:
                        //填写合格数
                        writeHGNum(position);
                        break;
                    case R.id.tv_sure:
                        new SubmitTask(position).execute();
                        break;
                }
            }
        });
    }

    private void refreshCheckList(String orderID) {
        if (null == orderID)
            orderID = "";
        new CheckTask(orderID).execute();
    }

    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE0                      = 1003;//接收项目id扫描结果

    private void scanningCode() {
        //第二个参数是需要申请的权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            Intent intent = new Intent(getContext(), SaomiaoUIActivity.class);//这是一个自定义的扫描界面，扫描UI框放大了。
            //            Intent intent = new Intent(getContext(), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE0);
        }
    }

    private void writeHGNum(final int position) {
        final EditText et = new EditText(getContext());
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(getContext()).setView(et).setTitle("填入")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //修改textview的内容
                        int wrNum = 0;
                        String content = String.valueOf(et.getText()).trim();
                        if (!content.equals("")) {
                            wrNum = Integer.parseInt(content);
                        }
                        if (wrNum > mData.get(position).getFAuxQtyFinish()) {
                            ToastUtils.showToast(getContext(), "合格数不能大于检验数");
                            return;
                        }
                        mData.get(position).setFHGQty(wrNum);
                        checkAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
    }

    //查询所有检验单
    class CheckTask extends AsyncTask<Void, String, String> {
        private String fbillno;

        CheckTask(String fbillno) {
            this.fbillno = fbillno;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipe.setRefreshing(true);
            ProgressDialogUtil.startShow(getContext(), "正在查询工序检验单");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("fbillno", fbillno);
            return SoapUtil.requestWebService(Consts.JA_gongxujianyan, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipe.setRefreshing(false);
            ProgressDialogUtil.hideDialog();
            if (null == mData) {
                mData = new ArrayList<>();
            } else {
                mData.clear();
            }
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    CheckInfo info = gson.fromJson(jsonArray.get(i).toString(), CheckInfo.class);
                    info.setFHGQty(info.getFAuxQtyFinish());
                    mData.add(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            checkAdapter.notifyDataSetChanged();
        }
    }

    //提交
    class SubmitTask extends AsyncTask<Void, String, String> {
        int position;

        SubmitTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在提交...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            //获取当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String format = df.format(new Date());

            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("fdate", format);
            map.put("fjianyanyuan", MyAppliaction.fjianyanyuan);
            map.put("fuserid", MyAppliaction.userID);
            map.put("finterid", mData.get(position).getFInterID());
            map.put("fentryindex", mData.get(position).getFEntryIndex());
            map.put("hgqty", mData.get(position).getFHGQty());
            map.put("fbhgqty", 0);
            map.put("fbfqty", 0);
            map.put("ffxqty", 0);
            map.put("mdgx", mData.get(position).getFSFSDGX());
            map.put("fnote", null == mData.get(position).getFnote() ? "" : mData.get(position).getFnote());
            return SoapUtil.requestWebService(Consts.JA_gongxujianyan_insert, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            if (s.contains("成功")) {
                ToastUtils.showToast(getContext(), "提交成功");
            } else {
                ToastUtils.showToast(getContext(), "提交失败");
            }
            refreshCheckList(orderID);
        }
    }
}
