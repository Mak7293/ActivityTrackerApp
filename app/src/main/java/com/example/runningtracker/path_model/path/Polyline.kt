package com.example.runningtracker.path_model.path

import org.osmdroid.util.GeoPoint

data class Polyline (
    val latLang: MutableList<GeoPoint>
)
