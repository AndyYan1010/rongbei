package com.bt.andy.rongbei.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.CheckInfo;
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

public class RecyCheckAdapter extends BaseQuickAdapter<CheckInfo, BaseViewHolder> {
    private Context mContext;

    public RecyCheckAdapter(int layoutResId, Context context, List data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final CheckInfo item) {
        helper.setText(R.id.tv_date, "--");
        helper.setText(R.id.tv_fno, item.getFBillNo());
        helper.setText(R.id.tv_tnum, item.getFNumber());
        helper.setText(R.id.tv_fname, item.getFName());
        helper.setText(R.id.tv_ftype, item.getFModel());
        helper.setText(R.id.tv_fgx, item.getFGXName());
        helper.setText(R.id.tv_checkNum, "" + item.getFAuxQtyFinish());
        helper.setText(R.id.tv_fnumber, "" + item.getFHGQty());
        final EditText et_note = helper.getView(R.id.et_note);
        et_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String cont = String.valueOf(et_note.getText()).trim();
                item.setFnote(cont);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
