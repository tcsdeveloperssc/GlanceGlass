package com.glance.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glance.R;
import com.glance.activity.HotSpotActivity;
import com.glance.bean.model.HotSpots;
import com.glance.bean.model.SubNodes;
import com.glance.bean.model.TextSpots;
import com.glance.controller.core.CallBackListener;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.LoadImageFromDatabaseTask;
import com.glance.utils.Utils;
import com.glance.view.HotSpotCircleView;
import com.google.android.glass.widget.CardScrollAdapter;

public class SubNodeAdapter extends CardScrollAdapter {

	private CallBackListener listener;

	private String artifactId;
	private ArrayList<HotSpots> hotSpotList = new ArrayList<HotSpots>();
	private ArrayList<TextSpots> textSpotList = new ArrayList<TextSpots>();
	private ArtiFactImageTask artTask;
	private LayoutInflater inflater;

	private RelativeLayout yourRelativeLayout;
	private HotSpotCircleView hotspotCircle;
	private Context mContext;
	private ArrayList<SubNodes> subNodeList = new ArrayList<SubNodes>();

	public SubNodeAdapter(Context ctx, ArrayList<SubNodes> subNodeList,
			CallBackListener listener) {

		this.mContext = ctx;
		this.subNodeList = subNodeList;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return subNodeList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.hotspot_activity, parent,
				false);

		yourRelativeLayout = (RelativeLayout) itemView
				.findViewById(R.id.hotspots_image);

		ImageView hotSpotBg = (ImageView) itemView.findViewById(R.id.hot_img);

		artifactId = subNodeList.get(position).getartifactId();

		String type = subNodeList.get(position).getSelfHelp();
		hotSpotList = subNodeList.get(position).gethotspots();

		textSpotList = subNodeList.get(position).getTextSpots();
		RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(
				640, 360);

		yourRelativeLayout.setVisibility(View.INVISIBLE);

		if (artifactId != null && artifactId.trim().length() > 0) {

//			if ("image".equalsIgnoreCase(type)) {
				if (Utils.getFromPreference(mContext, "OFFLINE_MODE")
						.equalsIgnoreCase("true")) {
					new LoadImageFromDatabaseTask(mContext, itemView,
							LoadImageFromDatabaseTask.IMAGE_BACKGROUND)
							.execute(artifactId, "ImageTable");
				} else {
					artTask = new ArtiFactImageTask(mContext, itemView,
							listener);
					artTask.execute(artifactId);
				}
//			} 

		}else {
			ProgressBar pBarHotspot = (ProgressBar)((itemView).findViewById(R.id.pbHotSpot));
			if (pBarHotspot != null)
				pBarHotspot.setVisibility(View.GONE);
			yourRelativeLayout.setVisibility(View.VISIBLE);
			hotSpotBg.setBackgroundColor(Color.parseColor(subNodeList.get(
					position).getColor_code()));
			HotSpotActivity.isImageLoaded = true;
		}

		
		hotspotCircle = new HotSpotCircleView(itemView.getContext(),
				hotSpotList);
		yourRelativeLayout.addView(hotspotCircle, Params);
		if (textSpotList != null) {
			for (int i = 0; i < textSpotList.size(); i++) {
				TextView tv = new TextView(mContext);
				tv.setX(Float.parseFloat(textSpotList.get(i).getX()));
				tv.setY(Float.parseFloat(textSpotList.get(i).getY()));
				tv.setTextSize(15);
				tv.setText((Html.fromHtml(textSpotList.get(i).getContent())));
				yourRelativeLayout.addView(tv);
			}
		}
		ImageView iv = (ImageView)((itemView).findViewById(R.id.iv_checkList));
		if (!subNodeList.get(position).isCheckListDone()){	
			if (iv != null){
				iv.setImageResource(R.drawable.checklist_not_done);
				iv.setVisibility(View.VISIBLE);
			}
		}else{
			if (iv != null){
				iv.setVisibility(View.GONE);
			}
		}
			
		itemView.setTag(position);
		return itemView;

	}

	@Override
	public Object getItem(int arg0) {
		
		return subNodeList.get(arg0);
	}

	@Override
	public int getPosition(Object arg0) {
		
		return subNodeList.indexOf(arg0);
	}
	
	
}