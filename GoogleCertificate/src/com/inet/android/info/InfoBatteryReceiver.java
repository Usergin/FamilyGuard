package com.inet.android.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.util.Log;

import com.inet.android.certificate.R;

public class InfoBatteryReceiver extends BroadcastReceiver {
	private String TAG = "BatteryBroadcastReceiver";
	String connect_dev = "";
	Resources path;

	public InfoBatteryReceiver(String connect) {
		// TODO Auto-generated constructor stub
		this.connect_dev = connect;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		path = context.getApplicationContext().getResources();
		String area = path.getString(R.string.battery);
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			CreateServiceInformation serviceInfo = new CreateServiceInformation(
					context);
			serviceInfo.sendStr(area,
					getInfoBattery(context.getApplicationContext(), intent));
			context.getApplicationContext().unregisterReceiver(this);
		}
	}

	private String getInfoBattery(Context context, Intent intent) {
		String infoBattery = "";
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float) scale;
		Log.d(TAG, path.getString(R.string.charge_level) + batteryPct);

		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		String strStatus;
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			strStatus = path.getString(R.string.status_battery_charging);
		} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
			strStatus = path.getString(R.string.status_battery_discharged);
		} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			strStatus = path.getString(R.string.status_battery_not_charged);
		} else if (status == BatteryManager.BATTERY_STATUS_FULL) {
			strStatus = path.getString(R.string.status_battery_full);
		} else {
			strStatus = "";
		}

		int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,
				BatteryManager.BATTERY_HEALTH_UNKNOWN);
		String strHealth;
		if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
			strHealth = path.getString(R.string.battery_healt_good);
		} else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
			strHealth = path.getString(R.string.battery_healt_overheat);
		} else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
			strHealth = path.getString(R.string.battery_healt_dead);
		} else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
			strHealth = path.getString(R.string.battery_healt_over_voltage);
		} else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
			strHealth = path
					.getString(R.string.battery_healt_unspecified_failure);
		} else {
			strHealth = path.getString(R.string.status_battery_unknown);
		}
		// ����� ������� �������� �������?
		int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		String charge = "";
		if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
			charge = path.getString(R.string.battery_plugged_usb);
		} else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
			charge = path.getString(R.string.battery_plugged_ac);
		} else if (chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
			charge = path.getString(R.string.battery_plugged_wireless);
		} else {
			charge = path.getString(R.string.battery_plugged_unknown);
		}
		infoBattery = this.connect_dev + "\n"
				+ path.getString(R.string.charge_level) + batteryPct + "\n"
				+ path.getString(R.string.status_battery) + strStatus + "\n"
				+ path.getString(R.string.status_battery_healt) + strHealth
				+ "\n" + path.getString(R.string.status_battery_charge)
				+ charge;
		return infoBattery;
	}
}