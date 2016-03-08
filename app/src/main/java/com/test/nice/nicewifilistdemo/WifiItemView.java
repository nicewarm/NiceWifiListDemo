package com.test.nice.nicewifilistdemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by pc on 2016/3/7.
 */
public class WifiItemView extends LinearLayout {


    TextView name;
    TextView statusView;
    private int status;//0 已经连接，2已经保存，1，没有配置
    private View freeConnect;
    private ProgressBar progressBar;
    private ImageView connectOk;
    private TextView connectFaild;
    public static final String freeWifiKey = "[ESS]";


    public WifiItemView(Context context) {
        super(context);
        initView(context);
    }

    public WifiItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wifi_item_view,this,true);
        name = (TextView) findViewById(R.id.name);
        statusView = (TextView) findViewById(R.id.status);
        freeConnect = findViewById(R.id.freeConnect);
        connectOk = (ImageView) findViewById(R.id.connectOk);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connectFaild = (TextView) findViewById(R.id.connectFaild);
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setConnecting(){
        freeConnect.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        connectOk.setVisibility(View.GONE);
    }

    public void setStatus(int status){
        this.status = status;
        if (status == 0){
            this.statusView.setText("已连接");
        }else if (status == 2){
            this.statusView.setText("已保存");
        }else if (status == 1){
            this.statusView.setText("不可用");
        }else {
            this.statusView.setText("没有配置");
        }
    }

    public void setData(ScanResult result,WifiController controller,String tempSSID,boolean isConnectOK){
        this.name.setText(result.SSID);
        this.setStatus(controller.getConnectStatus(result.SSID));
        Log.d("nice_wifi",tempSSID+"    ===     "+result.SSID);
        String ssidStr =  "\""+result.SSID + "\"";
        if (ssidStr.equals(tempSSID)){
            if (isConnectOK){
                connectOk.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                freeConnect.setVisibility(View.GONE);
                connectFaild.setVisibility(View.GONE);
            }else {
                freeConnect.setVisibility(View.GONE);
                connectOk.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                connectFaild.setVisibility(View.VISIBLE);
            }
        }else {
            if (result.capabilities.equals(freeWifiKey)){
                freeConnect.setVisibility(View.VISIBLE);
            }else {
                freeConnect.setVisibility(View.GONE);
            }
            connectOk.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            connectFaild.setVisibility(View.GONE);
        }
    }
}
