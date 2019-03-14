package com.bt.andy.rongbei.adapter;

import android.content.Context;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.PlanGXInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/11/20 14:07
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class RecyPlanAdapter extends BaseQuickAdapter<PlanGXInfo, BaseViewHolder> {
    private Context mContext;

    public RecyPlanAdapter(int layoutResId, Context context, List data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PlanGXInfo item) {
        helper.setText(R.id.tv_cont, item.getFName());
        helper.setText(R.id.tv_compTime, null == item.getFDate2() ? "--" : item.getFDate2());
        helper.setText(R.id.tv_per, item.getFName1());
        helper.setText(R.id.tv_time, null == item.getFDate1() ? "--" : item.getFDate1());
        if (item.getChangeTimes() > 0) {
            helper.setTextColor(R.id.tv_time, mContext.getResources().getColor(R.color.red));
        } else {
            helper.setTextColor(R.id.tv_time, mContext.getResources().getColor(R.color.green_100));
        }
        //添加子控件的点击事件
        //        helper.addOnClickListener(R.id.tv_time);
    }
}
