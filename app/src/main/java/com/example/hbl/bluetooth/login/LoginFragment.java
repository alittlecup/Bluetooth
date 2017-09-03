package com.example.hbl.bluetooth.login;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hbl.bluetooth.App;
import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.interfaces.onFragmentClick;
import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.network.bean.CodeResponse;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hbl on 2017/9/2.
 */

public class LoginFragment extends BaseFragment {
    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editSms)
    EditText editSms;
    @BindView(R.id.btnSms)
    Button btnSms;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    private CountDownTimer timer;
    private String code="";
    private onFragmentClick clickListen;
    public static final String TAG=LoginFragment.class.getSimpleName();
    public static LoginFragment newInstance(onFragmentClick click){
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setFragmentClick(click);
        return loginFragment;
    }
    private void setFragmentClick(onFragmentClick click){
        this.clickListen=click;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_fragment;
    }

    @OnClick({R.id.btnSms, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSms:
                getCode();
                sendYZM();
                break;
            case R.id.btnLogin:
                if (editSms.getText().toString().trim().equals(code)) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    editSms.clearFocus();
                    clickListen.onFragmentClick(Action.Action_Login);
                } else {
                    ToastUtil.show("验证码不正确");
                }
                break;
        }
    }
    private void getCode() {
        RetrofitUtil.getService()
                .getCode(editPhone.getText().toString().trim())
                .enqueue(new DefaultCallback<CodeResponse>() {
                    @Override
                    public void onFinish(int status, CodeResponse body) {
                        if (status == DefaultCallback.SUCCESS) {
                            code = body.code;
                            App.tel = body.tel;
                            editSms.setText(body.code);
                        }
                        timer.cancel();
                        btnSms.setText("获取验证码");
                        btnSms.setEnabled(true);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void sendYZM() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int last = (int) (millisUntilFinished / 1000);
                if (btnSms.isEnabled()) {
                    btnSms.setEnabled(false);
                }
                btnSms.setText("验证码(" + last + ")");
            }

            @Override
            public void onFinish() {
                btnSms.setText("获取验证码");
                btnSms.setEnabled(true);
            }
        }.start();
    }
}
