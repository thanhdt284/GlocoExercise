package com.dpanic.glocoexercise;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dpanic.glocoexercise.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ItemViewHolder>{

    private Context mContext;
    private ArrayList<User> mDataList;

    DataAdapter(Context context, ArrayList<User> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvName.setText(mDataList.get(position).getName());
        holder.tvAddress.setText(mDataList.get(position).getAddress().getFullAddress());
        holder.tvEmail.setText(mDataList.get(position).getEmail());
        holder.tvMobilePhone.setText(mDataList.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_email)
        TextView tvEmail;
        @BindView(R.id.tv_mobile_phone)
        TextView tvMobilePhone;
        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
