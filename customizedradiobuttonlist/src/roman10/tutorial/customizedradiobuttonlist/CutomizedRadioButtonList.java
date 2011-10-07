package roman10.tutorial.customizedradiobuttonlist;

import java.util.ArrayList;
import java.util.List;

import roman10.tutorial.singleselectionlist.TextSelected;
import roman10.tutorial.singleselectionlist.TextSelectedListAdapter;
import roman10.tutorial.singleselectionlist.TextSelectedView;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CutomizedRadioButtonList extends ListActivity {
	private List<TextSelected> mSelectionList = new ArrayList<TextSelected>();
	private Context mContext;
	private Button btn_next, btn_back;
	private CheckBox chk_default;
	private TextView tv_ins;
	
	private int mSelectedItem = 0;
	private String[] itemTexts = {"Breakfast", "Lunch", "Dinner", "All"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mContext = this.getApplicationContext();
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.setContentView(R.layout.customized_single_selection_list);
	    
	    mSelectedItem = 0;	    
    	for (int i = 0; i < itemTexts.length; ++i) {
    		TextSelected l_ts;
    		if (i == mSelectedItem) {
    			l_ts = new TextSelected(itemTexts[i], true);
    		} else {
    			l_ts = new TextSelected(itemTexts[i], false);
    		}
	    	mSelectionList.add(l_ts);
    	}
	    TextSelectedListAdapter adapter = new TextSelectedListAdapter(mContext);
    	adapter.setListItems(mSelectionList);
        this.setListAdapter(adapter);
       
        btn_next = (Button) findViewById(R.id.single_selection_btn_first);
	    btn_next.setClickable(true);
	    btn_next.setFocusable(true);
	    btn_next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//do whatever you like
				finish();
			}
		});
	    btn_back = (Button) findViewById(R.id.single_selection_btn_second);
	    btn_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//do whatever you like
				finish();
			}
		});

	    chk_default = (CheckBox) findViewById(R.id.single_selection_chkbox_default);
	    chk_default.setChecked(false);
	    
	    tv_ins = (TextView) this.findViewById(R.id.single_selection_chkbox_default_text);
	    tv_ins.setText("Test");
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {    
    	TextSelectedView l_v = (TextSelectedView)v;
    	if (l_v.getSelected()) {
    		//if it's already selected, do nothing
    	} else {
    		//change the prev selected to unselected
    		TextSelectedView l_prevV = (TextSelectedView)l.getChildAt(mSelectedItem);
    		l_prevV.setSelected(false);
    		mSelectedItem = position;
    		l_v.setSelected(true);
    	}
    }
}