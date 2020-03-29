package org.hamm.h1kemaps.app.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.hamm.h1kemaps.app.R;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;



public class RegisterActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private int mCode;
    private String mCodeKey;


    private EditText etAccount;
    private EditText etPassword;
    private EditText etRePassword;
    private EditText etCode;
    private Button btnRegisterOK;
    private Button btnGetCode;
    private ImageButton imgBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //bmob SMS初始化
        BmobSMS.initialize(this, "9dff9e8da2b268024c3a94883d253ce8");


        etAccount = (EditText) findViewById(R.id.et_account);
        etCode = (EditText) findViewById(R.id.et_code);
        etPassword = (EditText) findViewById(R.id.et_password);
        etRePassword = (EditText) findViewById(R.id.et_repassword);
        btnRegisterOK = (Button) findViewById(R.id.btn_Register_OK);
        btnGetCode = (Button) findViewById(R.id.btn_getCode);
        imgBtnBack = (ImageButton) findViewById(R.id.imgbtn_back);

        //注册点击监听器
        imgBtnBack.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);
        btnRegisterOK.setOnClickListener(this);

        //注册输入监听器
        inputTextListener(etAccount);
        inputTextListener(etPassword);
        inputTextListener(etCode);
        inputTextListener(etRePassword);


    }


    /**
     * 获取输入框文本
     *
     * @param view 输入框控件
     * @return 输入框内容
     */
    private String getInput(EditText view) {
        return view.getText().toString().trim();
    }

    /**
     * 点击监听器
     * （个人写法，怎么舒服怎么来）
     */
    @Override
    public void onClick(View v) {
        String phone = etAccount.getText().toString();


        if (v == imgBtnBack) {
            finish();
        }

        if (v == btnGetCode) {
            if (checkAccount()) {

              /*
                 Random random = new Random();
                 for (mCode = 0; mCode < 999; ) {
                 mCode = (random.nextInt(10000));
                }

              */

                BmobSMS.requestSMSCode(this, phone, "足迹app", new RequestSMSCodeListener() {

                    @Override
                    public void done(Integer smsId, BmobException ex) {
                        // TODO Auto-generated method stub
                        if (ex == null) {//验证码发送成功
                            Log.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码发送失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                downTimer.start();
                btnGetCode.setEnabled(false);
                //记录获取验证码时的账号，之后进行验证
                mCodeKey = getInput(etAccount);


                //获取完验证码转到下一个输入框
                etCode.requestFocus();
            } else {
                Toast.makeText(RegisterActivity.this, "请检查你的手机号", Toast.LENGTH_SHORT).show();
            }
        }

        if (v == btnRegisterOK) {
            if (checkAccountData()) {
                //写入用户资料
                SharedPreferences.Editor editor = getSharedPreferences("userdata", 0).edit();
                editor.putString("account", getInput(etAccount));
                editor.putString("username", getInput(etAccount));
                editor.putString("password", getInput(etPassword));
                editor.putBoolean("autoLogin", true);
                editor.apply();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MapActivity.class));
                finish();
                //注册完后关闭登陆界面 加个if避免空对象崩溃。
                if (LoginActivity.pInstance != null) {
                    //在LoginActivity加个变量可以在其他Activity关闭LoginActivity
                    LoginActivity.pInstance.finish();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 输入文本监听器
     * (个人写法，怎么舒服怎么来)
     */
    private void inputTextListener(final EditText view) {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //每次输入都检查注册按钮是否可用
                changedButton();

                //对于账号框
                if (view == etAccount) {
                    //如果长度为11位则可以点击 获取验证码 按钮
                    if (getInput(etAccount).length() == 11) {
                        btnGetCode.setEnabled(true);
                    } else {
                        btnGetCode.setEnabled(false);
                    }
                }
                //对于验证码框
                if (view == etCode) {
                    //如果输入了正确的验证码 则焦点转到下一个输入框 （s要toString之后才能使用equals，不然equals会一直返回false）
                    if (s.length() == String.valueOf(mCode).length() & s.toString().equals(String.valueOf(mCode))) {
                        etPassword.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 改变按钮状态
     */
    private void changedButton() {
        //任意一个输入框为空则注册按钮不可用
        if (!(getInput(etAccount).isEmpty() ||
                getInput(etCode).isEmpty() ||
                getInput(etPassword).isEmpty() ||
                getInput(etRePassword).isEmpty())) {
            btnRegisterOK.setEnabled(true);
        } else {
            btnRegisterOK.setEnabled(false);
        }
    }

    /**
     * 检查账号名是否正确
     *
     * @return 账号名正确 返回 true
     */
    private boolean checkAccount() {
        //正则表达式
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        String account = getInput(etAccount);
        Matcher matcher = Pattern
                .compile(regex)
                .matcher(account);
        //返回查找结果
        return matcher.find();
    }

    /**
     * 验证码重新获取
     * millisInFuture 毫秒数
     * countDownInterval 返回时间间隔
     */
    private CountDownTimer downTimer = new CountDownTimer(10 * 1000, 1000) {

        //定期返回剩余值
        @Override
        public void onTick(long l) {
            btnGetCode.setText("已发送(" + (l / 1000) + "s)");
        }

        //完成时进行的方法
        @Override
        public void onFinish() {
            btnGetCode.setText("重新获取");
            btnGetCode.setEnabled(true);
        }
    };


    /**
     * 检查账号资料
     *
     * @return 如果通过检查 返回 true
     */
    private boolean checkAccountData() {
        boolean cancel = false;
        View focusView = null;
        String phone =etAccount.getText().toString();
        String code = etCode.getText().toString();
      /*
        //检查验证码与账号是否对应
        if (!(mCodeKey.equals(getInput(etAccount)) & getInput(etCode).equals(String.valueOf(mCode)))) {
            focusView = etCode;
            etCode.setError("你的验证码有误！");
            cancel = true;
        }
      */

        BmobSMS.verifySmsCode(this, phone, code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//短信验证码已验证成功
                    Log.i("bmob", "验证通过");
                } else {
                    Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                    Toast.makeText(RegisterActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //检查密码位数
        if (getInput(etPassword).length() < 6) {
            Toast.makeText(RegisterActivity.this, "你的密码小于6位，请及时更改", Toast.LENGTH_SHORT).show();

        }
        //检查密码输入是否正确
        if (!getInput(etPassword).equals(getInput(etRePassword))) {
            focusView = etRePassword;
            etRePassword.setError("你的两次密码不一样！");
            cancel = true;
        }

        //检查账号是否已存在
        if (!getSharedPreferences("userdata", 0).getString("account", "").isEmpty()) {
            focusView = etAccount;
            etAccount.setError("该账号已存在");
            cancel = true;
        }

        if (cancel) {
            // 如果出现错误；不尝试注册，并将焦点放到第一个错误字段上，返回 FALSE。
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }


}
