package com.bt.andy.rongbei.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.activity.SaomiaoUIActivity;
import com.bt.andy.rongbei.adapter.RecySearchAdapter;
import com.bt.andy.rongbei.messegeInfo.SearchGXInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2019/3/7 15:01
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Search_F extends Fragment implements View.OnClickListener {
    private View               mRootView;
    private TextView           mTv_title;
    private SwipeRefreshLayout swipe;
    private RecyclerView       recy_check;
    private List<SearchGXInfo> mData;
    private RecySearchAdapter  searchAdapter;
    private EditText           et_orderid;
    private ImageView          img_scan0;
    private TextView           tv_sure0;
    private String             orderID;
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE0                      = 1003;//接收项目id扫描结果

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.search_f, null);
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
        mTv_title.setText("工序查看");

        //初始化列表
        initRecyList();
        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新列表
                searchList();
            }
        });
    }

    private void initRecyList() {
        mData = new ArrayList();
        recy_check.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new RecySearchAdapter(R.layout.rec_item_search, getContext(), mData);
        recy_check.setAdapter(searchAdapter);
    }

    private void searchList() {
        if (null == orderID || "".equals(orderID) || "项目单号".equals(orderID)) {
            ToastUtils.showToast(getContext(), "请输入项目单号");
            return;
        }
        new SearchTask(orderID).execute();//789
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
                searchList();
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
                    //获取商品id信息，跳转activity展示，在新的页面确定后添加到listview中
                    orderID = result;
                    searchList();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

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

    //提交
    class SearchTask extends AsyncTask<Void, String, String> {
        String FJSONMSG;

        SearchTask(String FJSONMSG) {
            this.FJSONMSG = FJSONMSG;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在查询...");
            swipe.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("FPSW", "8182");
            map.put("FORDERBILLNO", FJSONMSG);
            return SoapUtil.requestWebService(Consts.JA_LIST2, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            swipe.setRefreshing(false);
            if (null == mData) {
                mData = new ArrayList();
            } else {
                mData.clear();
            }
            try {
                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    SearchGXInfo info = gson.fromJson(jsonArray.get(i).toString(), SearchGXInfo.class);
                    mData.add(info);
                }
                searchAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showToast(getContext(), "查找失败");
            }
        }
    }
}
