package com.paintcoat.planner.core.ui

import java.util.Locale

fun formatOneDecimal(value: Float): String = String.format(Locale.US, "%.1f", value)
