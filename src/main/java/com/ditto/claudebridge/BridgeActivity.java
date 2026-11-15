package com.ditto.claudebridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BridgeActivity extends Activity {
    static final String PREFS = "bridge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String claude = "com.anthropic.claude";

        // Leave a note for the accessibility service to tap the voice button
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
            .putLong("requestTime", System.currentTimeMillis()).apply();

        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

        Intent assist = new Intent(Intent.ACTION_ASSIST);
        assist.setPackage(claude);
        assist.addFlags(flags);
        try {
            startActivity(assist);
        } catch (Exception e) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(claude);
            if (launch != null) {
                launch.addFlags(flags);
                startActivity(launch);
            }
        }
        finish();
    }
}
