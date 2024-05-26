package com.hkrtbse2.cab_hkr.ui.theme

import android.content.Context
import android.location.Location
import android.location.LocationManager

class LocationService(private val context: Context) {

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun getCurrentLocation(): Location? {
        return try {
            val provider = LocationManager.NETWORK_PROVIDER
            locationManager.getLastKnownLocation(provider)
        } catch (e: SecurityException) {
            throw MissingPermissionException("Missing location permission")
        } catch (e: Exception) {
            throw LocationServiceException("Error getting location")
        }
    }

    class LocationServiceException(message: String) : Exception(message)
    class LocationDisabledException(message: String) : Exception(message)
    class MissingPermissionException(message: String) : Exception(message)
    class NoNetworkEnabledException(message: String) : Exception(message)
    // You can add more methods for handling location services as needed.
}
