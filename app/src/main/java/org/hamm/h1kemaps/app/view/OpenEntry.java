package org.hamm.h1kemaps.app.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamm.h1kemaps.app.database.MyDB;
import org.hamm.h1kemaps.app.enity.Record;
import org.hamm.h1kemaps.app.util.DateFormatType;

import static org.hamm.h1kemaps.app.util.MyFormat.*;
import static org.hamm.h1kemaps.app.util.MyFormat.getTimeStr;
import static org.hamm.h1kemaps.app.util.MyFormat.myDateFormat;


import android.app.AlertDialog;


import org.hamm.h1kemaps.app.R;



/**
 * create_by Android Studio
 *
 * @author zouguo0212@
 * @package_name fun.zzti
 * @description
 * @date 2018/10/26 17:30
 */
public class OpenEntry extends MapActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private final static String TAG = "OpenEntry";

    MyDB myDB;
    private ListView myListView;
    private Button createButton;
    private MyBaseAdapter myBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_linear_layout);
        init();
    }

    //初始化控件
    private void init(){
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(this);

        myListView =(ListView) findViewById(R.id.list_view);

        List<Record> recordList = new ArrayList<Record>();
        myDB = new MyDB(this);
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = db.query(MyDB.TABLE_NAME_RECORD,null,
                null,null,null,
                null,MyDB.NOTICE_TIME+","+MyDB.RECORD_TIME+" DESC");
        if(cursor.moveToFirst()){
            Record record;
            while (!cursor.isAfterLast()){
                record = new Record();
                record.setId(
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(MyDB.RECORD_ID))));
                record.setTitleName(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_TITLE))
                );
                record.setTextBody(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_BODY))
                );
                record.setCreateTime(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_TIME)));
                record.setNoticeTime(
                        cursor.getString(cursor.getColumnIndex(MyDB.NOTICE_TIME)));
                recordList.add(record);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // 创建一个Adapter的实例
        myBaseAdapter = new MyBaseAdapter(this,recordList,R.layout.list_item2);
        myListView.setAdapter(myBaseAdapter);
        // 设置点击监听
        myListView.setOnItemClickListener(this);
        myListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createButton:
                Intent intent = new Intent(OpenEntry.this, CreateNewEntry.class);
                startActivity(intent);
                OpenEntry.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(OpenEntry.this,AmendActivity.class);
        Record record = (Record) myListView.getItemAtPosition(position);
        intent.putExtra(MyDB.RECORD_TITLE,record.getTitleName().trim());
        intent.putExtra(MyDB.RECORD_BODY,record.getTextBody().trim());
        intent.putExtra(MyDB.RECORD_TIME,record.getCreateTime().trim());
        intent.putExtra(MyDB.RECORD_ID,record.getId().toString().trim());
        if (record.getNoticeTime()!=null) {
            intent.putExtra(MyDB.NOTICE_TIME, record.getNoticeTime().trim());
        }
        this.startActivity(intent);
        OpenEntry.this.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Record record = (Record) myListView.getItemAtPosition(position);
        showDialog(record,position);
        return true;
    }

    void showDialog(final Record record,final int position){

        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(OpenEntry.this);
        dialog.setTitle("是否删除？");
        String textBody = record.getTextBody();
        dialog.setMessage(
                textBody.length()>150?textBody.substring(0,150)+"...":textBody);
        dialog.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = myDB.getWritableDatabase();
                        db.delete(MyDB.TABLE_NAME_RECORD,
                                MyDB.RECORD_ID +"=?",
                                new String[]{String.valueOf(record.getId())});
                        db.close();
                        myBaseAdapter.removeItem(position);
                        myListView.post(new Runnable() {
                            @Override
                            public void run() {
                                myBaseAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }



    /**
     * ListView展示的适配器类
     */
    class MyBaseAdapter extends BaseAdapter {
        private List<Record> recordList;//数据集合
        private Context context;
        private int layoutId;

        public MyBaseAdapter(Context context, List<Record> recordList, int layoutId){
            this.context = context;
            this.recordList = recordList;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            if (recordList!=null&&recordList.size()>0)
                return recordList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if (recordList!=null&&recordList.size()>0)
                return recordList.get(position);
            else
                return null;
        }

        public void removeItem(int position){
            this.recordList.remove(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.list_item2, parent,
                        false);
                viewHolder  = new ViewHolder();
                viewHolder.titleView =(TextView) convertView.findViewById(R.id.list_item_title);
                viewHolder.bodyView = (TextView) convertView.findViewById(R.id.list_item_body);
                viewHolder.timeView = (TextView) convertView.findViewById(R.id.list_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Record record = recordList.get(position);
            String tile = record.getTitleName();
            viewHolder.titleView.setText((position+1)+"."+(tile.length()>7?tile.substring(0,7)+"...":tile));
//            viewHolder.titleView.setText(tile);
            String body = record.getTextBody();
            viewHolder.bodyView.setText(body.length()>13?body.substring(0,12)+"...":body);
//            viewHolder.bodyView.setText(body);
            String createTime = record.getCreateTime();
            Date date = myDateFormat(createTime,DateFormatType.NORMAL_TIME);
            viewHolder.timeView.setText(getTimeStr(date));
            return convertView;
        }
    }

    /**
     * ListView里的组件包装类
     */
    class ViewHolder{
        TextView titleView;
        TextView bodyView;
        TextView timeView;
    }

}

