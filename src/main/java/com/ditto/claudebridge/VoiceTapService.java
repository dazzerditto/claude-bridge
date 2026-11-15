// VoiceTapService.java — keeps looking for the voice button, then taps it
public class VoiceTapService extends AccessibilityService {
    private final Handler h = new Handler(Looper.getMainLooper());
    private long handledTime = 0;

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {
        long req = getSharedPreferences(AssistActivity.PREFS, MODE_PRIVATE)
                       .getLong("requestTime", 0);
        if (req == 0 || req == handledTime) return;
        if (System.currentTimeMillis() - req > 6000) return;
        handledTime = req;
        attempt(0, false);
    }

    private void attempt(int tries, boolean triedNewChat) {
        if (tries > 30) return;                                  // ~6 s, give up
        AccessibilityNodeInfo root = getRootInActiveWindow();
        boolean nc = triedNewChat;
        if (root != null && "com.anthropic.claude".contentEquals(root.getPackageName())) {
            if (clickByDesc(root, "voice")) return;              // done
            if (tries > 10 && !nc) {                             // still an old chat? force new
                nc = clickByDesc(root, "new chat");
            }
        }
        final boolean f = nc;
        h.postDelayed(() -> attempt(tries + 1, f), 200);
    }

    private boolean clickByDesc(AccessibilityNodeInfo n, String word) {
        if (n == null) return false;
        CharSequence d = n.getContentDescription();
        if (d != null && d.toString().toLowerCase().contains(word)) {
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
