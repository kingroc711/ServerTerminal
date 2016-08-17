package club.com.serverterminal;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by kingroc on 16-8-11.
 */

public class NetWorkTools {
    public static final int mPort = 8091;

    static void sendBroadCastToCenter(){
        WifiManager wifiMgr = (WifiManager) App.getContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr == null){
            CNTrace.d("wifiMgr == null");
            return;
        }

        CNTrace.d("is Wifi enable : " + wifiMgr.isWifiEnabled());

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ips = Formatter.formatIpAddress(ip);
        int broadCastIP = ip | 0xFF000000;
        String broadCastIPs = Formatter.formatIpAddress(broadCastIP);
        CNTrace.d("ip : " + ips + ", broadCastIPs : " + broadCastIPs);

        DatagramSocket theSocket = null;
        try {
           // for (int i = 0; i < 256; i++) {
                InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
                //InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP | ((0xFF - i) << 24)));
                theSocket = new DatagramSocket();
                String data = "Hello";
                DatagramPacket theOutput = new DatagramPacket(data.getBytes(), data.length(), server, mPort);
                theSocket.send(theOutput);
            //}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (theSocket != null)
                theSocket.close();
        }
    }
}
