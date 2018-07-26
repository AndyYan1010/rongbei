package com.bt.andy.rongbei.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.GoodsInfo;
import com.bt.andy.rongbei.messegeInfo.WorkProceInfo;
import com.bt.andy.rongbei.utils.ToastUtils;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/19 10:50
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {
    private Context             mContext;
    private List<String>        mList;
    private List<GoodsInfo>     mGoodsList;
    private List<WorkProceInfo> mListProce;
    private String              mWorkProid;

    public TransferAdapter(Context context, List list, List<GoodsInfo> goodsist, List<WorkProceInfo> proce, String workProid) {
        this.mContext = context;
        this.mList = list;
        this.mGoodsList = goodsist;
        this.mListProce = proce;
        this.mWorkProid = workProid;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_genre, parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String s = mList.get(position);
        holder.tv_name.setText(s);

        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (position <= 7 || position % 8 != 7) {
                    return;
                }
                String sNum = mList.get(position - 3);//总计划数
                String surplusNum = mList.get(position - 2);//剩余计划数
                double num = Double.parseDouble(sNum);
                double levnum = Double.parseDouble(surplusNum);
                String sHDone = mList.get(position - 1);//接收数
                double rec = Double.parseDouble(sHDone);
                //弹出一个Dialog，修改数据
                showDialogToChange(holder.tv_name, num, levnum, rec, position);
            }
        });
    }

    private void showDialogToChange(final TextView tv_name, final double num, final double levnum, final double rec, final int position) {
        final EditText et = new EditText(mContext);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(mContext).setView(et).setTitle("填入")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //修改textview的内容
                        double wrNum = 0.0;
                        String content = String.valueOf(et.getText()).trim();
                        if (!content.equals("")) {
                            wrNum = Integer.parseInt(content);
                        }

                        if (wrNum > rec) {
                            ToastUtils.showToast(mContext, "实做数不能大于接收数");
                            return;
                        }

                        //                        if (mListProce.get(1).getFName().equals(mWorkProid)) {
                        //                            if (wrNum > num) {
                        //                                ToastUtils.showToast(mContext, "实做数不能大于计划数");
                        //                                return;
                        //                            }
                        //                        } else {
                        //                            if (wrNum > rec) {
                        //                                ToastUtils.showToast(mContext, "实做数不能大于接收数");
                        //                                return;
                        //                            }
                        //                        }

                        tv_name.setText(content);
                        mList.set(position, content);
                        GoodsInfo goodsInfo = mGoodsList.get(position / 8);
                        goodsInfo.setReal(content);
                    }
                }).setNegativeButton("取消", null).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
