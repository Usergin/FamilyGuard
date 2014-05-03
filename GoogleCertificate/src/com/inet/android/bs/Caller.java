package com.inet.android.bs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.inet.android.utils.Logging;

import android.util.Log;

/**
 * 
 * @author johny homicide
 *
 */
public class Caller {
	private final static String LOG_TAG = "Caller";

	/**
	 * Performs HTTP POST
	 */
	public static String doMake(String postRequest){
		String data = null;
		
		// �������� HttpClient � PostHandler
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://inp2.timespyder.com");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		Logging.doLog(LOG_TAG, "request:" + postRequest, "request:" + postRequest);

		nameValuePairs.add(new BasicNameValuePair("content", postRequest));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					"cp1251"));

			Logging.doLog(LOG_TAG, "doMake:" + EntityUtils.toString(httppost.getEntity()), 
					"3 - " + EntityUtils.toString(httppost.getEntity()));

			// �������� ������
			HttpResponse response = httpclient.execute(httppost);

			if (response != null) {
				try {
//					data = EntityUtils.toString(response.getEntity());
							
					HttpEntity httpEntity = response.getEntity();
							
					if(httpEntity != null){
						InputStream inputStream = httpEntity.getContent();
						data = convertStreamToString(inputStream);
					}
							
					Logging.doLog(LOG_TAG, "response: " + data, "response: " + data);

					if (data.indexOf("ANSWER") == -1) {
						Logging.doLog(LOG_TAG, "add line due to error in the answer", 
								"add line due to error in the answer");

//						addLine(postRequest);
					}
				} catch (ParseException e) {
							e.printStackTrace();
				} catch (IOException e) {
							e.printStackTrace();
				}

			} else {
				Logging.doLog(LOG_TAG, "http response equals null", 
						"http response equals null");
			}
		} catch (UnsupportedEncodingException e) {
			Logging.doLog(LOG_TAG, "UnsupportedEncodingException. Return -3.", 
					"UnsupportedEncodingException. Return -3.");

//			addLine(postRequest);
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			Logging.doLog(LOG_TAG, "ClientProtocolException. Return -2.", 
					"ClientProtocolException. Return -2.");

//			addLine(postRequest);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Logging.doLog(LOG_TAG, "IOException. Return -1.", 
					"IOException. Return -1.");

//			addLine(postRequest);
			e.printStackTrace();
			return null;
		}		
		return data;
	}

	private static String convertStreamToString(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}