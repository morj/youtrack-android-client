package jetbrains.android.client;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.*;
import jetbrains.android.data.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Zeckson
 * Date: Feb 10, 2010
 * Time: 1:28:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class YouTrackActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setListAdapter(new ArrayAdapter<String>(this, R.layout.issue_list_item, new String[]{"first", "second", "third"}));
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Issue issue: Issue.getSampleData()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            data.add(map);
            map.put("issue_id", issue.getId());
            map.put("summary", issue.getSummary());
            map.put("description", issue.getDescription());
            map.put("selected", Boolean.FALSE);
        }
        String[] from = new String[]{"issue_id", "summary", "description", "selected"};
        int[] to = new int[]{R.id.issue_id, R.id.summary, R.id.description, R.id.issueSelected};
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.issue_list_item, from, to);
        setListAdapter(adapter);

        ListView lv = getListView();
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

}
