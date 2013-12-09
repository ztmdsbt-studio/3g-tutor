package com.tutor.utilities;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tutor.model.ApplicationData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class Utils {
	private static AlertDialog alertDialog;
	private static Activity context;
	private static ApplicationData applicationData;
	private static final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static boolean isInteger(String str) {
		return isInteger(str, null);
	}

	public static boolean isInteger(String str, Boolean positive) {
		try {
			int d = Integer.parseInt(str);
			if (positive != null && positive) {
				return d >= 0;
			} else {
				return false;
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static void showMessageBox(Activity context,
			ApplicationData applicationData, String errorMsg) {
		String alertStr;
		if (applicationData.getCurrentActivity() == null) {
			alertStr = "Alert Dialog is null. Activity:"
					+ context.getClass().getName();
			Log.e("ต๗สิ", alertStr);
			return;
		}
		try {
			Utils.context = context;
			Utils.applicationData = applicationData;
			if (alertDialog != null) {
				alertDialog.dismiss();
			}
			alertDialog = new AlertDialog.Builder(
					(Context) (context.getParent() == null ? applicationData
							.getCurrentActivity() : context.getParent()))
					.create();
			alertDialog.setMessage(errorMsg);
			alertDialog.show();
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					alertDialog.dismiss();
				}
			};

			executor.schedule(runnable, 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showMessageBox(ApplicationData applicationData,
			String errorMessage) {
		String alertStr;
		if (applicationData.getCurrentActivity() == null) {
			alertStr = "Alert Dialog is null. Activity:"
					+ context.getClass().getName();
			Log.e("ต๗สิ", alertStr);
			return;
		}
		Utils.showMessageBox(applicationData.getCurrentActivity(),
				applicationData, errorMessage);
	}

	public static void closeMessageBox() {
		if (alertDialog != null) {
			alertDialog.dismiss();
		} else {
			String alertStr;
			if (applicationData.getCurrentActivity() == null) {
				alertStr = "Alert Dialog is null. Activity:"
						+ context.getClass().getName();
			} else {
				alertStr = "Alert Dialog is null.activity:"
						+ (context.getParent() == null ? applicationData
								.getCurrentActivity() : context.getParent())
								.getClass().getName();
			}
			Log.e("ต๗สิ", alertStr);
		}
	}
}