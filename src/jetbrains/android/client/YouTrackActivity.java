package jetbrains.android.client;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import jetbrains.android.data.RequestFailedException;
import jetbrains.android.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends ListActivity {
    private static final int SUCCESS_CODE = 200;
    private View currentView = null;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private final YouTrackDAO dao = new YouTrackDAO();
    private final String[] from = new String[]{"title", "description"};
    private final int[] to = new int[]{R.id.title, R.id.description};
    private SimpleAdapter dataAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.youtrack_menu, menu);        
        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {

        switch (item.getItemId()) {
            case R.id.quit_item:
                finish();
                return true;
            case R.id.update_item:
                updateData(true);
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
        if(requestCode == SUCCESS_CODE)
            updateData(true);
    }

    @Override
    public boolean onSearchRequested() {
        String query = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.user_filter_preference), null);
        startSearch(query, false, null, false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            updateQuery(true, intent.getStringExtra(SearchManager.QUERY));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Load default values to preferences on app start
        PreferenceManager.setDefaultValues(this, R.xml.youtrack_prefrences, false);

//        setListAdapter(new ArrayAdapter<String>(this, R.layout.issue_list_item, new String[]{"first", "second", "third"}));
        updateData(false);
        dataAdapter = new SimpleAdapter(this, data, R.layout.issue_list_item, from, to);
        setListAdapter(dataAdapter);

        final ListView lv = getListView();
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

/*
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                updateCurrentView(view, position, data);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                dismissCurrentView();
            }
        };
        lv.setOnItemSelectedListener(listener);
*/
//        lv.setTextFilterEnabled(true);
//        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                updateCurrentView(view, position, data);
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        dao.destroy();
        super.onDestroy();
    }

    private void updateCurrentView(View view, int position, List<Map<String, Object>> data) {
        if (view != currentView) {
            dismissCurrentView();

            getTitle(view).setMaxLines(20);
            TextView desc = getDescription(view);
            desc.setVisibility(View.VISIBLE);
            if ("".equals(data.get(position).get("description"))) {
                desc.setText("<no description>");
            }

            currentView = view;
        }
    }

    private void dismissCurrentView() {
        if (currentView != null) {
            getTitle(currentView).setLines(1);
            getDescription(currentView).setVisibility(View.GONE);
            currentView = null;
        }
    }

    private TextView getTitle(View itemView) {
        return (TextView) itemView.findViewById(R.id.title);
    }

    private TextView getDescription(View itemView) {
        return (TextView) itemView.findViewById(R.id.description);
    }

    private void updateData(boolean notify) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String login = preferences.getString(getString(R.string.user_name_preference), null);
            String pass = preferences.getString(getString(R.string.user_password_preference), null);
            String host = preferences.getString(getString(R.string.host_url_preference_title), null);
            dao.login(host, login, pass);

            String filter = preferences.getString(getString(R.string.user_filter_preference), null);
            updateQuery(notify, filter);
        } catch (RequestFailedException e) {
            //TODO: Graceful handle
        }
    }

    private void updateQuery(boolean notify, String filter) {
        data.clear();
        try {
            data.addAll(dao.getIssues("JT", filter, 0, 10));
        } catch (RequestFailedException ignore) {
            //ignore
        }
        if(notify)
            dataAdapter.notifyDataSetChanged();
    }

}
