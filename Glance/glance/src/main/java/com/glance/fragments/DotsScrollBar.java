package com.glance.fragments;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.glance.R;
import com.glance.utils.Utils.GLog;

public class DotsScrollBar
{
    LinearLayout main_image_holder;
    public static void createDotScrollBar(Context context, LinearLayout main_holder,int selectedPage,int count)
    {
        for(int i=0;i<count;i++)
        {
            ImageView dot = null;
            
            dot= new ImageView(context);
            LinearLayout.LayoutParams vp = 
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                                LayoutParams.WRAP_CONTENT);
            vp.setMargins(10, 0, 0, 0);
            dot.setLayoutParams(vp);    
            if(i==selectedPage)
            {
                try {
                    dot.setImageResource(R.drawable.dot_selected);
                } catch (Exception e) 
                {
                    GLog.d("inside DotsScrollBar.java","could not locate identifier");
                }
            }else
            {
                dot.setImageResource(R.drawable.dot_unselected);
            }
            main_holder.addView(dot);
        }
        main_holder.invalidate();
    }
}
