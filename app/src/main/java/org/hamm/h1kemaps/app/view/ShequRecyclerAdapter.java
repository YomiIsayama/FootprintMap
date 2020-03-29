package org.hamm.h1kemaps.app.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hamm.h1kemaps.app.R;

import java.util.List;


public class ShequRecyclerAdapter extends  RecyclerView.Adapter<ShequRecyclerAdapter.MyViewHolder> {

  private List<String> mDatas;
  private Context mContext;
  private LayoutInflater inflater;

  public ShequRecyclerAdapter(Context context, List<String> datas){
    this. mContext=context;
    this. mDatas=datas;
    inflater=LayoutInflater. from(mContext);
  }

  @Override
  public int getItemCount() {

    return mDatas.size();
  }

  //填充onCreateViewHolder方法返回的holder中的控件
  @Override
  public void onBindViewHolder(MyViewHolder holder, final int position) {

    holder.tv.setText( mDatas.get(position));
  }

  //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view = inflater.inflate(R.layout.shequliebiaorecylerview,parent, false);
    MyViewHolder holder= new MyViewHolder(view);
    return holder;
  }

  class MyViewHolder extends ViewHolder {

    TextView tv;

    public MyViewHolder(View view) {
      super(view);
      tv=(TextView) view.findViewById(R.id. item_number2);
    }

  }
}


