package roman10.tutorial.ui.pagedview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PhotoSwipeAdapter extends PagedAdapter {
    private int mPageCnt;
    private Context mContext;
	
    public PhotoSwipeAdapter(Context pContext, int pPageCnt) {
    	mContext = pContext;
    	mPageCnt = pPageCnt;
    }
    
    @Override
    public int getCount() {
        return mPageCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.paged_view_item, parent, false);
        }

        ((TextView) convertView).setText(Integer.toString(position));

        return convertView;
	}
}
