package com.wycherley.trackmybus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.wycherley.trackmybus.models.BusDriver;

/**
 * Manages parent's selected bus preference
 */
public class ParentBusPreference {
    private static final String PREF_NAME = "ParentBusPrefs";
    private static final String KEY_SELECTED_BUS = "selected_bus";
    private static final String KEY_SELECTED_BUS_ID = "selected_bus_id";

    private final SharedPreferences prefs;
    private final Gson gson;

    public ParentBusPreference(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Save selected bus driver
     */
    public void setSelectedBus(BusDriver driver) {
        String json = gson.toJson(driver);
        prefs.edit()
                .putString(KEY_SELECTED_BUS, json)
                .putString(KEY_SELECTED_BUS_ID, driver.getId())
                .apply();
    }

    /**
     * Get selected bus driver
     */
    public BusDriver getSelectedBus() {
        String json = prefs.getString(KEY_SELECTED_BUS, null);
        if (json != null) {
            return gson.fromJson(json, BusDriver.class);
        }
        return null;
    }

    /**
     * Get selected bus ID
     */
    public String getSelectedBusId() {
        return prefs.getString(KEY_SELECTED_BUS_ID, null);
    }

    /**
     * Check if bus is selected
     */
    public boolean hasSelectedBus() {
        return prefs.contains(KEY_SELECTED_BUS);
    }

    /**
     * Check if this bus is the selected one
     */
    public boolean isSelectedBus(String busId) {
        String selectedId = getSelectedBusId();
        return selectedId != null && selectedId.equals(busId);
    }

    /**
     * Clear selected bus
     */
    public void clearSelectedBus() {
        prefs.edit()
                .remove(KEY_SELECTED_BUS)
                .remove(KEY_SELECTED_BUS_ID)
                .apply();
    }
}