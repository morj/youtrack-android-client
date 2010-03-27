package jetbrains.android.client;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import jetbrains.android.data.RequestFailedException;
import jetbrains.android.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends Activity {
    private static final int SUCCESS_CODE = 200;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private final YouTrackDAO dao = new YouTrackDAO();
    private final String[] from = new String[]{"title", "description"};
    private final int[] to = new int[]{R.id.title, R.id.description};
    private SimpleAdapter dataAdapter;
    private String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load default values to preferences on app start
        PreferenceManager.setDefaultValues(this, R.xml.youtrack_prefrences, false);
        login();

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            query = getIntent().getStringExtra(SearchManager.QUERY);
        } else {
            query = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.user_filter_preference), "");
        }
        updateQuery(false);

        dataAdapter = new SimpleAdapter(this, data, R.layout.issue_list_item, from, to);
        final ListView lv = new ListView(this);

        lv.setAdapter(dataAdapter);
        setContentView(lv);

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Toast.makeText(YouTrackActivity.this, "Here: " + e1.getY(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        lv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            updateQuery(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.youtrack_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit_item:
                finish();
                return true;
            case R.id.update_item:
                updateQuery(true);
                return true;
            case R.id.filter_item:
                onSearchRequested();
                return true;
            case R.id.options_item:
                Intent preferencesIntent = new Intent().setClass(this, YouTrackPreference.class);
                //Call subactivity with preferences
                startActivityForResult(preferencesIntent, SUCCESS_CODE);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        if (requestCode == SUCCESS_CODE) {
            login();
        }
    }

    @Override
    public boolean onSearchRequested() {
        String query = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.user_filter_preference), null);
        startSearch(query, false, null, false);
        return true;
    }

    @Override
    protected void onDestroy() {
        dao.destroy();
        super.onDestroy();
    }

    private void login() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String login = preferences.getString(getString(R.string.user_name_preference), null);
            String pass = preferences.getString(getString(R.string.user_password_preference), null);
            String host = preferences.getString(getString(R.string.host_url_preference_title), null);
            dao.login(host, login, pass);
        } catch (RequestFailedException e) {
            //TODO: Graceful handle
        }
    }

    private void updateQuery(boolean notify) {
        data.clear();
        try {
            data.addAll(dao.getIssues("JT", query, 0, 10));
        } catch (RequestFailedException ignore) {
            //ignore
        }
        if (notify) {
            dataAdapter.notifyDataSetChanged();
        }
    }

}
