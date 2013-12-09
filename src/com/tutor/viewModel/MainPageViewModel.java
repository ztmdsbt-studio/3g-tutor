package com.tutor.viewModel;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicaitonTutorItem;
import com.tutor.model.CreditsItem;
import com.tutor.model.ProductCategory;
import com.tutor.model.ProductType;


public class MainPageViewModel {
	public ArrayList<ApplicaitonTutorItem> applicationTutors;
	
	public ArrayList<CreditsItem> creditsItems;
	
	public ArrayList<ProductType> productTypes;
	
	public ProductCategory currentProductCategory;
	
	public MainPageViewModel(){
		
	}
	
	private class GetPageDataTask extends AsyncTask<Void,Void,Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			getApplicaitons();
			getCreditsItems();
			return null;
		}
		
		private void getApplicaitons(){
			SoapObject request = new SoapObject(AppConfiguration.NAMESPACE, "getApp");
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = request;
			HttpTransportSE httpTransportSE = new HttpTransportSE(AppConfiguration.APP_TUTOR_SERVICE);
			
			try {
				httpTransportSE.call(null, envelope);
				if(envelope.getResponse()!=null){
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		private void getCreditsItems(){
			
		}
		
	}
}
