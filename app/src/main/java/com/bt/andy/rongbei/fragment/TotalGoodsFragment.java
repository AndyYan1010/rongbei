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
import com.bt.andy.rongbei.adapter.SelectWorkProceAdapter;
import com.bt.andy.rongbei.adapter.TransferAdapter;
import com.bt.andy.rongbei.messegeInfo.GoodsInfo;
import com.bt.andy.rongbei.messegeInfo.LiuZhuanInfo;
import com.bt.andy.rongbei.messegeInfo.WorkProceInfo;
import com.bt.andy.rongbei.utils.Consts;
import com.bt.andy.rongbei.utils.ProgressDialogUtil;
import com.bt.andy.rongbei.utils.SoapUtil;
import com.bt.andy.rongbei.utils.ToastUtils;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
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
 * @创建时间 2018/5/23 16:39
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class TotalGoodsFragment extends Fragment implements View.OnClickListener {
    private View                   mRootView;
    private TextView               mTv_title;
    private Dialog                 dialog;
    private Spinner                spi_cho;
    private SelectWorkProceAdapter workProceAdapter;//工序选择适配器
    private String workProid = "";//记录工序
    private List<WorkProceInfo> mListProce;//记录工序
    private EditText            et_orderid, mEdit_goods_id;
    private TextView mTv_surema, tv_sure0;
    private ImageView img_scan0, mImg_scan;
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
        et_orderid = mRootView.findViewById(R.id.et_orderid);//输入项目id
        img_scan0 = mRootView.findViewById(R.id.img_scan0);//扫描
        mImg_scan = mRootView.findViewById(R.id.img_scan);//扫描
        mEdit_goods_id = mRootView.findViewById(R.id.edit_goods_id);//输入商品id
        tv_sure0 = mRootView.findViewById(R.id.tv_sure0);//确认输入的项目id
        mTv_surema = mRootView.findViewById(R.id.tv_surema);//确认输入的流转卡id
        rec_detail = mRootView.findViewById(R.id.rec_detail);
        mBt_submit = mRootView.findViewById(R.id.bt_submit);//总表提交服务器
    }

    private void initData() {
        mTv_title.setText("工作汇报");
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mRecData = new ArrayList();
        mGoodsData = new ArrayList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 7);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 7 == 0 || position % 7 == 2) {
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        rec_detail.setLayoutManager(gridLayoutManager);
        adapter = new TransferAdapter(getContext(), mRecData, mGoodsData);
        rec_detail.setAdapter(adapter);
        img_scan0.setOnClickListener(this);
        tv_sure0.setOnClickListener(this);
        mImg_scan.setOnClickListener(this);
        mTv_surema.setOnClickListener(this);
        mBt_submit.setOnClickListener(this);
        if (mGoodsData.size() == 0) {
            mBt_submit.setVisibility(View.GONE);
        }
        //查询所有工序
        new WorkProTask("", "").execute();
    }

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
            case R.id.tv_sure0:
                String orderId = String.valueOf(et_orderid.getText()).trim();
                if (null == orderId || "".equals(orderId) || "项目单号".equals(orderId)) {
                    ToastUtils.showToast(getContext(), "请输入项目单号");//SEORD000005
                    return;
                }
                //TODO:查询项目
                searchForData(orderId);
                break;
            case R.id.tv_surema:
                String orderId1 = String.valueOf(et_orderid.getText()).trim();
                if (null == orderId1 || "".equals(orderId1) || "项目单号".equals(orderId1)) {
                    ToastUtils.showToast(getContext(), "请先输入查找项目单号");
                    return;
                }
                String goodsid = String.valueOf(mEdit_goods_id.getText()).trim();
                if (null == goodsid || "".equals(goodsid) || "流转卡条码".equals(goodsid)) {
                    ToastUtils.showToast(getContext(), "请输入流转卡条码");
                    return;
                }
                //TODO:跳转流转卡号对应的内容
                searchFromData(goodsid);
                break;
            case R.id.bt_submit:
                //TODO:提交总表到服务器
                // {"passid": "8182","items":[{"fdate":"2018-07-18","fscrwd_billno":"WORK000006","fqty":2,"fbiller":"morningstar","fopersn":"02"}]}

                String partArg = "{\"passid\": \"8182\",\"items\":[";
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateNowStr = sdf.format(date);
                String s = "";
                mGoodsData.remove(0);
                for (int i = 0; i < mGoodsData.size(); i++) {
                    GoodsInfo goodsInfo = mGoodsData.get(i);
                    String real = goodsInfo.getReal();
                    if ("".equals(real) || "请点击修改".equals(real)) {
                        real = "0";
                    }
                    if (i == mGoodsData.size() - 1) {
                        s = "{\"fdate\":\"" + dateNowStr + "\",\"fscrwd_billno\":\"" + goodsInfo.getFicmobillno() + "\",\"fqty\":" + real + ",\"fbiller\":\"" + MyAppliaction.uerName + "\",\"fopersn\":\"" + goodsInfo.getFgongxuno() + "\"}]}";
                    } else {
                        s = "{\"fdate\":\"" + dateNowStr + "\",\"fscrwd_billno\":\"" + goodsInfo.getFicmobillno() + "\",\"fqty\":" + real + ",\"fbiller\":\"" + MyAppliaction.uerName + "\",\"fopersn\":\"" + goodsInfo.getFgongxuno() + "\"},";
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
        new ProjectTask(orderId, workProid).execute();
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
        }
        local = local + 6;
        mRecData.set(local, "请点击修改");
        adapter.notifyDataSetChanged();
        rec_detail.scrollToPosition(local + 6);
    }

    private void scanningCode(int kind) {
        //第二个参数是需要申请的权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            Intent intent = new Intent(getContext(), CaptureActivity.class);
            if (kind == 0) {
                startActivityForResult(intent, REQUEST_CODE0);
            } else {
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
                        searchGoodsInfo(result, mEdit_goods_id, 1);
                    } else {
                        searchGoodsInfo(result, et_orderid, 0);
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void searchGoodsInfo(String goodsID, EditText et, int which) {
        ToastUtils.showToast(getContext(), "商品编码：" + goodsID);
        et.setText(goodsID);
        //根据id查询详情，项目id查询
        if (which == 0) {//传入的是项目id
            searchForData(goodsID);
        } else {//查询的是流水卡号
            searchFromData(goodsID);
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
            Map<String, String> map = new HashMap<>();
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
                    mListProce.add(info);
                }
                workProceAdapter.notifyDataSetChanged();
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
            Map<String, String> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("forderbillno", forderbillno);
            map.put("fgongxu", fgongxu);
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
                goodsInfo0.setPlannum("计划数");
                goodsInfo0.setAccept("接收数");
                goodsInfo0.setReal("实做数");
                mGoodsData.add(goodsInfo0);
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    LiuZhuanInfo info = gson.fromJson(jsonArray.get(i).toString(), LiuZhuanInfo.class);
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setGoodsid(info.getFlzkno());
                    goodsInfo.setPicid(info.getFnumber());
                    goodsInfo.setName(info.getFname());
                    goodsInfo.setSpeci(info.getFmodel());
                    goodsInfo.setPlannum("" + info.getFjhqty());
                    goodsInfo.setAccept("" + info.getFszqty());
                    goodsInfo.setReal("" + info.getFjhqty());
                    goodsInfo.setFicmobillno(info.getFicmobillno());
                    goodsInfo.setFgongxuno(info.getFgongxuno());
                    mGoodsData.add(goodsInfo);
                }
                if (mGoodsData.size() > 1) {
                    for (GoodsInfo goodsInfo1 : mGoodsData) {
                        String goodsid = goodsInfo1.getGoodsid();
                        String picid = goodsInfo1.getPicid();
                        String name = goodsInfo1.getName();
                        String speci = goodsInfo1.getSpeci();
                        String plannum = goodsInfo1.getPlannum();
                        String accept = goodsInfo1.getAccept();
                        String real = goodsInfo1.getReal();
                        mRecData.add(goodsid);
                        mRecData.add(picid);
                        mRecData.add(name);
                        mRecData.add(speci);
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
            Map<String, String> map = new HashMap<>();
            map.put("passid", "8182");
            map.put("FJSONMSG", FJSONMSG);
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
            }
        }
    }
}
