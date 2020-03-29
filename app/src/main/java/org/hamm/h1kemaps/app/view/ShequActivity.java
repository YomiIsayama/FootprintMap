package org.hamm.h1kemaps.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import org.hamm.h1kemaps.app.R;

import java.util.ArrayList;
import java.util.List;


public class ShequActivity extends MapActivity {
    private RecyclerView recyclerView;
    private List<String> mDatas;
    private ShequRecyclerAdapter recycleAdapter;
    private ImageButton btA;
    private ImageButton btB;
    private ImageButton btC;
    private ImageButton btD;
    private boolean isVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shequluntan);

        recyclerView = (RecyclerView) findViewById(R.id.list2);

        initData();
        recycleAdapter = new ShequRecyclerAdapter(ShequActivity.this, mDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(recycleAdapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//button扩展隐藏
        btA = (ImageButton) findViewById(R.id.btA);
        btB = (ImageButton) findViewById(R.id.btB);
        btC = (ImageButton) findViewById(R.id.btC);
        btD = (ImageButton) findViewById(R.id.btD);
        btB.setVisibility(View.INVISIBLE);
        btC.setVisibility(View.INVISIBLE);
        btD.setVisibility(View.INVISIBLE);
        btA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    btB.setVisibility(View.VISIBLE);
                    btC.setVisibility(View.VISIBLE);
                    btD.setVisibility(View.VISIBLE);
                    isVisible = false;
                } else {
                    btB.setVisibility(View.INVISIBLE);
                    btC.setVisibility(View.INVISIBLE);
                    btD.setVisibility(View.INVISIBLE);
                    isVisible = true;
                }
            }
        });
        //发帖页面
        btC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ShequActivity.this, fatieActivity.class));

            }
        });
        //系统分享
        btB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // （文字）
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                startActivity(Intent.createChooser(textIntent, "分享"));


             /*    // 多文件分享
                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                Uri uri1 = Uri.parse(getResourcesUri(R.drawable.ic_add_circle_black_24dp));
                Uri uri2 = Uri.parse(getResourcesUri(R.drawable.share));
                imageUris.add(uri1);
                imageUris.add(uri2);
                Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                mulIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(mulIntent, "多文件分享"));
 */

            }
        });
    }

    private void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 0; i < 40; i++) {
            mDatas.add("item" + i);
        }
    }

   /*
    //多文件分享getResourcesUri
    private String getResourcesUri(@DrawableRes int id) {
        Resources resources = getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        Toast.makeText(this, "Uri:" + uriPath, Toast.LENGTH_SHORT).show();
        return uriPath;
    }
*/

}