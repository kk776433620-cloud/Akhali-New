package com.akhali.smartassistant.engine

import android.view.accessibility.AccessibilityNodeInfo

class PredictiveEngine {

    fun predictNextAction(nodes: List<AccessibilityNodeInfo>, history: List<String>): String {
        val currentContext = nodes.mapNotNull { it.text?.toString() }.joinToString(" ")

        return when {
            currentContext.contains("حساب", ignoreCase = true) -> "OPEN_CALCULATOR"
            currentContext.contains("إعدادات", ignoreCase = true) -> "SHOW_SETTINGS_GUIDE"
            history.lastOrNull() == "SEARCH_PRODUCT" -> "COMPARE_PRICES"
            else -> "STAY_IDLE"
        }
    }

    fun getContextSummary(rootNode: AccessibilityNodeInfo?): String {
        val sb = StringBuilder()
        extractText(rootNode, sb)
        return sb.toString()
    }

    private fun extractText(node: AccessibilityNodeInfo?, sb: StringBuilder) {
        if (node == null) return
        if (node.text != null) sb.append("${node.text} ")
        for (i in 0 until node.childCount) {
            extractText(node.getChild(i), sb)
        }
    }
}
