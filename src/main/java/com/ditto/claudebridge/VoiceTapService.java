package com.ditto.claudebridge;
import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class VoiceTapService extends AccessibilityService {
    private final Handler h = new Handler(Looper.getMainLooper());
    private long handledTime = 0;

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {
        long req = getSharedPreferences(BridgeActivity.PREFS, MODE_PRIVATE)
                       .getLong("requestTime", 0);
        if (req == 0 || req == handledTime) return;
        if (System.currentTimeMillis() - req > 6000) return;
        handledTime = req;
        attempt(0, false);
    }

    // Phase 1: tap "new chat". Phase 2: tap voice.
    private void attempt(int tries, boolean newChatDone) {
        if (tries > 40) return;                       // ~8 s, give up
        AccessibilityNodeInfo root = getRootInActiveWindow();
        boolean done = newChatDone;
        if (root != null && "com.anthropic.claude".contentEquals(root.getPackageName())) {
            if (!done) {
                if (clickByDesc(root, "new chat")) {
                    done = true;                      // wait for new chat to load
                } else if (tries > 8) {
                    done = true;                      // no button: already on a new chat
                }
            } else if (clickByDesc(root, "voice")) {
                return;                               // in voice, finished
            }
        }
        final boolean d = done;
        h.postDelayed(() -> attempt(tries + 1, d), 200);
    }

    private boolean clickByDesc(AccessibilityNodeInfo n, String word) {
        if (n == null) return false;
        CharSequence d = n.getContentDescription();
        CharSequence t = n.getText();
        String s = ((d != null ? d : "") + " " + (t != null ? t : "")).toLowerCase();
        if (s.contains(word)) {
            AccessibilityNodeInfo c = n;
            while (c != null && !c.isClickable()) c = c.getParent();
            if (c != null) return c.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        for (int i = 0; i < n.getChildCount(); i++)
            if (clickByDesc(n.getChild(i), word)) return true;
        return false;
    }

    @Override public void onInterrupt() {}
}
