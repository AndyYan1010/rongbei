package com.bt.andy.rongbei.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.andy.rongbei.R;
import com.bt.andy.rongbei.messegeInfo.SearchGXInfo;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2019/3/8 14:35
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvSearchGXDetailAdapter extends BaseAdapter {
    private Context                        mContext;
    private List<SearchGXInfo.OperidsBean> mList;

    public LvSearchGXDetailAdapter(Context context, List<SearchGXInfo.OperidsBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return null == mList ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder viewHolder;
        if (null == view) {
            viewHolder = new MyViewHolder();
            view = View.inflate(mContext, R.layout.lv_search_gx, null);
            viewHolder.tv_fgx = view.findViewById(R.id.tv_fgx);
            viewHolder.tv_fhbnum = view.findViewById(R.id.tv_fhbnum);
            viewHolder.tv_cz1 = view.findViewById(R.id.tv_cz1);
            viewHolder.tv_cz2 = view.findViewById(R.id.tv_cz2);

            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }

        viewHolder.tv_fgx.setText(mList.get(i).getFopernote());
        int hgnum = 0;
        for (SearchGXInfo.OperidsBean.OperidsEntryBean bean : mList.get(i).getOperidsEntry()) {
            hgnum = hgnum + bean.getFauxqtypass();
        }
        viewHolder.tv_fhbnum.setText("" + hgnum);
        viewHolder.tv_cz1.setText(mList.get(i).getOperidsEntry().get(0).getFworkname1());
        viewHolder.tv_cz2.setText(mList.get(i).getOperidsEntry().get(0).getFworkname2());
        return view;
    }

    class MyViewHolder {
        TextView tv_fgx, tv_fhbnum, tv_cz1, tv_cz2;
    }
}
