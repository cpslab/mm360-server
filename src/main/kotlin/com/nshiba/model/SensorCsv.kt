package com.nshiba.model

data class SensorCsv(
        val unixtime: Long,
        val timestamp: String,
        val longitude: Double,
        val latitude: Double,
        val altitude: Double,
        val accuracy: Double,
        val pressure: Double,
        val theta: Double,
        val zengokeisya: Double,
        val sayukeisya: Double
)
//前後傾斜, 左右傾斜, orientation1, orientation2, orientation3, lowpass方位角
//149.23602,11.477978,-4.062758,154.01222,-76.90093,3.3370094
