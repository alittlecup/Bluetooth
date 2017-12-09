package com.example.hbl.bluetooth.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hbl.bluetooth.App;
import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.home.HomeActivity;
import com.example.hbl.bluetooth.interfaces.onFragmentClick;
import com.example.hbl.bluetooth.network.DefaultCallback;
import com.example.hbl.bluetooth.network.RetrofitUtil;
import com.example.hbl.bluetooth.network.ToastUtil;
import com.example.hbl.bluetooth.network.bean.CodeResponse;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

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
    @BindView(R.id.btnRegistry)
    Button btnRegistry;
    private CountDownTimer timer;
    private String code = "";
    private onFragmentClick clickListen;
    public static final String TAG = LoginFragment.class.getSimpleName();
    private boolean progressShow;

    public static LoginFragment newInstance(onFragmentClick click) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setFragmentClick(click);
        return loginFragment;
    }

    private void setFragmentClick(onFragmentClick click) {
        this.clickListen = click;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_fragment;
    }

    @OnClick({R.id.btnSms, R.id.btnLogin, R.id.btnRegistry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSms:
                getCode();
                sendYZM();
                break;
            case R.id.btnLogin:
//                if (editSms.getText().toString().trim().equals(code)) {
//                    if (timer != null) {
//                        timer.cancel();
//                    }
//                    editSms.clearFocus();
//                    clickListen.onFragmentClick(Action.Action_Login);
//                } else {
//                    ToastUtil.show("验证码不正确");
//                }
                login(btnLogin);
                break;
            case R.id.btnRegistry:
                clickListen.onFragmentClick(Action.Action_Regiest);
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

    @Override
    public void onStart() {
        super.onStart();
        boolean loggedInBefore = EMClient.getInstance().isLoggedInBefore();
        if(loggedInBefore){
            Intent intent = new Intent(getActivity(),
                    HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public void login(View view) {
//        if (!EaseCommonUtils.isNetWorkConnected(this)) {
//            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
//            return;
//        }
        String currentUsername = editPhone.getText().toString().trim();
        String currentPassword = editSms.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            ToastUtil.show(getResources().getString(R.string.User_name_cannot_be_empty));
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.show(getResources().getString(R.string.Password_cannot_be_empty));
            return;
        }

        progressShow = true;
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("TAG", "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
//        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
//        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d("TAG", "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d("TAG", "login: onSuccess");


                // ** manually load all local groups and conversation
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
//                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
//                        DemoApplication.currentUserNick.trim());
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }

                if (!getActivity().isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // get user's info (this should be get from App's server or 3rd party service)
//                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                if (timer != null) {
                    timer.cancel();
                }
                Intent intent = new Intent(getActivity(),
                        HomeActivity.class);
                startActivity(intent);

                getActivity().finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d("TAG", "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d("TAG", "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        ToastUtil.show(getResources().getString(R.string.Login_failed) + message);
                    }
                });
            }
        });
    }
}
