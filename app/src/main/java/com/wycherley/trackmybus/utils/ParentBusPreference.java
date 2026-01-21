package com.wycherley.trackmybus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.wycherley.trackmybus.models.BusDriver;

public class ParentBusPreference {
    private static final String PREF_NAME = "ParentBusPrefs";
    private static final String KEY_SELECTED_BUS_ID = "selected_bus_id";
    private static final String KEY_BUS_NUMBER = "bus_number";
    private static final String KEY_DRIVER_NAME = "driver_name";
    private static final String KEY_FROM_LOCATION = "from_location";
    private static final String KEY_TO_LOCATION = "to_location";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences preferences;

    public ParentBusPreference(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save the selected bus driver (saves all details)
     */
    public void setSelectedBus(BusDriver busDriver) {
        SharedPreferences.Editor editor = preferences.edit();

        if (busDriver != null) {
            editor.putString(KEY_SELECTED_BUS_ID, busDriver.getId());
            editor.putString(KEY_BUS_NUMBER, busDriver.getBusNumber());
            editor.putString(KEY_DRIVER_NAME, busDriver.getDriverName());
            editor.putString(KEY_FROM_LOCATION, busDriver.getFromLocation());
            editor.putString(KEY_TO_LOCATION, busDriver.getToLocation());
            editor.putString(KEY_PHONE_NUMBER, busDriver.getPhoneNumber());
            editor.putString(KEY_EMAIL, busDriver.getEmail());
        } else {
            // Clear all data if null
            editor.remove(KEY_SELECTED_BUS_ID);
            editor.remove(KEY_BUS_NUMBER);
            editor.remove(KEY_DRIVER_NAME);
            editor.remove(KEY_FROM_LOCATION);
            editor.remove(KEY_TO_LOCATION);
            editor.remove(KEY_PHONE_NUMBER);
            editor.remove(KEY_EMAIL);
        }

        editor.apply();
    }

    /**
     * Save only the bus ID (used when loading from Firebase)
     */
    public void setSelectedBusId(String busId) {
        SharedPreferences.Editor editor = preferences.edit();
        if (busId != null) {
            editor.putString(KEY_SELECTED_BUS_ID, busId);
        } else {
            editor.remove(KEY_SELECTED_BUS_ID);
        }
        editor.apply();
    }

    /**
     * Get the selected bus driver (returns all saved details)
     */
    public BusDriver getSelectedBus() {
        String busId = preferences.getString(KEY_SELECTED_BUS_ID, null);

        if (busId == null) {
            return null;
        }

        BusDriver busDriver = new BusDriver();
        busDriver.setId(busId);
        busDriver.setBusNumber(preferences.getString(KEY_BUS_NUMBER, ""));
        busDriver.setDriverName(preferences.getString(KEY_DRIVER_NAME, ""));
        busDriver.setFromLocation(preferences.getString(KEY_FROM_LOCATION, ""));
        busDriver.setToLocation(preferences.getString(KEY_TO_LOCATION, ""));
        busDriver.setPhoneNumber(preferences.getString(KEY_PHONE_NUMBER, ""));
        busDriver.setEmail(preferences.getString(KEY_EMAIL, ""));

        return busDriver;
    }

    /**
     * Get only the selected bus ID
     */
    public String getSelectedBusId() {
        return preferences.getString(KEY_SELECTED_BUS_ID, null);
    }

    /**
     * Check if a bus is selected
     */
    public boolean hasSelectedBus() {
        return preferences.getString(KEY_SELECTED_BUS_ID, null) != null;
    }

    /**
     * Clear all saved bus data
     */
    public void clearSelectedBus() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_SELECTED_BUS_ID);
        editor.remove(KEY_BUS_NUMBER);
        editor.remove(KEY_DRIVER_NAME);
        editor.remove(KEY_FROM_LOCATION);
        editor.remove(KEY_TO_LOCATION);
        editor.remove(KEY_PHONE_NUMBER);
        editor.remove(KEY_EMAIL);
        editor.apply();
    }
}