package roman10.ui.iconifiedtextselectedlist;

import roman10reborn.topsecret.main.R;
import roman10reborn.topsecret.settings.SettingsStatic;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class IconifiedTextSelectedView extends LinearLayout {
	//use framelayout to add the background photo frame and 
	//the front photo
	private TextView mText;
	private ImageView mIcon;
	//private ImageButton mIcon;
	private ImageView mBg;
	public CheckBox mCheckbox;
	public static int mCheckbox_id = 1000;
	private int icon_size_width = 100;
	private int icon_size_height = 100;
	public IconifiedTextSelectedView(Context context, IconifiedTextSelected iconifiedTextSelected) {
		super(context);

		/* First Icon and the Text to the right (horizontal),
		 * not above and below (vertical) */
		this.setOrientation(HORIZONTAL);
		mIcon = new ImageView(context);
		mBg = new ImageView(context);
		//mIcon = new ImageButton(context);
		//mIcon.setBackgroundResource(roman10reborn.topsecret.main.R.drawable.photo_frame);
		//mIcon.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.picture_frame));
		//mIcon.setPadding(5, 10, 20, 10); // 5px to the right
		//mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
		mIcon.setScaleType(ScaleType.CENTER_CROP);
		mBg.setScaleType(ScaleType.FIT_XY);
		//mIcon.setBackgroundResource(roman10reborn.topsecret.main.R.drawable.photo_frame);
		//mIcon.setClickable(true);
		//mIcon.setFocusable(false);
		//mIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //mIcon.setLayoutParams(new LayoutParams(90, 90));
		int thumbOption = SettingsStatic.getThumbOption(context);
		if (thumbOption == 0) {
			icon_size_width = 85;
			icon_size_height = 85;
		} else if (thumbOption == 1) {
			icon_size_width = 100;
			icon_size_height = 100;
		} else if (thumbOption == 2) {
			icon_size_width = 120;
			icon_size_height = 120;
		}
        
        FrameLayout.LayoutParams l_iconLayoutParams = new FrameLayout.LayoutParams(icon_size_height-10, icon_size_width-10);
        //l_iconLayoutParams.setMargins(10, 10, 10, 10);
        l_iconLayoutParams.gravity = Gravity.CENTER;
        mIcon.setLayoutParams(l_iconLayoutParams);
        
        FrameLayout.LayoutParams l_bgLayoutParams = new FrameLayout.LayoutParams(icon_size_height, icon_size_width);
        l_bgLayoutParams.gravity = Gravity.CENTER;
        mBg.setLayoutParams(l_bgLayoutParams);
        
        //mIcon.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        //mIcon.setMaxHeight(72);
//        mIcon.setMaxHeight(icon_size_height-10);
//        mBg.setMaxHeight(icon_size_height);
        //mIcon.setMaxWidth(72);
//        mIcon.setMaxWidth(icon_size_width-10);
//        mBg.setMaxWidth(icon_size_width);
		//mIcon.setClipToPadding(true);
		mIcon.setImageDrawable(iconifiedTextSelected.getIcon());
		mBg.setImageResource(R.drawable.photo_frame);
		//mIcon.setPadding(3, 3, 3, 3); // left, top, right, bottom
		mBg.setPadding(0, 0, 0, 0);
//		// left, top, right, bottom
//		FrameLayout fl = new FrameLayout(context);
//		fl.setLayoutParams(new LayoutParams(icon_size_height, icon_size_width));
//		fl.setPadding(0, 0, 0, 0);
		
		FrameLayout fl = new FrameLayout(context);
		fl.setLayoutParams(new FrameLayout.LayoutParams(icon_size_height, icon_size_width));
		fl.setPadding(0, 0, 0, 0);
		//fl.setClipToPadding(true);
		//fl.setClipChildren(true);
		fl.setForegroundGravity(Gravity.CENTER);
		fl.addView(mBg);
		fl.addView(mIcon);
		
		//addView(mIcon);
		addView(fl);
		
		mText = new TextView(context);
		mText.setPadding(15, 0, 0, 0);
		mText.setTypeface(Typeface.SERIF, 0);
		mText.setText(iconifiedTextSelected.getText());
		/* Now the text (after the icon) */
		LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		params.gravity = Gravity.CENTER_VERTICAL;
		addView(mText, params);
//		
		//add the checkbox
		mCheckbox = new CheckBox(context);
		mCheckbox.setChecked(false);
		mCheckbox.setFocusable(false);
		if (iconifiedTextSelected.getVisibility()) {
			mCheckbox.setVisibility(View.VISIBLE);
		} else {
			mCheckbox.setVisibility(View.GONE);
		}
		mCheckbox.setId(mCheckbox_id);
		addView(mCheckbox, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	public void setText(String words) {
		mText.setText(words);
	}
	
	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
		//mIcon.setPadding(4, 4, 4, 4); // left, top, right, bottom
	}

	public void setIcon(int res_id) {
		mIcon.setImageResource(res_id);
		//mIcon.setPadding(4, 4, 4, 4); // left, top, right, bottom
	}
	
	public void setSelected(boolean selected) {
		mCheckbox.setChecked(selected);
	}
	
	public void setVisibility(boolean visibility) {
		if (visibility) {
			mCheckbox.setVisibility(View.VISIBLE);
		} else 
		{
			mCheckbox.setVisibility(View.GONE);
		}
	}
}