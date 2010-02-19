package jetbrains.android.client;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import jetbrains.android.data.RequestFailedException;
import jetbrains.android.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends ListActivity {
    private static final String BASE_URI = "http://youtrack.jetbrains.net";

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
        lv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private View currentView = null;

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != currentView) {
                    if (currentView != null) {
                        getTitle(currentView).setLines(1);
                        getDescription(currentView).setVisibility(View.GONE);
                    }

                    getTitle(view).setMaxLines(20);
                    TextView desc = getDescription(view);
                    desc.setVisibility(View.VISIBLE);
                    if ("".equals(data.get(position).get("description"))) {
                        desc.setText("<no description>");
                    }

                    currentView = view;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                if (currentView != null) {
                    getTitle(currentView).setLines(1);
                    getDescription(currentView).setVisibility(View.GONE);
                }
            }
        });
//        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//           lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//               public void onItemClick(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                   // When clicked, show a toast with the TextView text
//                   Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//                           Toast.LENGTH_SHORT).show();
//               }
//           });
    }

    private TextView getTitle(View itemView) {
        return (TextView) itemView.findViewById(R.id.title);
    }

    private TextView getDescription(View itemView) {
        return (TextView) itemView.findViewById(R.id.description);
    }

}
