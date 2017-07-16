package com.nshiba.entity

data class CapturePointData(val id: String,
                            val path: String,
                            val sensorData: List<SensorData>)
