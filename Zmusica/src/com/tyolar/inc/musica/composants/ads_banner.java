package com.tyolar.inc.musica.composants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.tyolar.inc.musica.R;

@SuppressLint("NewApi")
public class ads_banner extends RelativeLayout {

	public ads_banner(Context context) {
		super(context);
		initview(context);
	}

	public ads_banner(Context context, AttributeSet attrs) {
		super(context, attrs);
		initview(context);
	}

	public ads_banner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initview(context);
	}

	private void initview(Context context) {
		// TODO Auto-generated method stub
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.include_ads, this, true);
		final RelativeLayout df = (RelativeLayout) findViewById(R.id.adsview);

	}
}
