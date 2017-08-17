package com.nshiba.entity

data class CapturePointData(val id: String,
                            var path: String,
                            val sensorData: List<SensorData>)
