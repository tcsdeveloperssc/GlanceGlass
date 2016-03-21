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
 19-Dec-2013
 528937vnkm
 Class Description
 **/
package com.glance.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.glance.R;
import com.glance.utils.Utils;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class SettingsAdapter extends CardScrollAdapter{

	private ArrayList<String> urlList;
	private Context mContext;
	private LayoutInflater inflater;
	private com.glance.view.RobotoTextView url;
	private ImageView doneImage;
	private boolean isImageSet = false;

	public SettingsAdapter(Context context, ArrayList<String> urlList) {
		this.urlList = urlList;
		this.mContext = context;
	}

	@Override
	public int getCount() {

		return urlList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.settings, parent, false);

		url = (RobotoTextView) itemView.findViewById(R.id.tv_url);
		doneImage = (ImageView) itemView.findViewById(R.id.iv_settings);

		url.setText(urlList.get(position));
		url.setTextColor(Color.WHITE);
		String savedUrl = Utils.getSavedUrl(mContext);
		if (urlList.get(position).equals(savedUrl)) {
			doneImage.setImageResource(R.drawable.ic_done);
			doneImage.setVisibility(View.VISIBLE);
		}
		itemView.setTag(position);
		return itemView;
	}


	@Override
	public Object getItem(int arg0) {
		
		return urlList.get(arg0);
	}

	@Override
	public int getPosition(Object arg0) {
		
		return urlList.indexOf(arg0);
	}

}
