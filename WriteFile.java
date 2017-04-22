package com.uasdtu.tript.uas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class WriteFile extends Activity {

	File file;
	String filename;

	public WriteFile(){
		filename = "log.txt";

		file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);

	}

	// write text to file
	public void write(String str1,String str2,String str3,String str4,String str5) {


		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file,true);
			fileOutputStream.write(str1.getBytes());
			fileOutputStream.write(str2.getBytes());
			fileOutputStream.write(str3.getBytes());
			fileOutputStream.write(str4.getBytes());
			fileOutputStream.write(str5.getBytes());
			fileOutputStream.write("\n".getBytes());

			try {
				fileOutputStream.close();
			}catch (IOException e) {e.printStackTrace();}
			//Toast.makeText(getApplicationContext(), "Message Saved",Toast.LENGTH_SHORT ).show();

		}catch (IOException e){e.printStackTrace();
			Log.e("error","1");}


	}}
