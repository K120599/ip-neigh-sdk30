package it.alessangiorgi.ipneigh30;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ArpNDK {

    private static final String TAG = "ArpNDK";
    private static final String ARPNDK_FAILED = "Arp table reading failed, are you using targetSdk 32 and an Android 13 device?";

    static {
        System.loadLibrary("ipneigh-android");
    }

    private static native int ARPFromJNI(int fd);

    public static ArrayList getARP() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> reachableDevices = new ArrayList<>();
        try {
            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor readSidePfd = pipe[0];
            ParcelFileDescriptor writeSidePfd = pipe[1];
            ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(readSidePfd);
            int fd_write = writeSidePfd.detachFd();
            int returnCode = ARPFromJNI(fd_write);

             if(returnCode != 0){
                 reachableDevices.add(ARPNDK_FAILED) ;
                return reachableDevices;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                Log.e(TAG, "getARP: "+line );
                stringBuilder.append(line).append("\n");
                String details = line;
                String[] words = details.split("\\s+");
                // Create an object to store the data
                DeviceDetails deviceInfo = new DeviceDetails();
                deviceInfo.ip = words[0];
                deviceInfo.device = words[1];
                deviceInfo.wlan = words[2];
                deviceInfo.lla = words[3];
                deviceInfo.mac = words[4];
                // deviceInfo.status = words[5];
 if(!words[4].equals("FAILED")){
                    reachableDevices.add(String.valueOf(deviceInfo));
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachableDevices;
    }

}
 class DeviceDetails {
    String ip;
    String device;
    String wlan;
    String lla;
    String mac;
   // String status;

    @Override
    public String toString() {
        return "{" +
                "ip='" + ip + '\'' +
                ", device='" + device + '\'' +
                ", wlan='" + wlan + '\'' +
                ", lla='" + lla + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
