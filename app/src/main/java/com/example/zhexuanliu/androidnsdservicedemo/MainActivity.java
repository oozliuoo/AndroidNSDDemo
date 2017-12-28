package com.example.zhexuanliu.androidnsdservicedemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
{
    /**
     * Server socket button
     */
    @BindView(R.id.server_socket_btn)
    Button mServerSocketButton;

    /**
     * UDP socket button
     */
    @BindView(R.id.udp_socket_btn)
    Button mUDPSocketButton;

    /**
     * Track the context
     */
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.mContext = this;
    }

    @OnClick(R.id.server_socket_btn)
    public void onServerSocketButtonClick()
    {
        Intent intent = new Intent(MainActivity.this, TCPNSDActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.udp_socket_btn)
    public void onUDPSocketButtonClick()
    {
        Intent intent = new Intent(MainActivity.this, UDPNSDActivity.class);
        startActivity(intent);
        finish();
    }
}
