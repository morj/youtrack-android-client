package jetbrains.android.client;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import jetbrains.android.data.RequestFailedException;
import jetbrains.android.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends ListActivity {
    private static final String BASE_URI = "http://youtrack.jetbrains.net";
    private View currentView = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (MenuItem item : MenuItem.values()) {
            menu.add(Menu.NONE, item.id, item.order, item.titleId);
        }
        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        return super.onMenuItemSelected(featureId, item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setListAdapter(new ArrayAdapter<String>(this, R.layout.issue_list_item, new String[]{"first", "second", "third"}));
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        try {
            YouTrackDAO dao = new YouTrackDAO();
            dao.login(BASE_URI, "app_exception", "app_exception");
            data.addAll(dao.getIssues("JT", "#unresolved", 0, 10));
        } catch (RequestFailedException e) {
            //TODO: Graceful handle
        }
        String[] from = new String[]{"title", "description"};
        int[] to = new int[]{R.id.title, R.id.description};
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.issue_list_item, from, to);
        setListAdapter(adapter);

        final ListView lv = getListView();
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Toast.makeText(YouTrackActivity.this, "Here: " + e1.getY(), Toast.LENGTH_SHORT);
                return true;
            }
        });
        /*lv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });*/

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                updateCurrentView(view, position, data);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                dismissCurrentView();
            }
        };
        lv.setOnItemSelectedListener(listener);
//        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

       /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                updateCurrentView(view, position, data);
            }
        });*/
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

}
