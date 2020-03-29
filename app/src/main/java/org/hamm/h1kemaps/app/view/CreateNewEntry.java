package org.hamm.h1kemaps.app.view;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.hamm.h1kemaps.app.R;
import org.hamm.h1kemaps.app.database.MyDB;
import org.hamm.h1kemaps.app.util.DateFormatType;
import org.hamm.h1kemaps.app.util.MyFormat;
import org.hamm.h1kemaps.app.util.MyTimeGetter;

import java.io.File;
import java.util.Date;

import static org.hamm.h1kemaps.app.util.MyFormat.getTimeStr;
import static org.hamm.h1kemaps.app.util.MyFormat.myDateFormat;

public class CreateNewEntry extends MapActivity implements View.OnClickListener{
  private ImageButton mIbtn;
  private ImageButton mOre;
  private ImageButton mBack;
  private ImageButton mNext;
  private ImageView mTupian;
  private Bitmap mBitmap;
  protected static final int CHOOSE_PICTURE = 0;
  protected static final int TAKE_PICTURE = 1;
  protected static Uri tempUri;
  private static final int CROP_SMALL_PICTURE = 2;
  private DatePickerDialog dialogDate;
  private TimePickerDialog dialogTime;
  private AlertDialog.Builder dialog;

  private String createDate;//完整的创建时间，插入数据库
  private String dispCreateDate;//创建时间-显示变量可能会去除年份

