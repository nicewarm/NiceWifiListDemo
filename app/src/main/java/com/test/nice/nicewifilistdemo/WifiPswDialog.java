package com.test.nice.nicewifilistdemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class WifiPswDialog extends Dialog{
	private TextView cancelButton;
	private TextView okButton;
	private EditText pswEdit;
	private OnCustomDialogListener customDialogListener;
	private String name;
	private TextView nameView;
	private CheckBox isShowpass;
	public WifiPswDialog(Context context,OnCustomDialogListener customListener,String name) {
			//OnCancelListener cancelListener) {
		super(context);
		// TODO Auto-generated constructor stub
		customDialogListener = customListener;
		this.name = name;
		
	}
	public interface OnCustomDialogListener{
		void back(String str);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_config_dialog);
		pswEdit = (EditText)findViewById(R.id.wifiDialogPsw);
		cancelButton = (TextView)findViewById(R.id.wifiDialogCancel);
		nameView = (TextView) findViewById(R.id.name);
		okButton = (TextView)findViewById(R.id.wifiDialogCertain);
		isShowpass = (CheckBox) findViewById(R.id.isShowPass);
		isShowpass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowpass.isChecked()){
					pswEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}else {
					pswEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		cancelButton.setOnClickListener(buttonDialogListener);
		okButton.setOnClickListener(buttonDialogListener);
		nameView.setText(this.name);
	}
	
	private View.OnClickListener buttonDialogListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if(view.getId() == R.id.wifiDialogCancel){
				pswEdit = null;
				customDialogListener.back(null);
				cancel();
			}
			else{
				customDialogListener.back(pswEdit.getText().toString());
				dismiss();
			}
		}
	};
	
}
