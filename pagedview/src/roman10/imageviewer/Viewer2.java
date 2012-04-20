package roman10.imageviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import roman10.zoomablegallery.ImageZoomView;
import roman10.zoomablegallery.MyGallery;
import roman10.zoomablegallery.PhotoAdapter;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


//TODO: slideshow in random order
public class Viewer2 extends Activity implements OnGestureListener {
	private MyGallery imgGallery;
	public static PhotoAdapter imgAdapter;
//	public static PhotoAdapter2 imgAdapter;
	private ImageButton resetBtn;
	private LinearLayout footbar;
	private ImageButton rotateLeftBtn, rotateRightBtn, zoomInBtn, zoomOutBtn, slideShowBtn, shareBtn;
	public static Viewer2 self;	//not good programming style, but it's ok for single instance
    private Context mContext;
    
    private GestureDetector gestureDetector;
    
    public static final String VIEW_TYPE = "TSVIEW_TYPE";
	public static final int VIEW = 1;
	public static final int SLIDESHOW = 2;
	public static final int SESHARE = 3;
	public static final int VIEW_SINGLE = 4;
	private int mViewType = VIEW;
	
	public static final String SHOW_STYLE = "TSSHOW_STYLE";
	//0 for ordered list; 1: random
	
	//we'll need to keep the screen on when showing images
	private WakeLock mWakeLock;	
	private FinishListener mFinishListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer_viewer2);
        mContext = this.getApplicationContext();
        self = this;
        
        Intent l_intent = this.getIntent();
        Bundle bundle = null;
	    if (l_intent!=null) {
	    	  bundle = l_intent.getExtras();
	    	  if (bundle != null) {
	    		  mViewType = bundle.getInt(Viewer2.VIEW_TYPE, Viewer2.VIEW);
	    	  }
	    }
	    if (mViewType == SLIDESHOW) {
	    	if (bundle != null) {
	    		mShowStyle = bundle.getInt(Viewer2.SHOW_STYLE, 0);
	    	}
	    }
        
	    mFinishListener = new FinishListener(mContext);
	    mFinishListener.setOnFinishListener(new OnFinishListener() {
			public void onReceiveFinish() {
				exitApp();
			}
	    }); 
	    
        imgGallery = (MyGallery) findViewById(R.id.imageviewer_viewer_img_gallery);
        imgGallery.setVisibility(View.VISIBLE);
        imgGallery.setSpacing(0);
		imgGallery.setAnimationDuration(1000);

		resetBtn = (ImageButton) this.findViewById(R.id.imageviewer_viewer_reset_btn);
		//resetBtn.setBackgroundResource(R.drawable.btn_bg);
		resetBtn.setVisibility(View.GONE);
		resetBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				imgGallery.resetCurrentPhoto();
				resetBtn.setVisibility(View.GONE);
			}
		});
		
		footbar = (LinearLayout) this.findViewById(R.id.image_viewer_footbar);
		hideMenu();
		rotateLeftBtn = (ImageButton) findViewById(R.id.bottom_btn1);
		rotateLeftBtn.setBackgroundResource(R.drawable.btn_bg);
		rotateLeftBtn.setImageResource(R.drawable.footbar_left);
		rotateLeftBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				int i = 0;
				ImageZoomView izv = (ImageZoomView) imgGallery.getSelectedView();
				for (; i < imageOriUpdateIndex.size(); ++i) {
					if (imageOriUpdateIndex.get(i)==izv.getIndex()) {
						i = -1;
						break;
					}
				}
				if (i !=-1 ) {
					imageOriUpdateIndex.add(izv.getIndex());
				}
				int l_currOri = imageOri.get(izv.getIndex());
				imageOri.set(izv.getIndex(), (l_currOri-1)%4);
				
				Log.i("rotate-update", String.valueOf(imageOri.get(izv.getIndex())));
				
				imgGallery.rotateLeft();
				timedShowMenu();
			}
		});
		
		rotateRightBtn = (ImageButton) findViewById(R.id.bottom_btn2);
		rotateRightBtn.setBackgroundResource(R.drawable.btn_bg);
		rotateRightBtn.setImageResource(R.drawable.footbar_right);
		rotateRightBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				int i = 0;
				ImageZoomView izv = (ImageZoomView) imgGallery.getSelectedView();
				for (; i < imageOriUpdateIndex.size(); ++i) {
					if (imageOriUpdateIndex.get(i)==izv.getIndex()) {
						i = -1;
						break;
					}
				}
				if (i !=-1 ) {
					imageOriUpdateIndex.add(izv.getIndex());
				}
				int l_currOri = imageOri.get(izv.getIndex());
				imageOri.set(izv.getIndex(), (l_currOri+1)%4);
				
				Log.i("rotate-update", String.valueOf(imageOri.get(izv.getIndex())));
				
				imgGallery.rotateRight();
				timedShowMenu();
			}
		});
				
		zoomInBtn = (ImageButton) findViewById(R.id.bottom_btn3);
		zoomInBtn.setBackgroundResource(R.drawable.btn_bg);
		zoomInBtn.setImageResource(R.drawable.footbar_zoomin);
		zoomInBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				imgGallery.zoomIn();
				timedShowMenu();
			}
		});
		
		zoomOutBtn = (ImageButton) findViewById(R.id.bottom_btn4);
		zoomOutBtn.setBackgroundResource(R.drawable.btn_bg);
		zoomOutBtn.setImageResource(R.drawable.footbar_zoomout);
		zoomOutBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				imgGallery.zoomOut();
				timedShowMenu();
			}
		});
		
		slideShowBtn = (ImageButton) findViewById(R.id.bottom_btn5);
		slideShowBtn.setBackgroundResource(R.drawable.btn_bg);
		slideShowBtn.setImageResource(R.drawable.footbar_slideshow);
		slideShowBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				int lCurStyle = ViewerConfigStatic.getShowStyle(getApplicationContext());
				if (lCurStyle != 0) {
					mShowStyle = lCurStyle;
					slideShowSecrets();
				} else {
					//ask user to select slideshow style
					Intent lIntent = new Intent();
					if (lCurStyle == 0 || lCurStyle == 1) {
						lCurStyle = 0;
					} else if (lCurStyle == 2) {
						lCurStyle = 1;
					}
					lIntent.putExtra(ShowStyleSelectionDialog.REQUEST_TYPE, ShowStyleSelectionDialog.REQUEST_TYPE_SHOW_STYLE);
					lIntent.putExtra(ShowStyleSelectionDialog.REQUEST_DEFAULT_SET, 0);
					lIntent.putExtra(ShowStyleSelectionDialog.REQUEST_DEFAULT_SEL, lCurStyle);
					lIntent.setClass(getApplicationContext(), roman10reborn.topsecret.dialogs.ShowStyleSelectionDialog.class);
			        startActivityForResult(lIntent, REQUEST_SELECT_SHOW_STYLE);
					//slideShowSecrets();
				}
			}
		});
		
		shareBtn = (ImageButton) findViewById(R.id.bottom_btn6);
		shareBtn.setBackgroundResource(R.drawable.btn_bg);
		shareBtn.setImageResource(R.drawable.footbar_share);
		shareBtn.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				sharePhoto();
				timedShowMenu();
			}
		});
		
		gestureDetector = new GestureDetector(this);
		refreshUI();
		
		/*use a background service to load the pictures, orientation, and thumbnails*/
		if ((mViewType == VIEW) || (mViewType == SLIDESHOW)) {
			  //start loading service, let it load pictures
	    	  startLoadService();
	    	  if (mViewType == SLIDESHOW) {
	    		  slideShowSecrets();
	    	  }
		} else if (mViewType == VIEW_SINGLE) {
	    	  //if view a single file only, do not load files
	      }
    }
	
	@Override
	public void onResume() {
		super.onResume();
		checkKeyguard();
		if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Viewer");
        mWakeLock.acquire();
        //ask the backend service to stop cleaning
        SettingsStatic.setAlarmCleanup(mContext, 0);
	}
	
	private static final int MENU_VIEWER_SETTINGS = 0;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_VIEWER_SETTINGS, 0, "Viewer Setting").setIcon(R.drawable.options_menu_viewer);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_VIEWER_SETTINGS:
				Intent l_intent = new Intent();
				l_intent.setClass(mContext, ViewerSettings.class);
				startActivity(l_intent);
				return true;
		}
		return false;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if ((mViewType == VIEW) || (mViewType == SLIDESHOW)) {
			startBackendService();
		}
		if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock = null;
        }
	}
	
	@Override
	public void onStop() {
		super.onStop();
		is_in_slideshow = false;
		//photoCanBeCleaned = true;
		//once the viewer is not visible, we destroy it so the backend can clean up 
		//the unencrypted picture
		//comment these two lines out as we want the viewer to back once user has done share
		//SettingsStatic.setAlarmCleanup(mContext, 1);	//moved to onDestroy
		//this.finish();
	}
	
	private void cleanUpSeShareFiles() {
		if (!TopSecretCleanUpService.clean_phone_mem_in_progress) {
			Intent l_intent = new Intent(mContext, TopSecretCleanUpService.class);
			l_intent.putExtra(TopSecretCleanUpService.CLEAN_TYPE, TopSecretCleanUpService.CLEAN_TYPE_PHONE);
			mContext.startService(l_intent);
		}
	}
	
	private void unbindLists() {
		//note that imgGallery.getCount() will return a huge number INT_MAX, cause freeze
		if (imgAdapter!=null) {
			for (int i = 0; i <= imgAdapter.getRealCount(); ++i) {
				ImageZoomView lView = (ImageZoomView) imgGallery.getChildAt(i);
				if (lView != null) {
					lView.cleanUp();
					lView = null;
				}
			}
			System.gc();
		}
	}
	
	@Override
	public void onDestroy() {
		//if the load service is still running, stop it
		//stopLoadService();
		super.onDestroy();
		//let the service to stop itself properly from loading more pictures
		if (ViewerLoadService.inProgress) {
			ViewerLoadService.stopLoading = true;
		}
		unbindLists();
		imgAdapter.setListItem(null);
		//as phone memory is very limited, we clear it up asap
		cleanUpSeShareFiles();
		//allow the backend service to clean up
		//SettingsStatic.setAlarmCleanup(mContext, 1);		
	}
	
	private void checkKeyguard() {
	    // If the keyguard is being displayed, exit this activity.  This returns
	    // the user to the activity list page, which will in turn return the user
	    // to the login page, requiring the user to enter his password again before
	    // get access again to his secrets.
	    KeyguardManager keyGuard = (KeyguardManager) getSystemService(
	        KEYGUARD_SERVICE);
	    if (keyGuard.inKeyguardRestrictedInputMode())
	    	startExitApp();
	}
	
	@Override
    public void onConfigurationChanged (Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	refreshUI();
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
		//this block is no longer needed as we decided to show option menu instead of the footbar
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//        	if (menu_shown == 1) {
//        		hideMenu();
//        	} else {
//        		timedShowMenu();
//        	}
//        } 
        return super.onKeyDown(keyCode, event);
    }
	
	private void startExitApp() {
		FinishEvent finishEvent = new FinishEvent(mContext, this.getApplicationContext());
		finishEvent.fire();
	}
	
	private void exitApp() {
		if (!TopSecretCleanUpService.clean_up_in_progress) {
			Intent l_intent = new Intent(mContext, TopSecretCleanUpService.class);
			l_intent.putExtra(TopSecretCleanUpService.CLEAN_TYPE, TopSecretCleanUpService.CLEAN_TYPE_ALL);
			mContext.startService(l_intent);
		}
		this.finish();
	}
	
	private void refreshUI() {
		imgAdapter = new PhotoAdapter(this);
//		imgAdapter = new PhotoAdapter(this, imageList);
		//imgAdapter.setNotifyOnChange(false);
		imgAdapter.setListItem(imageList);
		imgAdapter.setImageOriList(imageOri);
		imgGallery.setAdapter(imgAdapter);
		screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		int l_btnWidth = this.getWindowManager().getDefaultDisplay().getWidth()/6;
		LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(l_btnWidth, LinearLayout.LayoutParams.FILL_PARENT);
		rotateLeftBtn.setLayoutParams(shareParams);
		rotateRightBtn.setLayoutParams(shareParams);
		zoomInBtn.setLayoutParams(shareParams);
		zoomOutBtn.setLayoutParams(shareParams);
		slideShowBtn.setLayoutParams(shareParams);
		shareBtn.setLayoutParams(shareParams);
	}
	
	public static void staticRefreshUI() {
		//update image data for the gallery
		if (imgAdapter != null) {
			//imgAdapter.notifyDataSetChanged();
//			imgAdapter.insert(Viewer2.imageList.get(Viewer2.imageList.size()-1), Viewer2.imgAdapter.getCount());
		}
	}
    
    public int dpToPixels(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    
    
    public static List<String> imageList = new ArrayList<String>();
	public static List<Integer> imageOri = new ArrayList<Integer>();
	public static List<Integer> imageOriUpdateIndex = new ArrayList<Integer>();
    
    
    private Handler displayHandler;
    boolean mEnableReset = false;
    public void enableReset() {
    	mEnableReset = true;
    	displayHandler = new Handler();
		try {
			 Runnable mEnableTask = new Runnable() {
				public void run() {
					if (mEnableReset==false) {
						if (resetBtn.getVisibility() == View.GONE) {
							//already dismissed, no need to show the animation
						} else{
							Animation hideBtnAnimation =
						        new AlphaAnimation(1F, 0F);
							hideBtnAnimation.setDuration(1000);
							resetBtn.startAnimation(hideBtnAnimation);
							resetBtn.setVisibility(View.GONE);
						}
						displayHandler.removeCallbacks(this);
						return;
					}
					if (mEnableReset == true) { 
						resetBtn.setVisibility(View.VISIBLE);
						mEnableReset = false;
					}
					//show the reset button for 3 seconds
					displayHandler.postDelayed(this, 3000);
				}
			 };
			 displayHandler.removeCallbacks(mEnableTask);
			 displayHandler.postDelayed(mEnableTask, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private int menu_shown = 0;
    private void hideMenu() {
    	menu_shown = 0;
    	footbar.setVisibility(View.GONE);
    }
    
    private Handler mMenuHandler;
    private boolean mEnableMenu;
    private Runnable mEnableTask = new Runnable() {
		public void run() {
			if (mEnableMenu==false) {
				if (menu_shown == 0) {
					//already dismissed, no need to show the animation
				} else {
					Animation hideBtnAnimation =
				        new AlphaAnimation(1F, 0F);
					hideBtnAnimation.setDuration(1000);
					footbar.startAnimation(hideBtnAnimation);
					footbar.setVisibility(View.GONE);
					menu_shown = 0;
				}
				mMenuHandler.removeCallbacks(this);
				return;
			}
			if (mEnableMenu == true) { 
				if (menu_shown == 1) {
					//if menu is already shown
					mEnableMenu = false;
				} else {
					footbar.setVisibility(View.VISIBLE);
					mEnableMenu = false;
					menu_shown = 1;
				}
			}
			//show the reset button for 3 seconds
			mMenuHandler.postDelayed(this, 3000);
		}
	 };
    private void timedShowMenu() {
    	mEnableMenu = true;
    	if (mMenuHandler == null) {
    		mMenuHandler = new Handler();
    	} 
		try {
			 mMenuHandler.removeCallbacks(mEnableTask);
			 mMenuHandler.postDelayed(mEnableTask, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void sharePhoto() {
		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        ImageZoomView izv = (ImageZoomView) imgGallery.getSelectedView();
        String l_ext = imageList.get(izv.getIndex()).substring(
        		imageList.get(izv.getIndex()).lastIndexOf(".")+1).toLowerCase();
        intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(l_ext));
        Uri l_fileUri = Uri.fromFile(new File(imageList.get(izv.getIndex())));
        intent.putExtra(Intent.EXTRA_STREAM, l_fileUri);
        intent.putExtra("TSXQ", 0);		//for TS to ignore this intent
        try {
            startActivity(Intent.createChooser(intent, "Send Photo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "no program is found to share the photo!",
                    Toast.LENGTH_SHORT).show();
        }
	}
    
    private static int slideshow_interval = 3;
    private final Animation mHidePrevImageViewAnimation =
        new AlphaAnimation(1F, 0F);
    private final Animation mShowNextImageViewAnimation =
        new AlphaAnimation(0F, 1F);
	//slideshow 
	private static boolean is_in_slideshow = false;
	private static Handler slide_show_handler;
	private static int slideshow_ani_state = 0;
	private int mShowStyle = 0;
	private void slideShowSecrets() {
		if (imageList.size()==0) {
			Toast.makeText(mContext, "No Pictures for Slide Show!", Toast.LENGTH_SHORT).show();
			return;
		}	
		slideshow_interval = ViewerConfigStatic.getShowInterval(mContext);
		is_in_slideshow = true;
		//if it's in slideshow, hide the menu
		hideMenu();
		//slideshow_interval = ViewerConfigStatic.getShowInterval(mContext);
		slide_show_handler = new Handler();
		try {
			 Runnable mUpdatePictureTask = new Runnable() {
				//@Override
				public void run() {
					if (is_in_slideshow==false) {
						slide_show_handler.removeCallbacks(this);
						return;
					}
					if (slideshow_ani_state == 0) { 
						Animation a = mHidePrevImageViewAnimation;
						a.setDuration(1000);
						imgGallery.startAnimation(a);
					}
					if (slideshow_ani_state==1) {
						imgGallery.showNextPhoto(mShowStyle);
						Animation a = mShowNextImageViewAnimation;
						a.setDuration(1000);
						imgGallery.startAnimation(a);
					}
					
					if (is_in_slideshow) {
						if (slideshow_ani_state == 0) {
							slideshow_ani_state = 1;
							slide_show_handler.postDelayed(this, 1000);
						} else if (slideshow_ani_state == 1) {
							slideshow_ani_state = 0;
							slide_show_handler.postDelayed(this, (slideshow_interval)*1000);
						}
					} else {
						slide_show_handler.removeCallbacks(this);
					}
				}
			 };
			 slide_show_handler.removeCallbacks(mUpdatePictureTask);
			 slide_show_handler.postDelayed(mUpdatePictureTask, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub		
	}

	//@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	//single tap area
	private static int screenHeight;
	private static final int TAP_AREA_UP = 1;
	private static final int TAP_AREA_CENTER = 2;
	private static final int TAP_AREA_DOWN = 3;
	private static int tap_area = TAP_AREA_CENTER;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		if (is_in_slideshow) {
			//if it's in slideshow, a single tap will stop the slideshow
			is_in_slideshow = false;
			Toast.makeText(mContext, "Slideshow stopped!", Toast.LENGTH_SHORT).show();
		}
		float y = me.getY();
		if (y < screenHeight/5.0*1.0) {
			tap_area = TAP_AREA_UP;
		} else if (y > screenHeight/5.0*4.0) {
			  tap_area = TAP_AREA_DOWN;
		} else {
			  tap_area = TAP_AREA_CENTER;
		}
		gestureDetector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}
	//@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (menu_shown==0) {
			if (tap_area == TAP_AREA_CENTER) {
				timedShowMenu();
			}
		} else {
			//if menu is shown already, only when the event occurs at center area 
			//will cause the menu to hide
			if (tap_area == TAP_AREA_CENTER) {
				hideMenu();
			} 
		}
		return false;
	}
	
	private static final int REQUEST_SELECT_SHOW_STYLE = 0;
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	switch (requestCode) {
    		case REQUEST_SELECT_SHOW_STYLE:
    		if (resultCode == RESULT_OK) {
    			Bundle bundle = intent.getExtras();
    			boolean lDef = false;
    			if (bundle != null) {
    				mShowStyle = bundle.getInt(ShowStyleSelectionDialog.RESPONSE_SHOW_STYLE, 0);
    				lDef = bundle.getBoolean(ShowStyleSelectionDialog.RESPONSE_SHOW_STYLE_DEF, true);
    			}
        		if (lDef) {
        			if (mShowStyle == 0) {
        				ViewerConfigStatic.setShowStyle(this.getApplicationContext(), 1);
        			} else if (mShowStyle == 1) {
        				ViewerConfigStatic.setShowStyle(this.getApplicationContext(), 2);
        			}
        		} 
        		slideShowSecrets();
    		}
    		break;
    	}
	}
	
	/**
	 * The services control
	 */
	private void startBackendService() {
		Intent l_intent = new Intent(mContext, ViewerBackendService.class);
		startService(l_intent);
	}
	
	private void stopBackendService() {
		Intent l_intent = new Intent(mContext, ViewerBackendService.class);
		stopService(l_intent);
	}
	
	private void startLoadService() {
		Intent l_intent = new Intent(mContext, ViewerLoadService.class);
		startService(l_intent);
	}
	
	private void stopLoadService() {
		Intent l_intent = new Intent(mContext, ViewerLoadService.class);
		stopService(l_intent);
	}
}