package uplb.cas.ics.phporktraceability;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by marmagno on 11/11/2015.
 */
public class ChooseFeedHousePage extends AppCompatActivity implements View.OnDragListener
{
    private static final String LOGCAT = ChooseFeedHousePage.class.getSimpleName();
    private Toolbar toolbar;
    ViewPager viewPager;
    PagerAdapter adapter;
    LinearLayout ll;
    LinearLayout bl;

    SQLiteHandler db;
    String pen = "";
    String feed_id = "";

    public final static String KEY_PENID = "pen_id";
    public final static String KEY_PENNO = "pen_no";
    public final static String KEY_FUNC = "function";
    public final static String KEY_GNAME = "group_name";

    SessionManager session;
    ArrayList<HashMap<String, String>> pen_list;
    String[] lists;
    String[] lists2;
    String[] lists3;
    String[] ids;
    String location= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignpen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_phpork);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserSession();
        location = user.get(SessionManager.KEY_LOC);

        Intent i = getIntent();
        feed_id = i.getStringExtra("feed_id");

        db = new SQLiteHandler(getApplicationContext());

        loadLists();

        ll = (LinearLayout) findViewById(R.id.top_container);
        bl = (LinearLayout) findViewById(R.id.bottom_container);
        //ll.setOnDragListener(this);
        bl.setOnDragListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setOnDragListener(this);
        adapter = new CustomPagerAdapter2(ChooseFeedHousePage.this, lists, lists2, lists3, ids);
        viewPager.setAdapter(adapter);

    }


    public void loadLists(){

        ArrayList<HashMap<String, String>> the_list = db.getFeedPens(location);

        pen_list = new ArrayList<>();
        lists = new String[the_list.size()];
        lists2 = new String[the_list.size()];
        lists3 = new String[the_list.size()];
        ids = new String[the_list.size()];
        for(int i = 0;i < the_list.size();i++)
        {
            HashMap<String, String> c = the_list.get(i);

            lists[i] = "Pen: " + c.get(KEY_PENNO);
            lists[i] = "Function: " + c.get(KEY_FUNC);
            lists[i] = "Group Name: " + c.get(KEY_GNAME);
            ids[i] = c.get(KEY_PENID);
            pen_list.add(c);
        }
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
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                Intent i = new Intent(ChooseFeedHousePage.this, ChooseFeedPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
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
                pen = view.findViewById(id).getTag().toString();

                int vid = to.getId();
                if(findViewById(vid) == findViewById(R.id.bottom_container)){
                    Toast.makeText(ChooseFeedHousePage.this, "Chosen " + pen,
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ChooseFeedHousePage.this, AddFeedPig.class);
                    i.putExtra("pen", pen);
                    i.putExtra("feed_id", feed_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

                Log.d(LOGCAT, "Dropped " + pen);
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
    public void onBackPressed(){super.onBackPressed(); finish(); }
}