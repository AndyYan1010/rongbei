package com.bt.andy.rongbei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.SelectGoodsInfo;
import com.bt.andy.rongbei.utils.MyNumUtils;
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

public class RecySelectAdapter extends BaseQuickAdapter<SelectGoodsInfo, BaseViewHolder> {
    private Context               mContext;
    private List<SelectGoodsInfo> mData;
    private CheckBox              mCb_no;
    private CheckBox              mCb_all;

    public RecySelectAdapter(int layoutResId, Context context, List data, CheckBox cb_no, CheckBox cb_all) {
        super(layoutResId, data);
        this.mContext = context;
        this.mData = data;
        this.mCb_no = cb_no;
        this.mCb_all = cb_all;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SelectGoodsInfo item) {
        //添加子控件的点击事件
        //helper.addOnClickListener(R.id.line_write).addOnClickListener(R.id.tv_sure);
        helper.setText(R.id.tv_goodName, item.getFdate() + "  " + item.getFname() + item.getFmodel() + "--" + MyNumUtils.getDoubleNum(String.valueOf(item.getFQty())));
        final CheckBox ck_state = helper.getView(R.id.ck_state);
        ck_state.setChecked(item.isIsChecked());
        ck_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCb_no.setChecked(false);
                mCb_all.setChecked(false);
                mData.get(helper.getPosition()).setIsChecked(ck_state.isChecked());
            }
        });
    }
}
