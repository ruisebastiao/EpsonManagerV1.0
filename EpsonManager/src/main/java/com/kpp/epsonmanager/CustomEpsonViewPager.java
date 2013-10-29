package com.kpp.epsonmanager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Geral on 15-10-2013.
 */


public class CustomEpsonViewPager extends ViewPager {

    private boolean enabled;

    public CustomEpsonViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
       if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }
//    @Override
//    public void setCurrentItem(int item) {
//        if ( Propriedades.getInstance().getSelectedEpson()!=null){
//            Epson epson=Propriedades.getInstance().getSelectedEpson();
//            if (epson.isManMode()==false)
//                return;
//        }
//        super.setCurrentItem(item);
//    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    } }
