package com.bt.andy.rongbei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.SearchGXInfo;
import com.bt.andy.rongbei.utils.MyNumUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/11/20 14:07
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class RecySearchAdapter extends BaseQuickAdapter<SearchGXInfo, BaseViewHolder> {
    private Context mContext;

    public RecySearchAdapter(int layoutResId, Context context, List data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SearchGXInfo item) {
        //添加子控件的点击事件
        //helper.addOnClickListener(R.id.line_write).addOnClickListener(R.id.tv_sure);
        helper.setText(R.id.tv_date, item.getFBillDate());
        helper.setText(R.id.tv_fno, item.getFOrderBillNO());
        helper.setText(R.id.tv_fcode, item.getFNumber());
        helper.setText(R.id.tv_fname, item.getFName());

        DecimalFormat df = new DecimalFormat(".00");
        helper.setText(R.id.tv_fpnum, df.format(MyNumUtils.getDoubleNum(item.getFPlanAuxQty())));

        ListView lv_gx = helper.getView(R.id.lv_gx);
        boolean isComp = true;
        for (int i = 0; i < item.getOperids().size(); i++) {
            SearchGXInfo.OperidsBean bean = item.getOperids().get(i);
            boolean isComp2 = true;
            for (SearchGXInfo.OperidsBean.OperidsEntryBean nextBean : bean.getOperidsEntry()) {
                if (!"1".equals(nextBean.getCompleted())) {
                    isComp = false;
                    isComp2 = false;
                }
            }
            if (isComp2) {
                item.getOperids().remove(i);
            }
        }
        if (!isComp) {
            lv_gx.setVisibility(View.VISIBLE);
            LvSearchGXDetailAdapter detailAdapter = new LvSearchGXDetailAdapter(mContext, item.getOperids());
            lv_gx.setAdapter(detailAdapter);
        } else {
            lv_gx.setVisibility(View.GONE);
        }
    }
}
