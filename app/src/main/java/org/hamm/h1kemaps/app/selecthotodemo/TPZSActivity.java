package org.hamm.h1kemaps.app.selecthotodemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.hamm.h1kemaps.app.R;
import org.hamm.h1kemaps.app.adapter.SimpleAdapter;

import java.util.List;


public class TPZSActivity extends Activity {
    private ImageView mIvSelect;
    private RecyclerView mListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpzs);
        initViews();
        initEvents();
        aboutIntent();
    }

    private void aboutIntent() {
        Intent intent = getIntent();
        List<String> photoSelect = (List<String>) intent.getSerializableExtra("photo");
        if (photoSelect!=null) {
            Log.e("mDatas",photoSelect.toString());
            final SimpleAdapter mAdapter = new SimpleAdapter(this, photoSelect);
            mListView.setAdapter(mAdapter);
            mListView.setLayoutManager(new GridLayoutManager(this,3));
        }
    }

    private void initViews() {
        mIvSelect= (ImageView) findViewById(R.id.iv_select);
        mListView=(RecyclerView)findViewById(R.id.rlv_list);
    }
    private void initEvents() {
        mIvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TPZSActivity.this,SelectPhotoActivity.class);
                startActivity(intent);
            }
        });
    }


}
