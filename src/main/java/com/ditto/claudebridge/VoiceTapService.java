package com.ditto.claudebridge;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class VoiceTapService extends AccessibilityService {

    public static volatile boolean pending = false;
    public static volatile long pendingSince = 0L;
    private static final String CLAUDE_PKG = "com.anthropic.claude";
    private static final String[] KEYWORDS = {"voice", "start voice", "voice mode"};

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!pending) return;
        if (System.currentTimeMillis() - pendingSince > 10000) { pending = false; return; }
        if (event == null || event.getPackageName() == null) return;
        if (!CLAUDE_PKG.equals(event.getPackageName().toString())) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        AccessibilityNodeInfo node = findVoiceButton(root);
        while (node != null && !node.isClickable()) node = node.getParent();
        if (node != null && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
            pending = false;
        }
    }

    private AccessibilityNodeInfo findVoiceButton(AccessibilityNodeInfo node) {
        String desc = node.getContentDescription() == null ? "" : node.getContentDescription().toString().toLowerCase();
        String text = node.getText() == null ? "" : node.getText().toString().toLowerCase();
        for (String k : KEYWORDS) {
            if (desc.contains(k) || text.contains(k)) return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child == null) continue;
            AccessibilityNodeInfo found = findVoiceButton(child);
            if (found != null) return found;
        }
        return null;
    }

    @Override
    public void onInterrupt() {}
}
