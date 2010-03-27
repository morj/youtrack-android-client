package jetbrains.android.client;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class IssueActivity extends TabActivity {
    public static final String ISSUE = "ISSUE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String issue = getIntent().getStringExtra(ISSUE);

        TabHost th = getTabHost();

        LayoutInflater.from(this).inflate(R.layout.issue_properties, th.getTabContentView(), true);

        th.addTab(th.newTabSpec("Properties")
                .setIndicator("Properties")
                .setContent(R.id.issue_properties));
        th.addTab(th.newTabSpec("Description")
                .setIndicator("Description")
                .setContent(R.id.issue_description));
        th.addTab(th.newTabSpec("Comments")
                .setIndicator("Comments")
                .setContent(R.id.issue_comments));
    }
}
