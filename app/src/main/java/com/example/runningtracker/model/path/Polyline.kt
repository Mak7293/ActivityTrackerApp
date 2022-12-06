package com.example.runningtracker.model.path

import org.osmdroid.util.GeoPoint

data class Polyline (
    val latLang: MutableList<GeoPoint>
)
