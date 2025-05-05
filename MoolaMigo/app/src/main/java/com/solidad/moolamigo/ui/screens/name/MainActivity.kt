package com.solidad.moolamigo.ui.screens.name

import com.google.accompanist.permissions.isGranted



import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "YOUR_API_KEY")

        setContent {
            MaterialTheme {
                RestaurantScreen()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RestaurantScreen() {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var restaurants by remember { mutableStateOf<List<Place>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (permissionState.status.isGranted) {
            getCurrentLocationAndRestaurants(context) { result ->
                restaurants = result
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nearby Restaurants", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(restaurants) { place ->
                Text(text = place.name ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun rememberPermissionState(function: RememberObserver) {}

annotation class ExperimentalPermissionsApi

@SuppressLint("MissingPermission")
fun getCurrentLocationAndRestaurants(context: android.content.Context, onResult: (List<Place>) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val placesClient: PlacesClient = Places.createClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val placeFields = listOf(Place.Field.NAME, Place.Field.TYPES, Place.Field.LAT_LNG)
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            placesClient.findCurrentPlace(request)
                .addOnSuccessListener { response ->
                    val restaurants = response.placeLikelihoods
                        .mapNotNull { it.place }
                        .filter { it.types?.contains(Place.Type.RESTAURANT) == true }
                    onResult(restaurants)
                }
        }
    }
}