package org.hamm.h1kemaps.app.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.hamm.h1kemaps.app.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;


public class fatieActivity extends MapActivity {

  private ImageView ivBQ, ivGetPhoto, ivCode;
  private LinearLayout lin;
  private boolean isKeyBoardShow = false, isBQViewShow = false;
  private boolean isADJUST_PAN = false, isADJUST_RESIZE = false;
  private View bqView;
  private EditText etContent;
  private ImageView ivPic;
  RelativeLayout relPic;

  private int SELECT_PICTURE = 1; // 从图库中选择图片
  private int SELECT_CAMER = 2; // 用相机拍摄照片
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setADJUST_RESIZE();
    setContentView(R.layout.activity_fatie);
    etContent = (EditText) findViewById(R.id.wpost_et);
    ivPic = (ImageView) findViewById(R.id.wpost_img);
    relPic = (RelativeLayout) findViewById(R.id.wpost_imglayout);
    findViewById(R.id.wpost_remimg).setOnClickListener(
            new OnClickListener() {

              @Override
              public void onClick(View v) {
                relPic.setVisibility(View.GONE);
              }
            });

    findViewById(R.id.wpost_getimg).setOnClickListener(
            new OnClickListener() {

              @Override
              public void onClick(View v) {
                if (isKeyBoardShow) {
                  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.toggleSoftInput(0,
                          InputMethodManager.HIDE_NOT_ALWAYS);
                }
                showSetHeadimg();
              }
            });

    final TextView tvWordNum = (TextView) findViewById(R.id.wpost_wordnum);
    final int wordnum = 500;
    etContent.addTextChangedListener(new TextWatcher() {
      int con = 0;
      CharSequence c;

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                int arg3) {
        con = arg0.length();
        c = arg0;
        tvWordNum.setText((wordnum - con) + "");
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1,
                                    int arg2, int arg3) {
      }

      @Override
      public void afterTextChanged(Editable arg0) {
        if (con > wordnum) {
          c = c.subSequence(0, wordnum);
          etContent.setText(c.toString());
          etContent.setSelection(wordnum);
        }
      }
    });
    bqView = findViewById(R.id.wpost_bqview);
    bqView.setVisibility(View.GONE);
    lin = (LinearLayout) findViewById(R.id.wpos_layout);
