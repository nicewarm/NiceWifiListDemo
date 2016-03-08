package com.test.nice.nicewifilistdemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by pc on 2016/3/7.
 */
public class WifiController  {

    private WifiManager localWifiManager;//提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等
    //private List<ScanResult> wifiScanList;//ScanResult用来描述已经检测出的接入点，包括接入的地址、名称、身份认证、频率、信号强度等
    public List<WifiConfiguration> wifiConfigList;//WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
    private WifiInfo wifiConnectedInfo;//已经建立好网络链接的信息
    private WifiManager.WifiLock wifiLock;//手机锁屏后，阻止WIFI也进入睡眠状态及WIFI的关闭


    public WifiController( Context context){
        localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }


    //检查WIFI状态
    public int WifiCheckState(){
        return localWifiManager.getWifiState();
    }

    //开启WIFI
    public void WifiOpen(){
        if(!localWifiManager.isWifiEnabled()){
            localWifiManager.setWifiEnabled(true);
        }
    }

    //关闭WIFI
    public void WifiClose(){
        if(!localWifiManager.isWifiEnabled()){
            localWifiManager.setWifiEnabled(false);
        }
    }

    //扫描wifi
    public void WifiStartScan(){
        localWifiManager.startScan();
    }

    //得到Scan结果
    public List<ScanResult> getScanResults(){
        return localWifiManager.getScanResults();//得到扫描结果
    }

    //得到Wifi配置好的信息
    public void getConfiguration(){
        wifiConfigList = localWifiManager.getConfiguredNetworks();//得到配置好的网络信息
    }
    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
    public boolean IsConfiguration(String SSID) {
        String ssid = "\""+SSID + "\"";
        for(int i = 0; i < wifiConfigList.size(); i++) {
            if(ssid.equals(wifiConfigList.get(i).SSID)){//地址相同
                return true;
            }
        }
        return false;
    }

    //添加指定WIFI的配置信息,原列表不存在此SSID
    public int AddWifiConfig(String ssid,String pwd){
        int wifiId ;
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\""+ssid+"\"";//\"转义字符，代表"
        wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        wifiId = localWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
        return wifiId;
    }

    /***
     * 配置要连接的WIFI热点信息
     * @param SSID
     * @param password
     * @param type  加密类型
     * @return
     */
    public   int AddWifiInfo(String SSID, String password, int type) {

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        //增加热点时候 如果已经存在SSID 则将SSID先删除以防止重复SSID出现
        if (IsConfiguration(SSID)){
            localWifiManager.removeNetwork(getConfigurationNetWorkId(SSID));
        }

        // 分为三种情况：没有密码   用wep加密  用wpa加密
        if (type == 1) {   // WIFICIPHER_N//OPASS
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        } else if (type == 2) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 3) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return localWifiManager.addNetwork(config);
    }


    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
    public int getConfigurationNetWorkId(String SSID) {
        String ssid = "\""+SSID + "\"";
        for(int i = 0; i < wifiConfigList.size(); i++) {
            if(wifiConfigList.get(i).SSID.equals(ssid)){//地址相同
                return wifiConfigList.get(i).networkId;
            }
        }
        return -1;
    }

    public boolean removeNetWifi(int netId){
        return localWifiManager.removeNetwork(netId);
    }

    public int getConnectStatus(String SSID){
        String ssid = "\""+SSID + "\"";
        for(int i = 0; i < wifiConfigList.size(); i++) {
            if(ssid.equals(wifiConfigList.get(i).SSID)){//地址相同
                return wifiConfigList.get(i).status;
            }
        }
        return 100;
    }

    //连接指定Id的WIFI
    public boolean ConnectWifi(int wifiId){
        return localWifiManager.enableNetwork(wifiId, true);
    }


}
