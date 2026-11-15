package com.ditto.claudebridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BridgeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String claude = "com.anthropic.claude";

        // Tell the accessibility service to tap the voice button
        VoiceTapService.pending = true;
        VoiceTapService.pendingSince = System.currentTimeMillis();

        Intent assist = new Intent(Intent.ACTION_ASSIST);
        assist.setPackage(claude);
        assist.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(assist);
        } catch (Exception e) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(claude);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launch);
            }
        }
        finish();
    }
}
