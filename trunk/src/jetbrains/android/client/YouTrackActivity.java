package jetbrains.android.client;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

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

           super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
           setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, new String[]{"first", "second", "third"}));

           ListView lv = getListView();
           lv.setTextFilterEnabled(true);

           lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               public void onItemClick(AdapterView<?> parent, View view,
                                       int position, long id) {
                   // When clicked, show a toast with the TextView text
                   Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                           Toast.LENGTH_SHORT).show();
               }
           });
       }

}
