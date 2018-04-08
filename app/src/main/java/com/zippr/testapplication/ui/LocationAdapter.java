package com.zippr.testapplication.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zippr.testapplication.R;
import com.zippr.testapplication.models.SelLocDO;

import java.util.List;

/**
 * Created by aritrapal on 22/03/18.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private String TAG = "LocationAdapter";
    private List<SelLocDO> mValues;
    private Context context;

    public LocationAdapter(Context context, List<SelLocDO> items) {
        this.context = context;
        mValues = items;
    }

    public void refresh(List<SelLocDO> items) {
        this.mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if(!TextUtils.isEmpty(holder.mItem.name))
            holder.tvLocName.setText(holder.mItem.name);
        else
            holder.tvLocName.setText("");

        holder.tvPickupCount.setText(holder.mItem.parcelCount + "");
    }

    @Override
    public int getItemCount() {
        if(mValues != null)
            return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView tvLocName;
        public final TextView tvPickupCount;

        public SelLocDO mItem;

        public ViewHolder(View view) {
            super(view);
            mView               = view;
            tvLocName          = (TextView) view.findViewById(R.id.tvLocName);
            tvPickupCount           = (TextView) view.findViewById(R.id.tvPickupCount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvLocName.getText() + "'";
        }
    }
}
