package roman10.tutorial.singleselectionlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TextSelectedListAdapter extends BaseAdapter {

	/** Remember our context so we can use it when constructing views. */
	private Context mContext;

	private List<TextSelected> mItems = new ArrayList<TextSelected>();

	public TextSelectedListAdapter(Context context) {
		mContext = context;
	}

	public void addItem(TextSelected it) { mItems.add(it); }

	public void setListItems(List<TextSelected> lit) 
	{ mItems = lit; }

	/** @return The number of items in the */
	public int getCount() { return mItems.size(); }

	public Object getItem(int position) 
	{ return mItems.get(position); }
	
	/** Use the array index as a unique id. */
	public long getItemId(int position) {
		return position;
	}

	/** @param convertView The old view to overwrite, if one is passed
	 * @returns a IconifiedTextView that holds wraps around an IconifiedText */
	public View getView(int position, View convertView, ViewGroup parent) {
		TextSelectedView btv;
		btv = new TextSelectedView(mContext, mItems.get(position));
		return btv;
	}
}