package roman10.ui.iconifiedtextselectedlist;

import java.util.ArrayList;
import java.util.List;

import roman10reborn.topsecret.main.TopSecretImport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class IconifiedTextSelectedListAdapter extends BaseAdapter {

	/** Remember our context so we can use it when constructing views. */
	private Context mContext;

	private List<IconifiedTextSelected> mItems = new ArrayList<IconifiedTextSelected>();

	public IconifiedTextSelectedListAdapter(Context context) {
		mContext = context;
	}

	public void addItem(IconifiedTextSelected it) { mItems.add(it); }

	public void setListItems(List<IconifiedTextSelected> lit) 
	{ mItems = lit; }

	/** @return The number of items in the */
	public int getCount() { return mItems.size(); }

	public Object getItem(int position) 
	{ return mItems.get(position); }

	public boolean areAllItemsSelectable() { return false; }

	public boolean isSelectable(int position) { 
		return mItems.get(position).isSelectable();
	}

	/** Use the array index as a unique id. */
	public long getItemId(int position) {
		return position;
	}

	/** @param convertView The old view to overwrite, if one is passed
	 * @returns a IconifiedTextView that holds wraps around an IconifiedText */
	public View getView(int position, View convertView, ViewGroup parent) {
		IconifiedTextSelectedView btv;
		if (convertView == null) {
			btv = new IconifiedTextSelectedView(mContext, mItems.get(position));
		} else { // Reuse/Overwrite the View passed
			// We are assuming(!) that it is castable! 
			btv = (IconifiedTextSelectedView) convertView;
			btv.setText(mItems.get(position).getText());
			btv.setIcon(mItems.get(position).getIcon());
			btv.setSelected(mItems.get(position).getSelected());
			if (mItems.get(position).getVisibility()) {
				btv.setVisibility(true);
			} else {
				btv.setVisibility(false);
			}
		}
		btv.mCheckbox.setTag(position);
		btv.mCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int pos = (Integer)buttonView.getTag();
				//select all change
				if (pos == 0) {
					if (isChecked) {
						//set all checkbox as checked
						TopSecretImport.setAllEntriesSelected();
						TopSecretImport.self.refreshUI();
					} else {
						//set all checkbox as unchecked
						TopSecretImport.setAllEntriesUnselected();
						TopSecretImport.self.refreshUI();
					}
				} else {
					if (isChecked) {
						TopSecretImport.setEntrySelected(pos);
					} else {
						TopSecretImport.setEntryUnselected(pos);
					}
				}
			}
		});
		btv.mCheckbox.setChecked(TopSecretImport.mSelected.get(position));
		return btv;
	}
}