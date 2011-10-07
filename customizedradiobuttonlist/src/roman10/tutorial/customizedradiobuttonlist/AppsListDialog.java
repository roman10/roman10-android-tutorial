package roman10.tutorial.customizedradiobuttonlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AppsListDialog extends ListActivity {
	private List<Map<String, Object>> mObjectList = new ArrayList<Map<String, Object>>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Map<String, Object> m; 
		Intent l_intent;
		int icon_id;

		//add top secret 
		m = new HashMap<String, Object>();
		icon_id = getResources().getIdentifier("ts2", "drawable", "roman10reborn.apl.main");
		m.put("icon", icon_id);
		m.put("name", "Top Secret 2");
		l_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=roman10reborn.topsecret.main"));
		m.put("intent", l_intent);
		mObjectList.add(m);
		//add USecret
		m = new HashMap<String, Object>();
		icon_id = getResources().getIdentifier("usecret", "drawable", "roman10reborn.topsecret.main");
		m.put("icon", icon_id);
		m.put("name", "USecret");
		l_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=feipeng.ultimatesecret"));
		m.put("intent", l_intent);
		mObjectList.add(m);
		//add apl
		m = new HashMap<String, Object>();
		icon_id = getResources().getIdentifier("calllog", "drawable", "roman10reborn.topsecret.main");
		m.put("icon", icon_id);
		m.put("name", "Advanced Phone Log");
		l_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=roman10reborn.apl.main"));
		m.put("intent", l_intent);
		mObjectList.add(m);
		//add chef panda
		m = new HashMap<String, Object>();
		icon_id = getResources().getIdentifier("chefpanda", "drawable", "roman10reborn.topsecret.main");
		m.put("icon", icon_id);
		m.put("name", "Chef Panda");
		l_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=feipeng.receipt"));
		m.put("intent", l_intent);
		mObjectList.add(m);
		ListAdapter adapter = new SimpleAdapter(this.getApplicationContext(), 
				mObjectList,
				R.layout.list_item_single_line_image_text,
				new String[] {"icon", "name"}, 
				new int[] {R.id.list_item_single_line_image_text_image, R.id.list_item_single_line_image_text_text});
		setListAdapter(adapter);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		Map itemMap = (Map)l.getItemAtPosition(position);
		Intent lIntent = (Intent)itemMap.get("intent");		
		startActivity(lIntent);
	}

}
