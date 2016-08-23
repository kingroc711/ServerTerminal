package club.com.serverterminal;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
            InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
            theSocket = new DatagramSocket();
            theSocket.setSoTimeout(2000);
            String sendData = "Hello";
            DatagramPacket theOutput = new DatagramPacket(sendData.getBytes(), sendData.length(), server, mPort);
            theSocket.send(theOutput);

            DatagramPacket receivePacket =  new DatagramPacket(new byte[sendData.getBytes().length], sendData.getBytes().length);
            theSocket.receive(receivePacket);
            String s = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            CNTrace.d("addr : " + receivePacket.getAddress().getHostAddress() + ", port : " + receivePacket.getPort() + ", s : " + s);

            Intent i = new Intent(App.getContext(), MainService.class);
            i.putExtra("id", MainService.UPDATE_MSG);
            i.putExtra("ip", receivePacket.getAddress().getHostAddress());
            App.getContext().startService(i);
        } catch (UnknownHostException e){
            e.printStackTrace();
        }catch (SocketException e){
            e.printStackTrace();
        }catch (InterruptedIOException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(theSocket != null)
                theSocket.close();
        }
    }
}
