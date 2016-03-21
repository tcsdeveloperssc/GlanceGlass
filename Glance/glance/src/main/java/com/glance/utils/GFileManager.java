/**
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

/**
04-Dec-2013
528937vnkm
Class Description
**/
package com.glance.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;


public class GFileManager {
	
	
	public static final String STORY_FILE = "gstory";
	public static final String USER_TASKS = "usertasks";
	public static final String STORY_LIST = "storyList";
	public static final String MAILS_FILE = "mailsFile";
	public static final String ASSET_DETAIL_FILE = "assetDetailFile";
	public static final String ASSET_TASKS = "assetTaks";
	public static final String USER_LIST = "userList";
	public static final String SURVEY_LIST = "surveyList";
	
	public static void writeStoryToFile(Context mContext, String fileName, String response){
		
		File createdFile = fileCreation(fileName, mContext);
		try {
			FileWriter out = new FileWriter(createdFile);
			out.write(response);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to read the response
	 * written in the local file
	 * **/
	public static String getStoryFromFile(String fileName, Context mContext) {
		File file = fileCreation(fileName, mContext);
		// Read text from file
		StringBuilder text = new StringBuilder();
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				// text.append('\n');
			}
			Utils.log("resp--->file reader->", text.toString());
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}
		return text.toString();
	}
	
	/**
	 * Create File function
	 * **/
	public static File fileCreation(String fileName, Context mContext) {
		File root = new File(mContext.getFilesDir(), fileName);
		if (!root.exists()) {
			try {
				root.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return root;
	}
	
	/**
	 * Delete File function
	 * **/
	public static boolean deleteFile(String fileName, Context mContext) {
		File root = new File(mContext.getFilesDir(), fileName);
		if (!root.exists()) {
			try {
				return root.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
 