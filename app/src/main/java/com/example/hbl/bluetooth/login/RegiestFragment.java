//package com.example.hbl.bluetooth.login;
//
//import android.support.v4.app.Fragment;
//import android.app.ProgressDialog;
//import android.databinding.DataBindingUtil;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.hbl.bluetooth.R;
//import com.example.hbl.bluetooth.databinding.FragmentRegiestBinding;
//import com.hyphenate.EMError;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.exceptions.HyphenateException;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class RegiestFragment extends Fragment {
//
//    public RegiestFragment() {
//        // Required empty public constructor
//    }
//    FragmentRegiestBinding bind;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_regiest, container, false);
//        bind.setFragment(this);
//        return bind.getRoot();
//    }
//
//    public void onRegiestClick(View view){
//        final String username = bind.editPhone.getText().toString().trim();
//        final String pwd = bind.editPwd.getText().toString().trim();
//        String confirm_pwd = bind.editPwdConfim.getText().toString().trim();
//        if (TextUtils.isEmpty(username)) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
//            bind.editPhone.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(pwd)) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
//            bind.editPwd.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(confirm_pwd)) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
//            bind.editPwdConfim.requestFocus();
//            return;
//        } else if (!pwd.equals(confirm_pwd)) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
//            final ProgressDialog pd = new ProgressDialog(getActivity());
//            pd.setMessage(getResources().getString(R.string.Is_the_registered));
//            pd.show();
//
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        // call method in SDK
//                        EMClient.getInstance().createAccount(username, pwd);
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (!getActivity().isFinishing())
//                                    pd.dismiss();
//                                // save current user
//                                getActivity().finish();
//                            }
//                        });
//                    } catch (final HyphenateException e) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (!getActivity().isFinishing())
//                                    pd.dismiss();
//                                int errorCode=e.getErrorCode();
//                                if(errorCode== EMError.NETWORK_ERROR){
//                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
//                                }else if(errorCode == EMError.USER_ALREADY_EXIST){
//                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
//                                }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
//                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
//                                }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
//                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }
//            }).start();
//
//        }
//    }
//
//}
