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
import com.bt.andy.rongbei.adapter.RecyPlanAdapter;
import com.bt.andy.rongbei.messegeInfo.PlanGXInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
import com.bt.andy.rongbei.viewmodle.CustomDatePicker;
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
 * @创建时间 2019/3/12 16:23
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Plan_F extends Fragment implements View.OnClickListener {
    private View               mRootView;
    private TextView           mTv_title;
    private SwipeRefreshLayout swipe;
    private RecyclerView       recy_plan;
    private List<PlanGXInfo>   mData;
    private RecyPlanAdapter    planAdapter;
    private EditText           et_orderid;
    private ImageView          img_scan0;
    private TextView           tv_sure0;
    private TextView           bt_submit;
    private String             orderID;
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE0                      = 1003;//接收项目id扫描结果

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_plan, null);
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
        recy_plan = mRootView.findViewById(R.id.recy_check);
        bt_submit = mRootView.findViewById(R.id.bt_submit);//提交计划时间
    }

    private void initData() {
        mTv_title.setText("工序计划");

        //初始化列表
        initRecyList();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新列表
                searchList();
            }
        });

        //获取当前系统时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        mStartTime = df.format(new Date());

        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
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
            case R.id.bt_submit://提交
                sendPlanDate();
                break;
            default:
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
                    searchList();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String planJson;

    private void sendPlanDate() {
        if (null == orderID || "".equals(orderID)) {
            ToastUtils.showToast(getContext(), "请先填写查找订单号");
            return;
        }
        if (mData.size() < 1) {
            ToastUtils.showToast(getContext(), "没有需要提交的数据");
            return;
        }
        //{"passid": "8182","items": [{"fdate": "2018-04-19","FID":1005,"FIndex":1},{"fdate": "2018-04-19","FID":1005,"FIndex":2}]}
        planJson = "{\"passid\": \"8182\",\"items\": [";
        for (int i = 0; i < mData.size(); i++) {
            if (i == mData.size() - 1) {
                planJson = planJson + "{\"fdate\": \"" + mData.get(i).getFDate1() + "\",\"FID\":" + mData.get(i).getFID() + ",\"FIndex\":" + mData.get(i).getFIndex() + "}";
            } else {
                planJson = planJson + "{\"fdate\": \"" + mData.get(i).getFDate1() + "\",\"FID\":" + mData.get(i).getFID() + ",\"FIndex\":" + mData.get(i).getFIndex() + "},";
            }
        }
        planJson = planJson + "]}";
        new SubmitTask(planJson).execute();
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

    private void initRecyList() {
        mData = new ArrayList();
        recy_plan.setLayoutManager(new LinearLayoutManager(getContext()));
        planAdapter = new RecyPlanAdapter(R.layout.rec_item_plan, getContext(), mData);
        recy_plan.setAdapter(planAdapter);
        planAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectOrderTime(position);
            }
        });
    }

    private CustomDatePicker dpk1;
    private String           mStartTime;

    private void selectOrderTime(final int position) {
        //打开时间选择器
        dpk1 = new CustomDatePicker(getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                mData.get(position).setFDate1(time.substring(0, 10));
                planAdapter.notifyDataSetChanged();
            }
        }, "2000-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        dpk1.showSpecificTime(false); // 显示时和分
        dpk1.setIsLoop(true); // 允许循环滚动
        dpk1.show(mStartTime);
    }

    //查看生产任务单列表
    private void searchList() {
        if (null == orderID || "".equals(orderID)) {
            ToastUtils.showToast(getContext(), "项目单号不能为空");
            return;
        }
        String sql = "select b.FName,c.FName,a.FID,a.FIndex,d.FNOTE,convert(varchar(50),FDate1,23)FDate1,convert(varchar(50),FDate2,23)FDate2 " +
                "from t_BOS200000000Entry2 a inner join t_BOS200000000 d on a.FID=d.FID left join  t_Item_3001 b on a.FBase4=b.FItemID " +
                "left join t_Emp c on c.FItemID=a.FBase1  where d.FNOTE ='" + orderID + "' order by a.FID,a.FIndex";
        new ItemTask(sql).execute();
    }

    //提交安排的时间
    class SubmitTask extends AsyncTask<Void, String, String> {
        String FJSONMSG;

        SubmitTask(String FJSONMSG) {
            this.FJSONMSG = FJSONMSG;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在提交");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("FJSONMSG", FJSONMSG);
            return SoapUtil.requestWebService(Consts.SUB_PLAN_TIME, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            if (s.contains("成功")) {
                ToastUtils.showToast(getContext(), "提交成功");
                mData.clear();
                planAdapter.notifyDataSetChanged();
            } else {
                ToastUtils.showToast(getContext(), "提交失败");
            }
        }
    }

    class ItemTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在查找,请稍等...");
            if (null == mData) {
                mData = new ArrayList();
            } else {
                mData.clear();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("FPSW", "8182");
            map.put("FSQL", sql);
            return SoapUtil.requestWebService(Consts.JA_LIST, map);
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            swipe.setRefreshing(false);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PlanGXInfo planGXInfo = gson.fromJson(jsonArray.get(i).toString(), PlanGXInfo.class);
                    mData.add(planGXInfo);
                }
                planAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
