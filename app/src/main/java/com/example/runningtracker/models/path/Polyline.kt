package com.example.runningtracker.models.path

import org.osmdroid.util.GeoPoint

data class Polyline (
    val latLang: MutableList<GeoPoint>
)
