package com.example.sshtest;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.jcraft.jsch.*;

public class MainActivity extends Activity {
	private String user = "root";
	private String host = "******";  
	private int port = 22;
	private String pass = "***";
	private Button mConnect;
	private Button mCommand;
	private Session session;
	private AlertDialog.Builder mDlg;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mConnect = (Button)findViewById(R.id.connect);
		mCommand = (Button)findViewById(R.id.power);
		progressDialog = new ProgressDialog(getApplicationContext());
		mCommand.setEnabled(false);
		mDlg= new AlertDialog.Builder(this);
		mConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (AppStatus.getInstance(MainActivity.this).isOnline(MainActivity.this)) {
					try {
						boolean flag = new SShConnect().execute().get();
						if(flag){
							mCommand.setEnabled(true);
							Toast.makeText(getApplicationContext(), "You are successfully connected", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					
					
				}else{
					Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
		
		mCommand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (AppStatus.getInstance(MainActivity.this).isOnline(MainActivity.this)) {
					if(exeCommand()){
						mCommand.setEnabled(false);
						mDlg.setTitle("Success")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
						
						mDlg.create();
						mDlg.show();
					
					}else{
						mDlg.setTitle("Failure")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
						
						mDlg.create();
						mDlg.show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
				}
				
				
				
			}
		});
		
	}
	
	public boolean sshConnected() {
		JSch jsch = new JSch();

		try {
			session = jsch.getSession(user, host, port);
			session.setPassword(pass);

			// Avoid asking for key confirmation
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.connect();
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean exeCommand() {
		ChannelExec channelssh;
		try {
			channelssh = (ChannelExec) session.openChannel("exec");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			channelssh.setOutputStream(baos);

			// Execute command
			channelssh.setCommand("touch /var/www/html/Y/YM.txt");
			channelssh.connect();
			channelssh.disconnect();
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
			return false;

		}

	}
	
	public class SShConnect extends AsyncTask<Void, Void, Boolean>{
		
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MainActivity.this, "Connecting", "Please wait...",true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if(sshConnected()){
				
				return true;
				//Toast.makeText(getApplicationContext(), "You are successfully connected", Toast.LENGTH_LONG).show();
			}else{
				return false;
				//Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
		}
		
		
		
	}

	
	
}
