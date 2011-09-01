package roman10.tutorial.tabmenu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
    TabMenu.MenuItemAdapter []mMenuItemAdapters = new TabMenu.MenuItemAdapter[4];
    TabMenu.MneuAdapter mMenuAdapter;
    TabMenu mTabMenu;
    int mSelectedMenu = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*create the menu*/
        mMenuAdapter = new TabMenu.MneuAdapter(this, new String[] {
        		"Import", "Export", "Secrets", "Share"
        }, 16, Color.BLACK, Color.GRAY, Color.WHITE);
        /*create the menu items*/
        mMenuItemAdapters[0] = new TabMenu.MenuItemAdapter(this, new String[] {
        		"Gallery", "Browse", "All", "Add"
        }, new int[] {R.drawable.mainmenu_import, R.drawable.mainmenu_export, 
        		R.drawable.mainmenu_secret, R.drawable.mainmenu_share}, 10, 0xFFFFFFFF);
        
        mMenuItemAdapters[1] = new TabMenu.MenuItemAdapter(this, new String[] {
        		"Folder", "All", "Export", "Share"
        }, new int[] {R.drawable.mainmenu_import, R.drawable.mainmenu_export, 
        		R.drawable.mainmenu_secret, R.drawable.mainmenu_share}, 10, 0xFFFFFFFF);
        
        mMenuItemAdapters[2] = new TabMenu.MenuItemAdapter(this, new String[] {
        		"View", "Show", "Move", "Delete"
        }, new int[] {R.drawable.mainmenu_import, R.drawable.mainmenu_export, 
        		R.drawable.mainmenu_secret, R.drawable.mainmenu_share}, 10, 0xFFFFFFFF);
        
        mMenuItemAdapters[3] = new TabMenu.MenuItemAdapter(this, new String[] {
        		"Create", "SeShare", 
        }, new int[] {R.drawable.mainmenu_secret, R.drawable.mainmenu_share}, 10, 0xFFFFFFFF);
        
        mTabMenu = new TabMenu(this, new MenuClickEvent(), new MenuItemClickEvent(), 
        		mMenuAdapter, 0x55123456, R.style.PopupAnimation);
        mTabMenu.update();
        mTabMenu.setMenuSelected(0);
        //mTabMenu.setMenuItemSelected(1, Color.GRAY);
        mTabMenu.setMenuItemAdapter(mMenuItemAdapters[0]);
    }
    
    class MenuClickEvent implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mSelectedMenu = arg2;
			mTabMenu.setMenuSelected(arg2);
			mTabMenu.setMenuItemAdapter(mMenuItemAdapters[arg2]);
		}
    }
    
    class MenuItemClickEvent implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mTabMenu.setMenuItemSelected(arg2, Color.GRAY);
			String lStr = String.valueOf(mSelectedMenu) + " " + String.valueOf(arg2);
			Toast.makeText(Main.this, lStr, 500).show();
		}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	if (mTabMenu != null) {  
        		if (mTabMenu.isShowing())  
                	mTabMenu.dismiss();  
                else {  
                	mTabMenu.showAtLocation(findViewById(R.id.LinearLayout01),  
                            Gravity.BOTTOM | Gravity.CENTER, 0, 0);  
                }  
        	}
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /*@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);  
    }  
    @Override  
    public boolean onMenuOpened(int featureId, Menu menu) {  
        if (mTabMenu != null) {  
            if (mTabMenu.isShowing())  
            	mTabMenu.dismiss();  
            else {  
            	mTabMenu.showAtLocation(findViewById(R.id.LinearLayout01),  
                        Gravity.BOTTOM | Gravity.CENTER, 0, 0);  
            }  
        }  
        return false;
    }  */
}