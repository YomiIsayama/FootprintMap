package org.hamm.h1kemaps.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;


import org.hamm.h1kemaps.app.R;
import org.hamm.h1kemaps.app.util.ImageLoader;

import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    protected List<String> mDatas;

    public  interface  OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);

    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public SimpleAdapter(Context mContext, List<String> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_tp_item, parent, false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ImageLoader.getInStance(3, ImageLoader.Type.LIFO).loadImage(mDatas.get(position),holder.iv);
        setUpOnClicl(holder);
    }

    public void setUpOnClicl(final MyViewHolder holder) {
        if (mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView,pos);
                    return false;
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv=  (ImageView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
