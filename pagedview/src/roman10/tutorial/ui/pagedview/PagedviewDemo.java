package roman10.tutorial.ui.pagedview;

import roman10.tutorial.ui.pagedview.PagedView.OnPagedViewChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PagedviewDemo extends Activity {
	private static final int PAGE_COUNT = 10;
    private static final int PAGE_MAX_INDEX = PAGE_COUNT;
    
    private PhotoSwipeAdapter mAdapter;
    private PageIndicator mPageIndicator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final FrameLayout contentView = (FrameLayout) this.findViewById(R.id.content_view);
        LayoutInflater.from(this).inflate(R.layout.paged_view, contentView);
        
        final PagedView pagedView = (PagedView)findViewById(R.id.paged_view);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        mAdapter = new PhotoSwipeAdapter(this.getApplicationContext(), PAGE_COUNT);
        pagedView.setAdapter(mAdapter);
        
        mPageIndicator = (PageIndicator)findViewById(R.id.page_indicator);
        mPageIndicator.setDotCount(PAGE_MAX_INDEX);
        setActivePage(pagedView.getCurrentPage());
    }
    
    private void setActivePage(int page) {
    	mPageIndicator.setActiveDot(page);
    }
    
    private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {

//      @Override
      public void onStopTracking(PagedView pagedView) {
      }

//      @Override
      public void onStartTracking(PagedView pagedView) {
      }

//      @Override
      public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
//          setActivePage(newPage);
    	  setActivePage(newPage);
      }
  };
}