package it.alessangiorgi.ipneigh30;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArpNDK {

    private static final String TAG = "ArpNDK";
    private static final String ARPNDK_FAILED = "Arp table reading failed, are you using targetSdk 32 and an Android 13 device?";

    static {
        System.loadLibrary("ipneigh-android");
    }

    private static native int ARPFromJNI(int fd);

    public static ArrayList getARP() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Map> reachableDevices = new ArrayList<>();
        try {
            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor readSidePfd = pipe[0];
            ParcelFileDescriptor writeSidePfd = pipe[1];
            ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(readSidePfd);
            int fd_write = writeSidePfd.detachFd();
            int returnCode = ARPFromJNI(fd_write);

            if(returnCode != 0){
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error",ARPNDK_FAILED);
                reachableDevices.add(errorMap) ;
                return reachableDevices;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                Log.e(TAG, "getARP: "+line );
                stringBuilder.append(line).append("\n");
                // Create a map of the properties
                String[] words = line.split("\\s+");
                Map<String, String> deviceMap = new HashMap<>();
                deviceMap.put("ip", words[0]);
                deviceMap.put("device", words[1]);
                deviceMap.put("wlan", words[2]);
                deviceMap.put("lla", words[3]);
                deviceMap.put("mac", words[4]);
                reachableDevices.add(deviceMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachableDevices;
    }

}
