package com.example.ksheerasagara.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ksheera_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_FARMER_NAME   = "farmer_name"
        const val KEY_FARM_NAME     = "farm_name"
        const val KEY_VILLAGE       = "village"
        const val KEY_PHONE         = "phone"
        const val KEY_PIN           = "pin"
        const val KEY_USER_ID       = "user_id"       // ← NEW
        const val KEY_IS_LOGGED_IN  = "is_logged_in"
        const val KEY_IS_REGISTERED = "is_registered"
    }

    fun register(
        farmerName: String,
        farmName: String,
        pin: String,
        village: String = "",
        phone: String   = ""
    ) {
        // Generate a brand new unique ID for this account
        val newUserId = UUID.randomUUID().toString()

        prefs.edit()
            .putString(KEY_USER_ID,       newUserId)
            .putString(KEY_FARMER_NAME,   farmerName)
            .putString(KEY_FARM_NAME,     farmName)
            .putString(KEY_VILLAGE,       village)
            .putString(KEY_PHONE,         phone)
            .putString(KEY_PIN,           pin)
            .putBoolean(KEY_IS_REGISTERED, true)
            .putBoolean(KEY_IS_LOGGED_IN,  true)
            .apply()
    }

    fun login(pin: String): Boolean {
        return if (pin == prefs.getString(KEY_PIN, null)) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
            true
        } else false
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    // Clears ONLY session — Room DB stays untouched
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun updateProfile(
        farmerName: String,
        farmName: String,
        village: String,
        phone: String
    ) {
        prefs.edit()
            .putString(KEY_FARMER_NAME, farmerName)
            .putString(KEY_FARM_NAME,   farmName)
            .putString(KEY_VILLAGE,     village)
            .putString(KEY_PHONE,       phone)
            .apply()
    }

    fun updatePin(newPin: String) {
        prefs.edit().putString(KEY_PIN, newPin).apply()
    }

    fun verifyPin(pin: String) = pin == prefs.getString(KEY_PIN, null)

    fun isLoggedIn()    = prefs.getBoolean(KEY_IS_LOGGED_IN,   false)
    fun isRegistered()  = prefs.getBoolean(KEY_IS_REGISTERED,  false)
    fun getFarmerName() = prefs.getString(KEY_FARMER_NAME, "Farmer")  ?: "Farmer"
    fun getFarmName()   = prefs.getString(KEY_FARM_NAME,   "My Farm") ?: "My Farm"
    fun getVillage()    = prefs.getString(KEY_VILLAGE,     "")        ?: ""
    fun getPhone()      = prefs.getString(KEY_PHONE,       "")        ?: ""

    // Every DB query filters by this ID
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }
}