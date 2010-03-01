package jetbrains.android.client;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Zeckson
 * Date: Feb 28, 2010
 * Time: 11:52:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class YouTrackPreference extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.youtrack_prefrences);
    }
}
