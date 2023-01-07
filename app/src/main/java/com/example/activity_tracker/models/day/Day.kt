package com.example.activity_tracker.models.day

import com.example.activity_tracker.db.RunningEntity
import java.io.Serializable
import java.util.*


data class Day(
    var day: Pair<Date, MutableList<RunningEntity>>
):Serializable