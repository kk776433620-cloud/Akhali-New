package com.akhali.smartassistant.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class SystemControlAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        analyzeNode(rootNode)
    }

    private fun analyzeNode(node: AccessibilityNodeInfo) {
        if (node.text != null) {
            Log.d("Accessibility", "Text found: ${node.text}")
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) analyzeNode(child)
        }
    }

    fun performGlobalBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performGlobalHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun clickOnViewByText(text: String) {
        val nodes = rootInActiveWindow?.findAccessibilityNodeInfosByText(text)
        nodes?.firstOrNull()?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    override fun onInterrupt() {
        Log.e("Accessibility", "Service Interrupted")
    }
}
