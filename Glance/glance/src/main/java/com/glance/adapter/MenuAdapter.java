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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.glance.R;
import com.glance.utils.Utils;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class MenuAdapter extends CardScrollAdapter{

	private ArrayList<String> menuList;
	private Context mContext;
	private LayoutInflater inflater;
	private com.glance.view.RobotoTextView menuItem;
	private ImageView doneImage;
	private boolean isImageSet = false;

	public MenuAdapter(Context context, ArrayList<String> menuList) {
		this.menuList = menuList;
		this.mContext = context;
	}

	@Override
	public int getCount() {

		return menuList.size();
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.menu, parent, false);

		menuItem = (RobotoTextView) itemView.findViewById(R.id.tv_menu_item);
		doneImage = (ImageView) itemView.findViewById(R.id.menu_icon);

		menuItem.setText(menuList.get(position));
		String savedUrl = Utils.getSavedUrl(mContext);
		if (menuList.get(position).equals(savedUrl)) {
			doneImage.setImageResource(R.drawable.ic_done);
			doneImage.setVisibility(View.VISIBLE);
			isImageSet = true;
		}
		itemView.setTag(position);
		return itemView;
	}

	public boolean isImageSet() {
		return isImageSet;
	}

	public void setImageSet(boolean isImageSet) {
		this.isImageSet = isImageSet;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return menuList.get(arg0);
	}
	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return menuList.indexOf(arg0);
	}
	
	
	
}
