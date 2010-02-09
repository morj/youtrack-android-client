package jetbrains.android.client;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: Zeckson
 * Date: Feb 10, 2010
 * Time: 1:28:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class YouTrackActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
