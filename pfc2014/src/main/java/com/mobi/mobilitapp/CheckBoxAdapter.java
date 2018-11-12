package com.mobi.mobilitapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class CheckBoxAdapter extends ArrayAdapter<String> {

	private String[] files;
	private int layoutResourceId;
	private Context context;

	public CheckBoxAdapter(Context context, int resource, String[] files) {
		super(context, resource, files);

		// TODO Auto-generated constructor stub
		this.layoutResourceId = resource;
		this.context = context;
		this.files = files;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Typeface mFontreg = Typeface.createFromAsset(this.getContext().getAssets(),
				"fonts/Montserrat-Regular.ttf");
//		Typeface mFontbold = Typeface.createFromAsset(this.getContext().getAssets(),
//				"fonts/Montserrat-Bold.ttf");
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
		}
		
//		TextView tv = (TextView) row.findViewById(R.id.text1);
//		tv.setTypeface(mFontbold);
//		TextView tv2 = (TextView) row.findViewById(R.id.text2);
//		tv.setTypeface(mFontreg);
//		tv.setText(files[position]);
//		tv2.setText(files2[position]);
		
		
		CheckBox chbx= (CheckBox) row.findViewById(R.id.checkBox1);
		chbx.setTypeface(mFontreg);
		chbx.setText(files[position]);
	
		return row;
	}
}
