package com.bt.andy.rongbei.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.activity.SaomiaoUIActivity;
import com.bt.andy.rongbei.adapter.RecyPlanAdapter;
import com.bt.andy.rongbei.adapter.RecySelectAdapter;
import com.bt.andy.rongbei.adapter.SelectPlatAdapter;
import com.bt.andy.rongbei.messegeInfo.InnerIDInfo;
import com.bt.andy.rongbei.messegeInfo.PlanGXInfo;
import com.bt.andy.rongbei.messegeInfo.PlatOrderInfo;
import com.bt.andy.rongbei.messegeInfo.SelectGoodsInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.PopupOpenHelper;
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
import java.util.Iterator;
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
    private View                  mRootView;
    private TextView              mTv_title;
    private LinearLayout          linear_right;
    private ImageView             img_xz;
    private TextView              tv_kind;
    private RecyclerView          recy_plan;
    private List<PlanGXInfo>      mData;
    private List<SelectGoodsInfo> mGoogsList;
    private RecyPlanAdapter       planAdapter;
    private EditText              et_orderid;
    private ImageView             img_scan0;
    private TextView              tv_sure0;
    private RelativeLayout        relative_good;
    private RelativeLayout        relt_pors;
    private TextView              tv_type;
    private Spinner               spi_planname;
    private TextView              bt_submit;
    private String                orderID;
    private PopupOpenHelper       openHelper;
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE0                      = 1003;//接收项目id扫描结果
    private CustomDatePicker  dpk1;
    private String            mStartTime;
    private String            planJson;
    private boolean           isAllPlaned;
    private ObjectAnimator    animatorXz;
    private SelectPlatAdapter platAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_plan, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mTv_title = mRootView.findViewById(R.id.tv_title);
        linear_right = mRootView.findViewById(R.id.linear_right);
        img_xz = mRootView.findViewById(R.id.img_xz);
        tv_kind = mRootView.findViewById(R.id.tv_kind);
        et_orderid = mRootView.findViewById(R.id.et_orderid);//输入项目id
        img_scan0 = mRootView.findViewById(R.id.img_scan0);//扫描
        tv_sure0 = mRootView.findViewById(R.id.tv_sure0);//确认输入的项目id
        relative_good = mRootView.findViewById(R.id.relative_good);
        tv_type = mRootView.findViewById(R.id.tv_type);//选择项目中的商品
        relt_pors = mRootView.findViewById(R.id.relt_pors);
        spi_planname = mRootView.findViewById(R.id.spi_planname);
        recy_plan = mRootView.findViewById(R.id.recy_check);
        bt_submit = mRootView.findViewById(R.id.bt_submit);//提交计划时间
    }

    private void initData() {
        mTv_title.setText("工序计划");
        linear_right.setVisibility(View.VISIBLE);
        //初始化列表
        initRecyList();

        //获取当前系统时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        mStartTime = df.format(new Date());

        linear_right.setOnClickListener(this);
        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_right:
                //切换显示界面
                changePlanOrSearch();
                break;
            case R.id.img_scan0:
                //动态申请照相机权限,开启照相机
                scanningCode();
                break;
            case R.id.tv_sure0:
                orderID = String.valueOf(et_orderid.getText()).trim();
                if ("排单".equals(String.valueOf(tv_kind.getText()))) {
                    //查询商品
                    searchGoodsList();
                } else {
                    //查看排单列表
                    searchPlatList();
                }
                break;
            case R.id.tv_type:
                showPop2SelectGoods();
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
                    if ("排单".equals(String.valueOf(tv_kind.getText()))) {
                        //查询商品
                        searchGoodsList();
                    } else {
                        //查看排单列表
                        searchPlatList();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void searchPlatList() {
        if (null == orderID || "".equals(orderID)) {
            ToastUtils.showToast(getContext(), "项目单号不能为空");
            return;
        }
        String sql = "select distinct a.fid,fnote2,a.FNOTE,FText " +
                "from t_BOS200000000 a inner join t_BOS200000000Entry2 b on a.FID=b.FID " +
                "inner join xj c on c.FID=a.FID where a.FNOTE='" + orderID + "' and isnull(a.fstatus,0)>0";
        new ItemPlatTask(sql).execute();
    }

    private void changePlanOrSearch() {
        //动画效果
        animatorXz = ObjectAnimator.ofFloat(img_xz, "rotation", 0f, 90f, 180f, 0f);
        animatorXz.setDuration(1000);
        animatorXz.start();
        if ("排单".equals(String.valueOf(tv_kind.getText()))) {
            tv_kind.setText("查看");
            relative_good.setVisibility(View.GONE);
            relt_pors.setVisibility(View.VISIBLE);
        } else {
            tv_kind.setText("排单");
            relative_good.setVisibility(View.VISIBLE);
            relt_pors.setVisibility(View.GONE);
        }
        mData.clear();
        planAdapter.notifyDataSetChanged();
    }

    //跳出pop可多选择商品或日期类别
    private void showPop2SelectGoods() {
        if (null == mGoogsList || mGoogsList.size() < 1) {
            ToastUtils.showToast(getContext(), "没有产品列表信息，请重新搜索。");
            return;
        }
        openHelper = new PopupOpenHelper(getContext(), tv_type, R.layout.popup_choice);
        openHelper.openPopupWindowWithView(true, 0, (int) tv_type.getY() + tv_type.getHeight());
        openHelper.setOnPopupViewClick(new PopupOpenHelper.ViewClickListener() {
            @Override
            public void onViewListener(PopupWindow popupWindow, View inflateView) {
                final CheckBox cb_no = inflateView.findViewById(R.id.cb_no);
                final CheckBox cb_all = inflateView.findViewById(R.id.cb_all);
                RecyclerView recy_goods_item = inflateView.findViewById(R.id.recy_goods_item);
                TextView tv_cancle = inflateView.findViewById(R.id.tv_cancle);
                TextView tv_sure = inflateView.findViewById(R.id.tv_sure);
                //设置数据和点击事件
                recy_goods_item.setLayoutManager(new LinearLayoutManager(getContext()));
                final RecySelectAdapter selectAdapter = new RecySelectAdapter(R.layout.recy_goods_item, getContext(), mGoogsList, cb_no, cb_all);
                recy_goods_item.setAdapter(selectAdapter);
                cb_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (cb_no.isChecked()) {
                            cb_all.setChecked(false);
                            for (SelectGoodsInfo goodsInfo : mGoogsList) {
                                goodsInfo.setIsChecked(false);
                            }
                            selectAdapter.notifyDataSetChanged();
                        }
                    }
                });
                cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (cb_all.isChecked()) {
                            cb_no.setChecked(false);
                            for (SelectGoodsInfo goodsInfo : mGoogsList) {
                                goodsInfo.setIsChecked(true);
                            }
                            selectAdapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openHelper.dismiss();
                    }
                });
                tv_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isSelect = false;
                        //选择了哪些
                        for (int i = 0; i < mGoogsList.size(); i++) {
                            if (mGoogsList.get(i).isIsChecked()) {
                                isSelect = true;
                            }
                        }
                        if (isSelect) {
                            String fnumStr = "";
                            String sql = "{\"passid\": \"8182\",\"items\": [";//{"passid": "8182","items": [{"FID":1020,"FIndex":1}]}
                            for (int i = 0; i < mGoogsList.size(); i++) {
                                if (mGoogsList.get(i).isIsChecked()) {
                                    if (sql.endsWith("[")) {
                                        sql = sql + "{\"FID\":" + mGoogsList.get(i).getFid() + ",\"FIndex\":" + mGoogsList.get(i).getFIndex() + "}";
                                    } else {
                                        sql = sql + ",{\"FID\":" + mGoogsList.get(i).getFid() + ",\"FIndex\":" + mGoogsList.get(i).getFIndex() + "}";
                                    }
                                    sql = sql + "]}";
                                    if ("".endsWith(fnumStr)) {
                                        fnumStr = fnumStr + mGoogsList.get(i).getFnumber();
                                    } else {
                                        fnumStr = fnumStr + "," + mGoogsList.get(i).getFnumber();
                                    }
                                }
                            }
                            //删除已勾选的条目
                            Iterator<SelectGoodsInfo> iterator = mGoogsList.iterator();
                            while (iterator.hasNext()) {
                                boolean isChecked = iterator.next().isIsChecked();
                                if (isChecked) {
                                    iterator.remove();
                                }
                            }
                            selectAdapter.notifyDataSetChanged();
                            //查询选择的内码
                            searchInnerID(sql, fnumStr);
                        } else {
                            ToastUtils.showToast(getContext(), "您未选择产品");
                        }
                        openHelper.dismiss();
                    }
                });
            }
        });
    }

    private void searchGoodsList() {
        if (null == orderID || "".equals(orderID)) {
            ToastUtils.showToast(getContext(), "项目单号不能为空");
            return;
        }
        String sql = "select b.fid,b.FIndex,c.fnumber,c.fname,c.fmodel,FQty,convert(varchar(50),b.fdate3,23)fdate,F_106 " +
                "from t_BOS200000000 a inner join xj b on a.fid=b.FID inner join  t_ICItem c on c.FItemID=b.FBase5  " +
                "where isnull(b.fstatus,0)=0 and a.fnote='" + orderID + "' order by convert(varchar(50),b.fdate3,23)";
        new ItemGoodsTask(sql).execute();
    }

    private void searchInnerID(String sql, String fnumStr) {
        new ItemInnerIDTask(sql, fnumStr).execute();
    }

    private void sendPlanDate() {
        isAllPlaned = true;
        if (null == orderID || "".equals(orderID)) {
            ToastUtils.showToast(getContext(), "请先填写查找订单号");
            return;
        }
        if (mData.size() < 1) {
            ToastUtils.showToast(getContext(), "没有需要提交的数据");
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            if (null == mData.get(i).getFDate1() || "".equals(mData.get(i).getFDate1()) || "--".equals(mData.get(i).getFDate1())) {
                isAllPlaned = false;
            }
        }
        if (!isAllPlaned) {
            ToastUtils.showToast(getContext(), "您有条目未设置计划时间，请填写");
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

    private List<PlatOrderInfo> mPlatList;
    private int                 platId;

    private void initRecyList() {
        mGoogsList = new ArrayList();
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
        //选择单据列表   spi_planname
        mPlatList = new ArrayList<>();
        platAdapter = new SelectPlatAdapter(getContext(), mPlatList);
        spi_planname.setAdapter(platAdapter);
        spi_planname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PlatOrderInfo proceInfo = mPlatList.get(i);
                platId = proceInfo.getFid();
                //查看排单表
                searchList("" + platId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void selectOrderTime(final int position) {
        //打开时间选择器
        dpk1 = new CustomDatePicker(getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                mData.get(position).setFDate1(time.substring(0, 10));
                mData.get(position).setChangeTimes(1);
                planAdapter.notifyDataSetChanged();
            }
        }, "2000-01-01 00:00", "2090-12-31 00:00"); //初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        dpk1.showSpecificTime(false); // 显示时和分
        dpk1.setIsLoop(true); // 允许循环滚动
        dpk1.show(mStartTime);
    }

    //查看生产任务单列表
    private void searchList(String innerID) {
        if (null == innerID || "".equals(innerID)) {
            ToastUtils.showToast(getContext(), "未查找到内码");
            return;
        }
        String sql;
        if ("排单".equals(String.valueOf(tv_kind.getText()))) {
            sql = "select b.FName,c.FName,a.FID,a.FIndex,d.FNOTE,convert(varchar(50),FDate1,23)FDate1,convert(varchar(50),FDate2,23)FDate2 " +
                    "from t_BOS200000000Entry2 a inner join t_BOS200000000 d on a.FID=d.FID left join  t_Item_3001 b on a.FBase4=b.FItemID " +
                    "left join t_Emp c on c.FItemID=a.FBase1  where d.FNOTE ='" + innerID + "' order by a.FID,a.FIndex";
        } else {
            sql = "select b.FName,c.FName,a.FID,a.FIndex,d.FNOTE,convert(varchar(50),FDate1,23)FDate1,convert(varchar(50),FDate2,23)FDate2 " +
                    "from t_BOS200000000Entry2 a inner join t_BOS200000000 d on a.FID=d.FID left join  t_Item_3001 b on a.FBase4=b.FItemID " +
                    "left join t_Emp c on c.FItemID=a.FBase1  where d.fid ='" + innerID + "' order by a.FID,a.FIndex";
        }
        new ItemListTask(sql).execute();
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

    //查询排单
    class ItemPlatTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemPlatTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在查找,请稍等...");
            if (null == mPlatList) {
                mPlatList = new ArrayList();
            } else {
                mPlatList.clear();
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
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PlatOrderInfo platOrderInfo = gson.fromJson(jsonArray.get(i).toString(), PlatOrderInfo.class);
                    mPlatList.add(platOrderInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            platAdapter.notifyDataSetChanged();
        }
    }

    //查询商品
    class ItemGoodsTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemGoodsTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在查找,请稍等...");
            if (null == mGoogsList) {
                mGoogsList = new ArrayList();
            } else {
                mGoogsList.clear();
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
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    SelectGoodsInfo selectGoodsInfo = gson.fromJson(jsonArray.get(i).toString(), SelectGoodsInfo.class);
                    mGoogsList.add(selectGoodsInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //查看生产任务单列表
    class ItemListTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemListTask(String sql) {
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
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PlanGXInfo planGXInfo = gson.fromJson(jsonArray.get(i).toString(), PlanGXInfo.class);
                    mData.add(planGXInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            planAdapter.notifyDataSetChanged();
        }
    }

    //查看选择单据内码
    class ItemInnerIDTask extends AsyncTask<Void, String, String> {
        String            sql;
        String            fnumStr;

        ItemInnerIDTask(String sql, String fnumStr) {
            this.sql = sql;
            this.fnumStr = fnumStr;
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
            map.put("passid", "8182");
            map.put("fnumber", fnumStr);
            map.put("fbillno", orderID);
            map.put("FJSONMSG", sql);
            return SoapUtil.requestWebService(Consts.T_BOS200000000, map);
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                InnerIDInfo innerIDInfo = gson.fromJson(jsonArray.get(0).toString(), InnerIDInfo.class);
                //查询生成任务单
                searchList(innerIDInfo.getFinterid());
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showToast(getContext(), "查询失败");
            }
        }
    }
}
