package com.paintcoat.planner.data.local

import android.content.Context
import android.content.SharedPreferences

class PlannerPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun readRooms(): String = prefs.getString(KEY_ROOMS, "").orEmpty()

    fun writeRooms(value: String) {
        prefs.edit().putString(KEY_ROOMS, value).apply()
    }

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback).orEmpty()

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readDouble(key: String, fallback: Double): Double =
        prefs.getFloat(key, fallback.toFloat()).toDouble()

    fun writeDouble(key: String, value: Double) {
        prefs.edit().putFloat(key, value.toFloat()).apply()
    }

    fun clearSettings() {
        prefs.edit()
            .remove(KEY_UNITS)
            .remove(KEY_DEFAULT_COVERAGE)
            .remove(KEY_DEFAULT_COATS)
            .remove(KEY_DEFAULT_TEXTURE)
            .remove(KEY_PRIMER_COVERAGE)
            .remove(KEY_THEME_MODE)
            .apply()
    }

    fun nextRoomIndex(): Int {
        val next = prefs.getInt(KEY_ROOM_COUNTER, 0) + 1
        prefs.edit().putInt(KEY_ROOM_COUNTER, next).apply()
        return next
    }

    companion object {
        private const val FILE_NAME = "paint_coat_planner"
        private const val KEY_ROOMS = "rooms"
        private const val KEY_SEEDED = "seeded"
        private const val KEY_ROOM_COUNTER = "room_counter"
        const val KEY_UNITS = "units"
        const val KEY_DEFAULT_COVERAGE = "default_coverage"
        const val KEY_DEFAULT_COATS = "default_coats"
        const val KEY_DEFAULT_TEXTURE = "default_texture"
        const val KEY_PRIMER_COVERAGE = "primer_coverage"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
