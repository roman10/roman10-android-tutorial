package roman10.tutorial.ui.pagedview;

import roman10.tutorial.ui.pagedview.PagedView.OnPagedViewChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PagedViewDemo2 extends Activity {
	private static final int PAGE_COUNT = 10000000;
    private static final int PAGE_MAX_INDEX = PAGE_COUNT;
    
    private PhotoSwipeAdapter mAdapter;
    private TextView indicator;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paged_view_frame);
        final FrameLayout contentView = (FrameLayout) this.findViewById(R.id.content_view);
        LayoutInflater.from(this).inflate(R.layout.paged_view_demo2, contentView);
        
        final PagedView pagedView = (PagedView)findViewById(R.id.paged_view2);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        mAdapter = new PhotoSwipeAdapter(this.getApplicationContext(), PAGE_COUNT);
        pagedView.setAdapter(mAdapter);
        
        indicator = (TextView) findViewById(R.id.page_indicator2);
        setActivePage(pagedView.getCurrentPage());
	}
	private void setActivePage(int page) {
		indicator.setText((page + 1) + " of " + PAGE_COUNT);
    }
    
    private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {

    //	      @Override
	      public void onStopTracking(PagedView pagedView) {
	      }
	
	//	      @Override
	      public void onStartTracking(PagedView pagedView) {
	      }
	
	//	      @Override
	      public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
	    	  setActivePage(newPage);
	      }
   };
}
