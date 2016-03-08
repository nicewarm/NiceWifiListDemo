package com.test.nice.nicewifilistdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listView;
    private List<ScanResult> wifiResultList = new ArrayList<>();
    private WifiController wifiController;
    private WifiListAdapter adapter;
    private WIFIReceiver wifiReceiver;
    public static final String BaiMiLife = "百米生活";
    private String tempSSID ;
    private boolean isConnectOK = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        wifiController = new WifiController(MainActivity.this);
        adapter = new WifiListAdapter();
        listView.setAdapter(adapter);
        Log.d("nice_wifi","scan  onCreate");
        scan();
    }

    @Override
    protected void onStart() {
        super.onStart();
        wifiReceiver = new WIFIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wifiReceiver!=null){
            unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
    }

    private void scan(){
        wifiResultList.clear();
        wifiController.WifiOpen();
        wifiController.WifiStartScan();
        List<ScanResult> resultList= wifiController.getScanResults();
        wifiController.getConfiguration();
        for (ScanResult scanResult:resultList){
            if (!containsWifi(scanResult)){
                if (scanResult.SSID.equals(BaiMiLife)){
                    wifiResultList.add(0,scanResult);
                }else {
                    wifiResultList.add(scanResult);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean containsWifi(ScanResult scanResult){
        if (scanResult == null){
            return false;
        }
        for (ScanResult result:wifiResultList){
            if (result.capabilities.equals(scanResult.capabilities)&&result.SSID.equals(scanResult.SSID)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final WifiItemView itemView = (WifiItemView) view;
        int netWorkId = wifiController.getConfigurationNetWorkId(wifiResultList.get(position).SSID);
        boolean isFree = wifiResultList.get(position).capabilities.equals(WifiItemView.freeWifiKey);
        if (isFree&&netWorkId ==-1){
            int freeNetWorkId = wifiController.AddWifiInfo(wifiResultList.get(position).SSID, "", 1);
            wifiController.ConnectWifi(freeNetWorkId);
            itemView.setConnecting();
        }else {
            if (netWorkId != -1){
                wifiController.ConnectWifi(netWorkId);
                itemView.setConnecting();
            }else {
                WifiPswDialog pswDialog = new WifiPswDialog(MainActivity.this,new WifiPswDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String wifiPassword) {
                        // TODO Auto-generated method stub
                        if(wifiPassword != null){
                            int netId = wifiController.AddWifiConfig(wifiResultList.get(position).SSID, wifiPassword);
                            if(netId != -1){
                                wifiController.getConfiguration();//添加了配置信息，要重新得到配置信息
                                wifiController.ConnectWifi(netId);
                                itemView.setConnecting();
                            }
                        }

                    }
                },wifiResultList.get(position).SSID);
                pswDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                pswDialog.show();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int netWorkId = wifiController.getConfigurationNetWorkId(wifiResultList.get(position).SSID);
        wifiController.removeNetWifi(netWorkId);
        wifiController.getConfiguration();
        return false;
    }

    private class WifiListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return wifiResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return wifiResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WifiItemView itemView ;
            ScanResult scanResult = wifiResultList.get(position);
            if (convertView == null){
                itemView = new WifiItemView(MainActivity.this);
            }else {
                itemView = (WifiItemView) convertView;
            }
            itemView.setData(scanResult,wifiController,tempSSID,isConnectOK);
            return itemView;
        }
    }


    private class WIFIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            tempSSID = "";
            if (info.getSupplicantState() == SupplicantState.COMPLETED){
                tempSSID = info.getSSID();
                isConnectOK = true;
                Log.d("nice_wifi","scan  COMPLETED");
                scan();
            }
            if (info.getSupplicantState() == SupplicantState.DISCONNECTED){
                tempSSID = info.getSSID();
                Log.d("nice_wifi","scan  DISCONNECTED");
                isConnectOK = false;
                scan();
            }
            Log.d("nice_wifi",info.getSupplicantState()+"   "+info.getSSID());
        }
    }

}
