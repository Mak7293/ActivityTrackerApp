package com.example.runningtracker.models.day

import com.example.runningtracker.db.RunningEntity
import java.util.*

data class Day(
    var day: MutableMap<Date,List<RunningEntity>>
)
