package roman10.tutorial.singleselectionlist;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class TextSelectedView extends LinearLayout {
	private TextView mText;
	private RadioButton mRadioBtn;
	private TextSelected mTextSelected;
	public TextSelectedView(Context context, final TextSelected textSelected) {
		super(context);
		/* First Text to the right (horizontal),
		 * not above and below (vertical) */
		this.setOrientation(HORIZONTAL);
		mTextSelected = textSelected;
		
		mText = new TextView(context);
		mText.setPadding(15, 0, 0, 0);
		mText.setTypeface(Typeface.SERIF, 0);
		mText.setText(textSelected.getText());
		LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		params.gravity = Gravity.CENTER_VERTICAL;
		addView(mText, params);
		//add radio button
		mRadioBtn = new RadioButton(context);
		mRadioBtn.setChecked(textSelected.getSelected());
		mRadioBtn.setFocusable(false);
		mRadioBtn.setClickable(false);
		addView(mRadioBtn, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	public void setText(String words) {
		mText.setText(words);
	}
	
	public void setSelected(boolean selected) {
		mRadioBtn.setChecked(selected);
		mTextSelected.setSelected(selected);
	}
	
	public boolean getSelected() {
		return mTextSelected.getSelected();
	}
}