package com.example.hbl.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hbl.bluetooth.network.ToastUtil;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hbl on 2017/9/25.
 */

public class MsgFragment extends BaseFragment {

    FragmentActivity activity;
    @BindView(R.id.btnSure)
    Button btnSure;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.editup)
    EditText editup;
    @BindView(R.id.editdown)
    EditText editdown;
    @BindView(R.id.edittime)
    EditText edittime;
    Unbinder unbinder;
    ArrayAdapter<String> adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        new Thread() {
            @Override
            public void run() {
                try {
                    names = EMClient.getInstance().contactManager().getAllContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, names);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (names.isEmpty()) return;
                mCurrentName = names.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                System.out.println(username);
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {

            }

            @Override
            public void onFriendRequestDeclined(String s) {

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
            }
        });
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                try {
                    names = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(names);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String mCurrentName;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }

    private List<String> names = new ArrayList<>();


    @OnClick(R.id.btnSure)
    public void onClick() {
        startActivity(new Intent(activity, AddUserActivity.class));
    }

    @OnClick(R.id.btn_send)
    public void setInfo() {
        String upmsg = editup.getText().toString().trim();
        String downmsg = editdown.getText().toString().trim();
        String timemsg = edittime.getText().toString();
        if (TextUtils.isEmpty(mCurrentName)) return;
//        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String msg="衣加热强度： "+upmsg+"\n"+
                "裤加热强度： "+downmsg+"\n"+
                "加热时长： "+timemsg;
        EMMessage cmdMsg = EMMessage.createTxtSendMessage(msg, mCurrentName);
        //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
//        cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
//        String toUsername = "test1";//发送给某个人
//        cmdMsg.setAttribute("up", upmsg);
//        cmdMsg.setAttribute("down", downmsg);
//        cmdMsg.setAttribute("time", timemsg);
//        String action="action1";//action可以自定义

        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        ToastUtil.show("发送成功");


    }
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            for(EMMessage mes:messages){
//                System.out.println(mes.toString());
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      ToastUtil.show(mes.getBody().toString());
                  }
              });
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            for(EMMessage mes:messages){
                System.out.println(mes.toString());
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
            for(EMMessage mes:messages){
                System.out.println(mes.toString());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
            for(EMMessage mes:message){
                System.out.println(mes.toString());
            }
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
            for(EMMessage mes:messages){
                System.out.println(mes.toString());
            }

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动

        }
    };
}
