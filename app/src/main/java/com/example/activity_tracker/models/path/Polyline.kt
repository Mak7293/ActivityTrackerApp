package com.example.activity_tracker.models.path

import org.osmdroid.util.GeoPoint

data class Polyline (
    val latLang: MutableList<GeoPoint>
)
