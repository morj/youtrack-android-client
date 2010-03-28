package jetbrains.android.client;

import android.*;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import jetbrains.android.data.RequestFailedException;
import jetbrains.android.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends ListActivity {
    private static final int SUCCESS_CODE = 200;
    private static final YouTrackDAO dao = new YouTrackDAO();
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private final String[] from = new String[]{"id", "summary"};
    private final int[] to = new int[]{R.id.issue_id, R.id.summary};
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
        updateQuery(false, false);

        dataAdapter = new SimpleAdapter(this, data, R.layout.issue_list_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView idView = (TextView) view.findViewById(R.id.issue_id);
                TextView summaryView = (TextView) view.findViewById(R.id.summary);
                
                if (isResolved(position)) {
                    idView.setPaintFlags(idView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    idView.setEnabled(false);
                    summaryView.setEnabled(false);
                } else {
                    idView.setPaintFlags(idView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    idView.setEnabled(true);
                    summaryView.setEnabled(true);
                }
                return view;
            }

            public boolean isResolved(int position) {
                String state = getField(position, "state");
                // TODO: remove this hardcode when http://youtrack.jetbrains.net/issue/JT-5206 resolved
                return state != null &&
                        (  "Can't Reproduce".equalsIgnoreCase(state)
                        || "Duplicate".equalsIgnoreCase(state)
                        || "Fixed".equalsIgnoreCase(state)
                        || "Won't fix".equalsIgnoreCase(state)
                        || "To be discussed".equalsIgnoreCase(state)
                        || "Incomplete".equalsIgnoreCase(state)
                        || "Obsolete".equalsIgnoreCase(state)
                        || "Verified".equalsIgnoreCase(state));
            }

            public Map<String, Object> getItem(int position) {
                return (Map<String, Object>) super.getItem(position);
            }

            public<T> T getField(int position, String fieldName) {
                Map<String, Object> item = getItem(position);
                Object field = null;
                if (item != null) {
                    field = item.get(fieldName);
                }
                return (T) field;
            }
        };
        final ListView lv = getListView();

        lv.setAdapter(dataAdapter);

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Intent viewIssue = new Intent()
                        .setClass(YouTrackActivity.this, IssueActivity.class)
                        .setAction(Intent.ACTION_VIEW);
                startActivity(viewIssue);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            updateQuery(true, false);
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
                updateQuery(true, true);
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
        startSearch(query, false, null, false);
        return true;
    }

    @Override
    protected void onDestroy() {
//        dao.destroy();
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

    private void updateQuery(boolean refreshView, boolean reload) {
        data.clear();
        try {
            data.addAll(dao.getIssues("JT", query, 0, 10, reload));
        } catch (RequestFailedException ignore) {
            //ignore
        }
        if (refreshView) {
            dataAdapter.notifyDataSetChanged();
        }
    }

}
