package uplb.cas.ics.phporktraceability;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;
import listeners.OnSwipeTouchListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by marmagno on 11/11/2015.
 */


public class LocationPage extends AppCompatActivity
        implements View.OnTouchListener, View.OnDragListener{

    public final static String KEY_LOCID = "loc_id";
    public final static String KEY_LOCNAME= "loc_name";
    public final static String KEY_LOCADD = "address";
    private static final String LOGCAT = LocationPage.class.getSimpleName();
    ViewPager viewPager;
    PagerAdapter adapter;
//    LinearLayout ll;
    LinearLayout bl;
    TextView tv_title;
    ImageView iv_left, iv_right;
    SQLiteHandler db;
    SessionManager session;
    String[] lists = {};
    String[] lists2 = {};
    String[] lists3 = {};
    String[] ids = {};
    String function = "";
    String location = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewpager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton home = (ImageButton) toolbar.findViewById(R.id.home_logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(LocationPage.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        TextView pg_title = (TextView) toolbar.findViewById(R.id.page_title);
        pg_title.setText(R.string.location);

        db = SQLiteHandler.getInstance();

        session = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        function = i.getStringExtra("function");

        loadLists();

        bl = (LinearLayout) findViewById(R.id.bottom_container);

        bl.setOnDragListener(this);
        bl = (LinearLayout) findViewById(R.id.bottom_container);
        bl.setOnDragListener(this);
        bl.setOnTouchListener(new OnSwipeTouchListener(LocationPage.this) {
            @Override
            public void onSwipeLeft() {
                nextItem();
            }

            @Override
            public void onSwipeRight(){
                prevItem();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new CustomPagerAdapter(LocationPage.this, lists, lists2, lists3, ids);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    int currentPage = position + 1;
                    if (currentPage == 1) {
                        iv_left.setVisibility(View.INVISIBLE);
                        iv_right.setVisibility(View.VISIBLE);
                    } else if (currentPage == lists.length) {

                        iv_left.setVisibility(View.VISIBLE);
                        iv_right.setVisibility(View.INVISIBLE);
                    } else {
                        iv_left.setVisibility(View.VISIBLE);
                        iv_right.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        iv_left = (ImageView)findViewById(R.id.iv_left);
        iv_right = (ImageView)findViewById(R.id.iv_right);

        checkList();

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevItem();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextItem();

            }
        });

        tv_title = (TextView) findViewById(R.id.tv_title);
        String title = "Swipe to Choose a Farm";
        tv_title.setText(title);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        if(fab != null) {
//            fab.setVisibility(View.VISIBLE);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addFarm();
//                }
//            });
//        } else {
//            Log.e(LOGCAT, "fab is null.");
//        }
    }

    public void checkList(){
        int count = viewPager.getCurrentItem();
        if(count + 1 < lists.length){
            iv_right.setVisibility(View.VISIBLE);
        }

    }

    public void nextItem() {
        int item = viewPager.getCurrentItem();
        viewPager.setCurrentItem(item + 1);
    }

    public void prevItem() {
        int item = viewPager.getCurrentItem();
        viewPager.setCurrentItem(item - 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            //noinspection SimplifiableIfStatement
//            case R.id.action_help:
//            	 show_help();
//            	 return true;
            case android.R.id.home:
                Intent i = new Intent(LocationPage.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return true;
            case R.id.action_logout:
                session.logoutUser();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadLists(){

        ArrayList<HashMap<String, String>> list = db.getLocs(function);
        int size = list.size();
        if(size > 0) {

            lists = new String[size];
            lists2 = new String[size];
            lists3 = new String[size];
            ids = new String[size];
            for (int i = 0; i < size; i++) {
                HashMap<String, String> c = list.get(i);

                lists[i] = "Farm: " + c.get(KEY_LOCNAME);
                lists2[i] = "Description: " + c.get(KEY_LOCADD);
                lists3[i] = "";
                ids[i] = c.get(KEY_LOCID);
            }
        }
        else{

            final int SPLASH_TIME_OUT = 1000;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No data available.")
                    .setMessage("Please update your database. Cannot proceed any further.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int id) {

                            new Handler().postDelayed(new Runnable() {

                                /*
                                 * Showing splash screen with a timer. This will be useful when you
                                 * want to show case your app logo / company
                                 */
                                @Override
                                public void run() {
                                    // This method will be executed once the timer is over
                                    // Start your app main activity
                                    Intent i = new Intent(LocationPage.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();

                                    dialog.cancel();
                                }
                            }, SPLASH_TIME_OUT);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent e) {
        int action = e.getAction();
        switch(action){
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d(LOGCAT, "Drag event started");
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d(LOGCAT, "Drag event entered into "+ v.toString());
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d(LOGCAT, "Drag event exited from " + v.toString());
                break;
            case DragEvent.ACTION_DROP:
                TextView tv_drag = (TextView) findViewById(R.id.tv_dragHere);
                View view = (View) e.getLocalState();
                ViewGroup from = (ViewGroup) view.getParent();
                from.removeView(view);
                view.invalidate();
                LinearLayout to = (LinearLayout) v;
                to.addView(view);
                to.removeView(tv_drag);
                view.setVisibility(View.VISIBLE);

                int id = view.getId();
                location = view.findViewById(id).getTag().toString();

                int vid = to.getId();
                if(findViewById(vid) == findViewById(R.id.bottom_container)){
                    session.setLocation(function, location);

                    Intent i = new Intent(LocationPage.this, ChooseModule.class);
                    startActivity(i);
                    finish();
                }

                Log.d(LOGCAT, "Dropped " + location);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(LOGCAT, "Drag ended");
                break;
            default:
                break;
        }
        return true;

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(null, shadowBuilder, v, 0);
            } else
                v.startDrag(null, shadowBuilder, v, 0);

            return true;
        }
        else { return false; }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(LocationPage.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    /*
    public void show_help(){
        Intent intent = new Intent(this,HelpPage.class);
        intent.putExtra("help_page", 2);
        startActivity(intent);
    }

    public void addFarm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View subView = inflater.inflate(R.layout.add_farm_fragment,null);
        final EditText et_farmname = (EditText) subView.findViewById(R.id.et_farm_name);
        final EditText et_farmid = (EditText) subView.findViewById(R.id.et_farm_id);

        builder.setTitle("Add a farm");
        builder.setView(subView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){

                String farm_name = et_farmname.getText().toString();
                String farm_add = (function.equals("weaning"))? "Weaning Farm": "Growing Farm";
                String farm_id = et_farmid.getText().toString();
                try {
                    db.addLoc(farm_id, farm_name, farm_add);
                    Toast.makeText(LocationPage.this, "Farm Added", Toast.LENGTH_SHORT).show();
                    refresh();
                } catch (Exception e) {
                    Toast.makeText(LocationPage.this, "Error on inputs.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog contact_prompt = builder.create();
        contact_prompt.show();


    }

    public void refresh() {
        loadLists();
        adapter = new CustomPagerAdapter(LocationPage.this, lists, lists2, lists3, ids);
        viewPager.setAdapter(adapter);
    }
    */
}