//		lin.addView(bqView);
    ivBQ = (ImageView) findViewById(R.id.wpost_bq);
    ivBQ.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isKeyBoardShow && !isBQViewShow) {// 键盘隐藏，选择表情
          imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
          setADJUST_PAN();
          isKeyBoardShow = false;
          isBQViewShow = true;
        } else if (isBQViewShow && !isKeyBoardShow) {// 键盘显示
          imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
          setADJUST_RESIZE();
          isKeyBoardShow = true;
          isBQViewShow = false;
        } else if (!isBQViewShow && !isKeyBoardShow) {// 键盘隐藏，选择表情
          setADJUST_PAN();
          showBQView();
          isKeyBoardShow = false;
          isBQViewShow = true;
        }
      }
    });
    lin.getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {

              @Override
              public void onGlobalLayout() {
                int n = lin.getRootView().getHeight() - lin.getHeight();
                if (n > 100) {// 软键盘已弹出
                  if (isBQViewShow) {

                    setADJUST_RESIZE();
                    hideBQView();
                    isBQViewShow = false;
                  }
                  isKeyBoardShow = true;
                } else {// 软键盘未弹出
                  isKeyBoardShow = false;
                  if (isADJUST_PAN) {
                    setADJUST_RESIZE();
                  }
                }

                if (isKeyBoardShow && !isBQViewShow) {// 键盘隐藏，选择表情
                  hideBQView();
                } else if (isBQViewShow && !isKeyBoardShow) {// 键盘显示
                  showBQView();
                } else if (!isBQViewShow && !isKeyBoardShow) {// 键盘隐藏，选择表情
                  hideBQView();
                }

              }
            });

  }

  @Override
  protected void onResume() {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        // InputMethodManager inputManager = (InputMethodManager)
        // etContent
        // .getContext().getSystemService(
        // Context.INPUT_METHOD_SERVICE);
        // inputManager.showSoftInput(etContent, 0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

        // // InputMethodManager imm = (InputMethodManager)
        // // getSystemService(Context.INPUT_METHOD_SERVICE);
        // // imm.toggleSoftInput(0,
        // InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }, 300);
    super.onResume();
  }

  private void hideBQView() {
    bqView.setVisibility(View.GONE);
    ivBQ.setImageResource(R.drawable.face);
  }

  private void showBQView() {
    bqView.setVisibility(View.VISIBLE);
    ivBQ.setImageResource(R.drawable.key);

  }

  private void setADJUST_PAN() {
    isADJUST_RESIZE = false;
    isADJUST_PAN = true;
    getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

  }

  private void setADJUST_RESIZE() {
    isADJUST_RESIZE = true;
    isADJUST_PAN = false;
    getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

  }

  public void cancle(View v) {
    finish();
  }

  public void button(View v) {

  }

  /**
   * 设置添加屏幕的背景透明度
   *
   * @param bgAlpha
   */
  public void backgroundAlpha(float bgAlpha) {
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.alpha = bgAlpha; // 0.0-1.0
    getWindow().setAttributes(lp);
  }

  private void showSetHeadimg() {
    final PopupWindow popupWindow = new PopupWindow(this);
    View v = (LinearLayout) LayoutInflater.from(this).inflate(
            R.layout.dialog_changetx, null);
    popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setFocusable(true);
    popupWindow.setBackgroundDrawable(getResources().getDrawable(
            android.R.color.transparent));
    popupWindow.setContentView(v);
    popupWindow.setAnimationStyle(R.style.AnimationPreview);
    popupWindow.showAtLocation(new View(this), Gravity.BOTTOM, 0, 0);
    backgroundAlpha(0.35f);
    popupWindow.setOnDismissListener(new OnDismissListener() {

      @Override
      public void onDismiss() {
        backgroundAlpha(1f);
      }
    });
    v.findViewById(R.id.tx_camera).setOnClickListener(
            new OnClickListener() {

              @Override
              public void onClick(View v) {
                popupWindow.dismiss();
                // 进入相机
                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                        getExternalCacheDir(), "edtimg.jpg")));
                startActivityForResult(intent, SELECT_CAMER);
              }
            });
    v.findViewById(R.id.tx_photo).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        popupWindow.dismiss();
        // 进入图库
        Intent intent = new Intent(
                Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
      }
    });
    v.findViewById(R.id.tx_cancle).setOnClickListener(
            new OnClickListener() {

              @Override
              public void onClick(View v) {
                popupWindow.dismiss();
              }
            });

  }

  @Override
  protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
    if (resultCode == RESULT_OK) {
      Thread thread=new Thread(new Runnable() {

        @Override
        public void run() {
          Bitmap bitmap = null;
          if (requestCode == SELECT_CAMER) {
            bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/edtimg.jpg");
          } else if (requestCode == SELECT_PICTURE) {
            Uri uri = data.getData();
            ContentResolver cr = fatieActivity.this.getContentResolver();
            try {
              bitmap = BitmapFactory
                      .decodeStream(cr.openInputStream(uri));
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            }
          }
          Message msg=new Message();
          msg.obj=ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
          handler.sendMessage(msg);


          BitmapUtil.comp(bitmap);
//						Message msg2=new Message();
//						msg2.obj=bitmap;
//						handler.sendMessage(msg2);
        }
      });

      thread.start();

    } else {
      Toast.makeText(this, "选择图片失败,请重新选择", Toast.LENGTH_SHORT).show();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }


  Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
      Bitmap bitmap=(Bitmap) msg.obj;
      relPic.setVisibility(View.VISIBLE);
      ivPic.setImageBitmap(bitmap);
    }
  };

}
