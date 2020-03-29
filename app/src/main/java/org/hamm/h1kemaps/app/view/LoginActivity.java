package org.hamm.h1kemaps.app.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.hamm.h1kemaps.app.R;


public class LoginActivity extends Activity implements View.OnClickListener {
  public static final String TAG = "LoginActivity";

  public static LoginActivity pInstance;



  private SharedPreferences mUserData;

  private TextView tvYeek;
  private EditText etUsername;
  private EditText etPassword;
  private Button btnLogin;
  private Button btnRegister;
  private Button btnTest;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    pInstance=this;
    mUserData = getSharedPreferences("userdata",0);
    etUsername = (EditText) findViewById(R.id.et_username);
    etPassword = (EditText)findViewById(R.id.et_password);
    btnLogin =(Button) findViewById(R.id.btn_login);
    btnRegister = (Button) findViewById(R.id.btn_register);


    //激活和不激活
    btnLogin.setEnabled(false);
    btnLogin.setOnClickListener(this);
    btnRegister.setOnClickListener(this);





    //检查是否有效输入（主要是让登陆按钮变色）
    // new AppCheckUpdate(this,false);
    etUsername.setText(mUserData.getString("account",""));

    if(mUserData.getBoolean("autoLogin",false)){
      Toast.makeText(getApplicationContext(),"欢迎"+mUserData.getString("username","用户")+"使用",Toast.LENGTH_SHORT).show();
      startActivity(new Intent(LoginActivity.this,MapActivity.class));
      finish();
    }
    updateInput();


  }

  //按钮监听器（其实我觉得没必用做接口实现？）
  @Override
  public void onClick(View v) {
    switch (v.getId()) {

      case R.id.btn_login:
        if(getInput(0).equals(mUserData.getString("account",""))&&
                getInput(1).equals(mUserData.getString("password",""))){
          Toast.makeText(getApplicationContext(),"欢迎"+mUserData.getString("username","用户")+"使用",Toast.LENGTH_SHORT).show();
          startActivity(new Intent(LoginActivity.this,MapActivity.class));
          finish();
        }else {
          Toast.makeText(getApplicationContext(),"账号或密码有误！",Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.btn_register:
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        break;

      default:
        break;
    }
  }

  /**
   * getInput方法是用来获取用户名和密码的方法
   * 现在用于给按钮变色，未来给验证账号密码
   *
   * @param i 0为返回用户名，1为返回密码
   * @return 返回用户名或密码
   */
  private String getInput(int i) {
    String s = "";
    switch (i) {
      case 0:
        s = etUsername.getText().toString().trim();
        break;
      case 1:
        s = etPassword.getText().toString().trim();
        break;
      case 2:
        break;
      default:
        break;
    }
    return s;
  }

  //更新输入方法，目前只改变按钮样式，未来可能可能加入一键清除。（如果用自定义输入框（EditText）就不用这个方法了）
  private void updateInput() {
    //添加文字变化监听器，可以百度搜.后面的add……er了解。
    //https://www.cnblogs.com/Free-Thinker/p/6839276.html这个是我随便找的算详细吧。
    etUsername.addTextChangedListener(new TextWatcher() {
      //变化前的字符
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      //变化的字符
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        //如果输入框变化了，就改变按钮
        changedButton();
      }

      //变化后的字符
      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    //同上，因为可能先输密码再输账号，所以都要添加监听器
    etPassword.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        changedButton();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  //改变按钮方法

  /**
   * 把成功放在if，未知和失败放在else，这个自行理解，不懂问我。
   * 所以if里面装的是两个输入框都有内容（不为空）。利用isEmpty方法检查String是否为空（""）。
   * 如果是空值就返回true,所以在前面加个！就变成了空值时返回false   (！true等价于false)。
   * 任意一个是空值都得是false所以账号和密码用 | （或）并联。
   */
  private void changedButton() {
    if ((getInput(0).length()==11 && !getInput(1).isEmpty())) {
      //都不为空则则可点击（已激活）
      btnLogin.setEnabled(true);
    } else {
      //其他情况都不可点击（未激活）
      btnLogin.setEnabled(false);
    }

  }





  //以后用来检查账号的方法
  private boolean checkAccount() {
    return false;
  }
}
