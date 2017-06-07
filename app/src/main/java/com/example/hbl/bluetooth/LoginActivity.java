package com.example.hbl.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editYz)
    EditText editYz;
    @BindView(R.id.btnYz)
    Button btnYz;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnYz, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnYz:
                break;
            case R.id.btnLogin:
                enter();
                break;
        }
    }
    private void enter(){
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
