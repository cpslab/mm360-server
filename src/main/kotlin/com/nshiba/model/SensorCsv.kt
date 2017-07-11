package com.nshiba.model

data class SensorCsv(
        var unixtime: Long = 0L,
        var timestamp: String = "",
        var longitude: Double = 0.0,
        var latitude: Double = 0.0,
        var altitude: Double = 0.0,
        var accuracy: Double = 0.0,
        var pressure: Double = 0.0,
        var theta: Double = 0.0,
        var zengokeisya: Double = 0.0,
        var sayukeisya: Double = 0.0
)
//{ "unixtime": 1496536003990, "timestamp": "2017-06-04_09:26:43", "longitude": 35.7502211, "latitude": 139.8147858, "altitude": 38.0, "accuracy": 3.0, "pressure": 1007.46686, "theta": 149.23602, "zengokeisya": 11.477978, "sayukeisya": -4.062758 }
