package com.tutor.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ConfirmDialog extends Dialog {

	private static int default_width = 272;
	private static int default_height = 160;

	public ConfirmDialog(Context context, int layout, int style) {
		this(context,default_width,default_height,layout,style);
	}
	
	public ConfirmDialog(Context context,int width, int height,int layout, int style ){
		super(context,style);
		
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		float density=getDesity(context);
		params.width=(int)(width*density);
		params.height=(int)(height*density);
		params.gravity=Gravity.CENTER;
		window.setAttributes(params);
	}
	
	private float getDesity(Context context)
	{
		Resources resource = context.getResources();
		DisplayMetrics dm = resource.getDisplayMetrics();
		return dm.density;
	}
	
	public void onCancelClick(View view)
	{
		this.dismiss();
	} 
}
