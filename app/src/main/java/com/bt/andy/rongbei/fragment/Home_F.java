package com.bt.andy.rongbei.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.rongbei.MyAppliaction;
import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.activity.SaomiaoUIActivity;
import com.bt.andy.rongbei.adapter.SelectProGoodsAdapter;
import com.bt.andy.rongbei.adapter.SelectWorkPerAdapter;
import com.bt.andy.rongbei.adapter.SelectWorkProceAdapter;
import com.bt.andy.rongbei.adapter.TransferAdapter;
import com.bt.andy.rongbei.messegeInfo.GoodsInfo;
import com.bt.andy.rongbei.messegeInfo.LiuZhuanInfo;
import com.bt.andy.rongbei.messegeInfo.LoginInfo;
import com.bt.andy.rongbei.messegeInfo.ProGoodsInfo;
import com.bt.andy.rongbei.messegeInfo.WorkPerInfo;
import com.bt.andy.rongbei.messegeInfo.WorkProceInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
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
 * @创建时间 2018/5/22 16:41
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Home_F extends Fragment implements View.OnClickListener {
    private View                   mRootView;
    private TextView               mTv_title;
    private Dialog                 dialog;
    private Spinner                spi_cho;
    private Spinner                spi_sce;
    private Spinner                spi_goodname;
    private SelectWorkProceAdapter workProceAdapter;//工序选择适配器
    private SelectWorkPerAdapter   workPerAdapter;//操作员选择适配器
    private SelectProGoodsAdapter  proGoodsAdapter;//商品选择适配器
    private String workProid   = "";//记录工序
    private String goodsNumber = "";//记录商品单号
    private String goodsName   = "";//记录商品名称
    private int    workPerId   = 0;//记录操作员
    private List<WorkProceInfo> mListProce;//记录工序
    private List<WorkPerInfo>   mListPer;//记录操作员
    private List<ProGoodsInfo>  mListGoods;//记录商品
    private EditText            et_orderid;
    private TextView            tv_sure0;
    private TextView            tv_search;
    private ImageView           img_scan0;
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE                       = 1002;//接收扫描结果
    private int REQUEST_CODE0                      = 1003;//接收项目id扫描结果
    public  List<String>    mRecData;//存放recyclerview数据
    public  List<GoodsInfo> mGoodsData;//存放每个商品的数据
    private RecyclerView    rec_detail;
    private TransferAdapter adapter;
    private TextView        mBt_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_total, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mTv_title = mRootView.findViewById(R.id.tv_title);
        spi_cho = mRootView.findViewById(R.id.spi_cho);//工序下拉选择
        spi_sce = mRootView.findViewById(R.id.spi_sce);//操作员下拉选择
        spi_goodname = mRootView.findViewById(R.id.spi_goodname);//项目商品下拉选择
        et_orderid = mRootView.findViewById(R.id.et_orderid);//输入项目id
        img_scan0 = mRootView.findViewById(R.id.img_scan0);//扫描
        tv_sure0 = mRootView.findViewById(R.id.tv_sure0);//确认输入的项目id
        tv_search = mRootView.findViewById(R.id.tv_search);//确认选择的项目中商品名称
        rec_detail = mRootView.findViewById(R.id.rec_detail);
        mBt_submit = mRootView.findViewById(R.id.bt_submit);//总表提交服务器
    }

    private void initData() {
        mTv_title.setText("工作汇报");
        //初始化选择工序列表
        initGXList();
        //初始化职员列表
        initZYList();

        //初始化工序详情列表
        mRecData = new ArrayList();
        mGoodsData = new ArrayList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 10);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 8 == 1 || position % 8 == 3) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        rec_detail.setLayoutManager(gridLayoutManager);
        adapter = new TransferAdapter(getContext(), mRecData, mGoodsData, mListProce, workProid);
        rec_detail.setAdapter(adapter);
        //初始化项目商品列表
        initProGoods();


        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        mBt_submit.setOnClickListener(this);
        if (mGoodsData.size() == 0) {
            mBt_submit.setVisibility(View.GONE);
        }
        //查询所有工序
        new WorkProTask("", "").execute();
    }

    private void initProGoods() {
        mListGoods = new ArrayList();
        ProGoodsInfo goodsInfo = new ProGoodsInfo();
        goodsInfo.setFname("请选择商品名称");
        mListGoods.add(goodsInfo);
        proGoodsAdapter = new SelectProGoodsAdapter(getContext(), mListGoods);
        spi_goodname.setAdapter(proGoodsAdapter);
        spi_goodname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    ToastUtils.showToast(getContext(), "请选择商品");
                    goodsNumber = "";
                    goodsName = "";
                } else {
                    goodsNumber = mListGoods.get(i).getFnumber();
                    goodsName = mListGoods.get(i).getFname();
                    //根据商品名称，对应查询
                    searchDetailByGoods();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void searchDetailByGoods() {

    }

    private void initZYList() {
        mListPer = new ArrayList<>();
        WorkPerInfo workProinfo = new WorkPerInfo();
        workProinfo.setFname("请选择操作员");
        mListPer.add(workProinfo);
        workPerAdapter = new SelectWorkPerAdapter(getContext(), mListPer);
        spi_sce.setAdapter(workPerAdapter);
        spi_sce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    ToastUtils.showToast(getContext(), "请选择操作员");
                    workPerId = 0;
                } else {
                    WorkPerInfo proceInfo = mListPer.get(i);
                    workPerId = proceInfo.getFitemid();//获得职员
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initGXList() {
        mListProce = new ArrayList<>();
        WorkProceInfo workProinfo = new WorkProceInfo();
        workProinfo.setFName("请选择工序");
        mListProce.add(workProinfo);
        workProceAdapter = new SelectWorkProceAdapter(getContext(), mListProce);
        spi_cho.setAdapter(workProceAdapter);
        spi_cho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    ToastUtils.showToast(getContext(), "请选择工序");
                } else {
                    WorkProceInfo proceInfo = mListProce.get(i);
                    workProid = proceInfo.getFName();//获得工序
                    //查询同工序下的员工
                    searchPerByGX(workProid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void searchPerByGX(String workProid) {
        new WorkPerTask(workProid).execute();
    }

    private String orderId;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_scan0:
                //动态申请照相机权限,开启照相机
                scanningCode(0);
                break;
            case R.id.img_scan:
                //动态申请照相机权限
                scanningCode(1);
                break;
            case R.id.tv_sure0://查看项目详情
                orderId = String.valueOf(et_orderid.getText()).trim();
                if (null == orderId || "".equals(orderId) || "项目单号".equals(orderId)) {
                    ToastUtils.showToast(getContext(), "请输入项目单号");//SEORD000005
                    return;
                }
                //TODO:查询项目
                searchForData(orderId);
                break;
            //            case R.id.tv_surema:
            //                String orderId1 = String.valueOf(et_orderid.getText()).trim();
            //                if (null == orderId1 || "".equals(orderId1) || "项目单号".equals(orderId1)) {
            //                    ToastUtils.showToast(getContext(), "请先输入查找项目单号");
            //                    return;
            //                }
            //                String goodsid = String.valueOf(mEdit_goods_id.getText()).trim();
            //                if (null == goodsid || "".equals(goodsid) || "流转卡条码".equals(goodsid)) {
            //                    ToastUtils.showToast(getContext(), "请输入流转卡条码");
            //                    return;
            //                }
            //                //TODO:跳转流转卡号对应的内容
            //                searchFromData(goodsid);
            //                break;
            case R.id.tv_search:
                orderId = String.valueOf(et_orderid.getText()).trim();
                if (null == orderId || "".equals(orderId) || "项目单号".equals(orderId)) {
                    ToastUtils.showToast(getContext(), "请输入项目单号");//SEORD000005
                    return;
                }
                //查询项目详情
                new ProjectTask(orderId, workProid).execute();
                break;
            case R.id.bt_submit:
                //TODO:提交总表到服务器
                //{"passid": "8182","items":[{"fdate":"2018-07-23","fid":"1010","fentryid":"14","fqty":2,"fbiller":"morningstar","fjyname":"免检","fsfsdgx":"否","fsfmdgx":"是","fsfddgx":"是"}]}
                String partArg = "{\"passid\": \"8182\",\"items\":[";
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateNowStr = sdf.format(date);
                String s = "";
                mGoodsData.remove(0);
                for (int i = 0; i < mGoodsData.size(); i++) {
                    GoodsInfo goodsInfo = mGoodsData.get(i);
                    String real = goodsInfo.getReal();
                    if ("".equals(real) || "请点击修改".equals(real) || "0".equals(real)) {
                        mGoodsData.remove(i);
                    }
                }
                if (mGoodsData.size() == 0) {
                    ToastUtils.showToast(getContext(), "实作数都为0，无需提交");
                    return;
                }
                for (int i = 0; i < mGoodsData.size(); i++) {
                    GoodsInfo goodsInfo = mGoodsData.get(i);
                    String real = goodsInfo.getReal();
                    if ("".equals(real) || "请点击修改".equals(real)) {
                        real = "0";
                    }
                    s = "{\"fdate\":\"" + dateNowStr + "\",\"fid\":\"" + goodsInfo.getFid() + "\",\"fqty\":" + real + ",\"fbiller\":\"" + MyAppliaction.uerName + "\",\"fbiller2\":" + workPerId + ",\"fentryid\":\"" + goodsInfo.getFentryid() +
                            "\",\"fjyname\":\"" + goodsInfo.getFJYName() + "\",\"fsfsdgx\":\"" + goodsInfo.getFSFSDGX() + "\",\"fsfmdgx\":\"" + goodsInfo.getFSFMDGX() + "\",\"fsfddgx\":\"" + goodsInfo.getFSFDDGX();
                    if (i == mGoodsData.size() - 1) {
                        s = s + "\"}]}";
                    } else {
                        s = s + "\"},";
                    }
                    partArg = partArg + s;
                }
                new SubmitTask(partArg).execute();
                break;
            default:
                break;
        }
    }

    private void searchForData(String orderId) {
        //查询项目详情
        new ProjectTask(orderId, workProid).execute();
        //查询项目商品列表
        new ProGoodsTask(orderId).execute();
    }

    private void searchFromData(String goodsid) {
        int local = 0;
        for (int i = 0; i < mRecData.size(); i++) {
            String con = mRecData.get(i);
            if (goodsid.equals(con)) {
                local = i;
            }
        }
        if (local == 0) {
            ToastUtils.showToast(getContext(), "未在本项目中查找到该流转卡号");
        } else {
            local = local + 7;
            mRecData.set(local, "请点击修改");
            adapter.notifyDataSetChanged();
            rec_detail.scrollToPosition(local + 7);
        }
    }

    private void scanningCode(int kind) {
        //第二个参数是需要申请的权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            Intent intent = new Intent(getContext(), SaomiaoUIActivity.class);//这是一个自定义的扫描界面，扫描UI框放大了。
            //            Intent intent = new Intent(getContext(), CaptureActivity.class);
            if (kind == 0) {//查看项目单号
                startActivityForResult(intent, REQUEST_CODE0);
            } else {//1是流转卡（已删除）
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE0 || requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //获取商品id信息，跳转activity展示，在新的页面确定后添加到listview中
                    if (requestCode == REQUEST_CODE) {
                        //                        searchGoodsInfo(result, mEdit_goods_id, 1);
                    } else {
                        searchGoodsInfo(result, et_orderid, 0);
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void searchGoodsInfo(String proID, EditText et, int which) {
        ToastUtils.showToast(getContext(), "商品编码：" + proID);
        et.setText(proID);
        //根据id查询详情，项目id查询
        if (which == 0) {//传入的是项目id
            searchForData(proID);
        } else {//查询的是流水卡号
            searchFromData(proID);
        }
    }

    //查询所有工序
    class WorkProTask extends AsyncTask<Void, String, String> {
        String fuserno;
        String fuserpsw;

        WorkProTask(String fuserno, String fuserpsw) {
            this.fuserno = fuserno;
            this.fuserpsw = fuserpsw;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在加载工序");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("fnumber", "");
            map.put("fname", "");
            return SoapUtil.requestWebService(Consts.gongxu, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    WorkProceInfo info = gson.fromJson(jsonArray.get(i).toString(), WorkProceInfo.class);
                    if ("".equals(MyAppliaction.userType)) {
                        mListProce.add(info);
                    } else {
                        if (info.getFName().equals(MyAppliaction.userType)) {
                            mListProce.add(info);
                        }
                    }
                }
                workProceAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //查询工序职员
    class WorkPerTask extends AsyncTask<Void, String, String> {
        private String gx;

        WorkPerTask(String gx) {
            this.gx = gx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == mListPer) {
                mListPer = new ArrayList<>();
            } else {
                mListPer.clear();
            }
            WorkPerInfo workProinfo = new WorkPerInfo();
            workProinfo.setFname("请选择操作员");
            mListPer.add(workProinfo);
            ProgressDialogUtil.startShow(getContext(), "正在查询职员");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("fnumber", "");
            map.put("fname", "");
            map.put("fgongxu", gx);
            return SoapUtil.requestWebService(Consts.JA_zhiyuan, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    WorkPerInfo info = gson.fromJson(jsonArray.get(i).toString(), WorkPerInfo.class);
                    mListPer.add(info);
                }
                workPerAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //查询项目详情
    class ProjectTask extends AsyncTask<Void, String, String> {
        String forderbillno;
        String fgongxu;

        ProjectTask(String forderbillno, String fgongxu) {
            this.forderbillno = forderbillno;
            this.fgongxu = fgongxu;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), "正在查询项目详情");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("forderbillno", forderbillno);
            map.put("flzkno", "");
            map.put("fgongxu", fgongxu);
            map.put("fnumber", goodsNumber);
            map.put("fname", "");
            return SoapUtil.requestWebService(Consts.gongxuNo, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                mGoodsData.clear();
                mRecData.clear();
                adapter.notifyDataSetChanged();
                mBt_submit.setVisibility(View.GONE);
                GoodsInfo goodsInfo0 = new GoodsInfo();
                goodsInfo0.setGoodsid("流转卡号");
                goodsInfo0.setPicid("图号");
                goodsInfo0.setName("零件名称");
                goodsInfo0.setSpeci("规格");
                goodsInfo0.setFAuxQtyjh("总计划数");
                goodsInfo0.setPlannum("剩余计划数");
                goodsInfo0.setAccept("接收数");
                //                goodsInfo0.setAccept("待提交数");
                goodsInfo0.setReal("实做数");
                mGoodsData.add(goodsInfo0);
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    LiuZhuanInfo info = gson.fromJson(jsonArray.get(i).toString(), LiuZhuanInfo.class);
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setFid("" + info.getFID());
                    goodsInfo.setFentryid("" + info.getFEntryID());
                    goodsInfo.setGoodsid(info.getFBillNo());
                    goodsInfo.setPicid(info.getFNumber());
                    goodsInfo.setName(info.getFName());
                    goodsInfo.setSpeci(info.getFModel());
                    double fAuxQtyjh = info.getFAuxQtyjh();
                    double fAuxQty = info.getFAuxQty();//剩余计划数
                    goodsInfo.setFAuxQtyjh("" + fAuxQtyjh);//总计划数
                    goodsInfo.setPlannum("" + fAuxQty);//剩余计划数
                    // double hDone = info.getFAuxQtyjh() - fAuxQty;//已提交
                    if (mListProce.get(1).getFName().equals(workProid)) {
                        goodsInfo.setAccept("" + info.getFAuxQtyRecive());//接收数
                        goodsInfo.setReal("" + info.getFAuxQtyRecive());//应实作数
                    } else {
                        goodsInfo.setAccept("" + (info.getFAuxQtyRecive() - (fAuxQtyjh - fAuxQty)));//接收数
                        goodsInfo.setReal("" + (info.getFAuxQtyRecive() - (fAuxQtyjh - fAuxQty)));//应实作数
                    }
                    goodsInfo.setFJYName(info.getFJYName());
                    goodsInfo.setFSFSDGX(info.getFSFSDGX());
                    goodsInfo.setFSFMDGX(info.getFSFMDGX());
                    goodsInfo.setFSFDDGX(info.getFSFDDGX());
                    mGoodsData.add(goodsInfo);
                }
                if (mGoodsData.size() > 1) {
                    for (GoodsInfo goodsInfo1 : mGoodsData) {
                        String goodsid = goodsInfo1.getGoodsid();
                        String picid = goodsInfo1.getPicid();
                        String name = goodsInfo1.getName();
                        String speci = goodsInfo1.getSpeci();
                        String oriPlan = goodsInfo1.getFAuxQtyjh();
                        String plannum = goodsInfo1.getPlannum();
                        String accept = goodsInfo1.getAccept();
                        String real = goodsInfo1.getReal();
                        mRecData.add(goodsid);
                        mRecData.add(picid);
                        mRecData.add(name);
                        mRecData.add(speci);
                        mRecData.add(oriPlan);
                        mRecData.add(plannum);
                        mRecData.add(accept);
                        mRecData.add(real);
                    }
                    adapter.notifyDataSetChanged();
                    mBt_submit.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //查询项目商品
    class ProGoodsTask extends AsyncTask<Void, String, String> {
        String forderbillno;

        ProGoodsTask(String forderbillno) {
            this.forderbillno = forderbillno;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == mListGoods) {
                mListGoods = new ArrayList<>();
            } else {
                mListGoods.clear();
            }
            ProGoodsInfo goodsInfo = new ProGoodsInfo();
            goodsInfo.setFname("请选择商品名称");
            mListGoods.add(goodsInfo);
            proGoodsAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, Object> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("forderbillno", forderbillno);
            return SoapUtil.requestWebService(Consts.PRO_GOODS, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ProGoodsInfo goodsInfo = gson.fromJson(jsonArray.get(i).toString(), ProGoodsInfo.class);
                    mListGoods.add(goodsInfo);
                }
                proGoodsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //提交
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
            map.put("forderbillno", orderId);
            map.put("fgongxu", workProid);
            map.put("fnumber", goodsNumber);
            map.put("fname", "");
            return SoapUtil.requestWebService(Consts.submit, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            if (s.contains("成功")) {
                ToastUtils.showToast(getContext(), "提交成功");
                mRecData.clear();
                mGoodsData.clear();
                mBt_submit.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                JSONArray jsonArray;
                String message = "";
                try {
                    jsonArray = new JSONArray(s);
                    Gson gson = new Gson();
                    LoginInfo info = gson.fromJson(jsonArray.get(0).toString(), LoginInfo.class);
                    message = info.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ToastUtils.showToast(getContext(), "提交失败,错误信息：" + message);
            }
        }
    }
}
