package com.tutor.utilities;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;
import android.util.Log;

import com.tutor.model.AppConfiguration;

public class GetDataFromWebService {
	private static String serviceFunctionName;
	private static String serviceWebUri;
	private OnGetDataListener onGetDataListener;
	private static long timeCunsuming;

	public static class GetPageDataTask extends
			AsyncTask<Map<String, Object>, Boolean, String> {

		@Override
		protected String doInBackground(Map<String, Object>... params) {
			return getDataFromWebService(params[0]);
		}
	}

	public class GetPageDataTaskAsync extends
			AsyncTask<Map<String, Object>, Boolean, String> {

		@Override
		protected String doInBackground(Map<String, Object>... params) {
			return getDataFromWebService(params[0]);
		}

		@Override
		protected void onPostExecute(String responseData) {
			if (onGetDataListener != null) {
				onGetDataListener.onGetDataCompleted(responseData);
			}
		}
	}

	public void GetDataFromWebAsync(String functionName, String serviceUri,
			Map<String, Object> params) {
		serviceFunctionName = functionName;
		serviceWebUri = serviceUri;
		GetPageDataTaskAsync task = new GetPageDataTaskAsync();
		try {
			task.execute(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOnGetDataListener(OnGetDataListener onGetDataListener) {
		this.onGetDataListener = onGetDataListener;
	}

	private static String getDataFromWebService(Map<String, Object> params) {
		String response = "";
		SoapObject request = new SoapObject(AppConfiguration.NAMESPACE,
				serviceFunctionName);
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				request.addProperty(entry.getKey(), entry.getValue().toString());
			}
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		HttpTransportSE httpTransportSE = new HttpTransportSE(serviceWebUri,
				30000);
		try {
			timeCunsuming = System.currentTimeMillis();
			httpTransportSE.debug = true;
			httpTransportSE.call(null, envelope);
			Log.d("调试",
					"调用'"
							+ serviceFunctionName
							+ "'成功: "
							+ String.valueOf(System.currentTimeMillis()
									- timeCunsuming) + "毫秒");
			if (envelope.getResponse() != null) {
				response = envelope.getResponse().toString();
			}
		} catch (Exception e) {
			if (httpTransportSE.responseDump != null) {
				response = httpTransportSE.responseDump;
			}
			String errorMsg = "";
			if (e != null) {
				errorMsg = "调用'" + serviceFunctionName + "'失败. response: "
						+ response;
				Log.d("调试", errorMsg);
				e.printStackTrace();
			} else {
				errorMsg = "调用'" + serviceFunctionName + "'失败. response: "
						+ response + ". error:" + e.getCause() + ". callstack:"
						+ e.getStackTrace();
				Log.d("调试", errorMsg);
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String GetDataFromWeb(String functionName, String serviceUri,
			Map<String, Object> params) {
		serviceFunctionName = functionName;
		serviceWebUri = serviceUri;
		String responseStr = null;
		GetPageDataTask task = new GetPageDataTask();
		try {
			responseStr = task.execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}
}