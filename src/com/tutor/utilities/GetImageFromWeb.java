package com.tutor.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class GetImageFromWeb {
	public static Bitmap GetImageDataFromWeb(String url) {
		Bitmap imageData = null;
		GetImageDataTask task = new GetImageDataTask();
		try {
			imageData = task.execute(url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return imageData;
	}
	
	public static class GetImageDataTask extends AsyncTask<String, Bitmap, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... url) {
			return GetImageDataService(url[0]);
		}
	
		private Bitmap GetImageDataService(String url) {
			URL myFileUrl = null;
			Bitmap bitmap = null;
			try {
				myFileUrl = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}
}