  private Integer year;
  private Integer month;
  private Integer dayOfMonth;
  private Integer hour;
  private Integer minute;
  private boolean timeSetTag;
  private TextView editTime;
  private EditText editTitle;
  private EditText editBody;
  MyTimeGetter myTimeGetter;
  MyDB myDB;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_linear_layout);
    initUI();
    initListeners();
  }

  private void initUI() {
    mIbtn = (ImageButton) findViewById(R.id.btn_edit_menu_upcoming);
    mOre = (ImageButton) findViewById(R.id.btn_edit_menu_notice);
    editTitle = (EditText) findViewById(R.id.edit_title);
    editBody = (EditText)findViewById(R.id.edit_body);
    editTime = (TextView)findViewById(R.id.edit_title_time);
    mBack = (ImageButton) findViewById(R.id.button_back);
    mNext = (ImageButton) findViewById(R.id.button_save);
    mTupian = (ImageView) findViewById(R.id.edit_tupian);

    mBack.setOnClickListener(this);
    mNext.setOnClickListener(this);



    Date date = new Date(System.currentTimeMillis());
    createDate = myDateFormat(date,DateFormatType.NORMAL_TIME);
    dispCreateDate = getTimeStr(date);

    dialogDate = null;
    dialogTime = null;
    hour = 0;
    minute = 0;
    year = 0;
    month = 0;
    dayOfMonth = 0;
    timeSetTag = false;
    myDB = new MyDB(this);
  }

  private void initListeners() {
    mIbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showChoosePicDialog();
      }
    });
  }

  /**
   * 显示修改图片的对话框
   */
  protected void showChoosePicDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewEntry.this);
    builder.setTitle("添加图片");
    String[] items = {"选择本地照片", "拍照"};
    builder.setNegativeButton("取消", null);
    builder.setItems(items, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case CHOOSE_PICTURE: // 选择本地照片
            Intent openAlbumIntent = new Intent(
                    Intent.ACTION_GET_CONTENT);
            openAlbumIntent.setType("image/*");
            //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
            startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            break;
          case TAKE_PICTURE: // 拍照
            Intent openCameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "temp_image.jpg"));
            // 将拍照所得的相片保存到SD卡根目录
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
            break;
        }
      }
    });
    builder.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == TupianActivity.RESULT_OK) {
      switch (requestCode) {
        case TAKE_PICTURE:
          cutImage(tempUri); // 对图片进行裁剪处理
          break;
        case CHOOSE_PICTURE:
          cutImage(data.getData()); // 对图片进行裁剪处理
          break;
        case CROP_SMALL_PICTURE:
          if (data != null) {
            setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
          }
          break;
      }
    }
  }

  /**
   * 裁剪图片方法实现
   */
  protected void cutImage(Uri uri) {
    if (uri == null) {
      Log.i("alanjet", "The uri is not exist.");
    }
    tempUri = uri;
    Intent intent = new Intent("com.android.camera.action.CROP");
    //com.android.camera.action.CROP这个action是用来裁剪图片用的
    intent.setDataAndType(uri, "image/*");
    // 设置裁剪
    intent.putExtra("crop", "true");
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // outputX outputY 是裁剪图片宽高
    intent.putExtra("outputX", 150);
    intent.putExtra("outputY", 150);
    intent.putExtra("return-data", true);
    startActivityForResult(intent, CROP_SMALL_PICTURE);
  }

  /**
   * 保存裁剪之后的图片数据
   */
  protected void setImageToView(Intent data) {
    Bundle extras = data.getExtras();
    if (extras != null) {
      mBitmap = extras.getParcelable("data");
      //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
      mTupian.setImageBitmap(mBitmap);//显示图片
      //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...
    }
  }

  /*
   *  返回键监听，消除误操作BUG
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      String title;
      String body;
      String createDate;
      title = editTitle.getText().toString();
      body = editBody.getText().toString();
      createDate = editTime.getText().toString();
      //当返回按键被按下
      if (!isShowIng()){
        if (!"".equals(title)||!"".equals(body)){
          showDialog(title,body,createDate);
          clearDialog();
        } else {
          intentStart();
        }
      }
    }
    return false;
  }

  /*
   *  按钮点击事件监听
   *
   */
  @SuppressLint("SetTextI18n")
  //  @Override
  public void onClick(View v) {
    String title;
    String body;
    title = editTitle.getText().toString();
    body = editBody.getText().toString();
    switch (v.getId()){
      case R.id.button_save:
        if (saveFunction(title,body,createDate)){
          intentStart();
        }
        break;
      case R.id.button_back:
        if (!"".equals(title)||!"".equals(body)){
          showDialog(title,body,createDate);
          clearDialog();
        } else {
          intentStart();
        }
        break;

      case R.id.btn_edit_menu_upcoming:
        break;
      default:
        break;
    }
  }

  /*
   * 返回主界面
   */
  void intentStart(){
    Intent intent = new Intent(CreateNewEntry.this,OpenEntry.class);
    startActivity(intent);
    this.finish();
  }

  /*
   * 备忘录保存函数
   */
  boolean saveFunction(String title,String body,String createDate){

    boolean flag = true;
    if ("".equals(title)){
      Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
      flag = false;
    }
    if (title.length()>10){
      Toast.makeText(this,"标题过长",Toast.LENGTH_SHORT).show();
      flag = false;
    }
    if (body.length()>200){
      Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
      flag = false;
    }
    if ("".equals(createDate)){
      Toast.makeText(this,"时间格式错误",Toast.LENGTH_SHORT).show();
      flag = false;
    }

    if(flag){
      SQLiteDatabase db;
      ContentValues values;
      //  存储备忘录信息
      db = myDB.getWritableDatabase();
      values = new ContentValues();
      values.put(MyDB.RECORD_TITLE,title);
      values.put(MyDB.RECORD_BODY,body);
      values.put(MyDB.RECORD_TIME,createDate);
      if (timeSetTag){
        //  为当前备忘录添加提醒
        DatePicker datePicker = dialogDate.getDatePicker();
        String str = datePicker.getYear()+"-"+
                (datePicker.getMonth()+1)+"-"+
                datePicker.getDayOfMonth()+" "+
                MyFormat.timeFormat(hour,minute);
        values.put(MyDB.NOTICE_TIME,str);
      }
      db.insert(MyDB.TABLE_NAME_RECORD,null,values);
      Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
      db.close();
    }
    return flag;
  }

  /*
   * 弹窗函数
   * @param title
   * @param body
   * @param createDate
   */
  void showDialog(final String title, final String body, final String createDate){
    dialog = new AlertDialog.Builder(CreateNewEntry.this);
    dialog.setTitle("提示");
    dialog.setMessage("是否保存当前编辑内容");
    dialog.setPositiveButton("保存",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                saveFunction(title, body, createDate);
                intentStart();
              }
            });

    dialog.setNegativeButton("取消",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                intentStart();
              }
            });
    dialog.show();
  }

  /*
   *  清空弹窗
   */
  void clearDialog(){
    dialog = null;
  }

  /*
   *  判断是否弹窗是否显示
   */
  boolean isShowIng(){
    if (dialog!=null){
      return true;
    }else{
      return false;
    }
  }



}
