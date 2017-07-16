package com.nshiba.entity

data class SensorData(val unixtime: Long,
                      val timestamp: String,
                      val gps: GpsData,
                      val attitude: AttitudeData,
                      val links: MutableList<LinkData> = mutableListOf()) {

    private constructor(builder: Builder) : this(
            unixtime = builder.unixtime,
            timestamp = builder.timestamp,
            gps = GpsData(
                    longitude = builder.longitude,
                    latitude = builder.latitude,
                    altitude = builder.altitude),
            attitude = AttitudeData(
                    theta = builder.theta,
                    pitchX = builder.pitchX,
                    rollY = builder.rollY,
                    orientation = builder.orientation)
    )

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {

        var unixtime: Long = 0L

        var timestamp: String = ""

        var longitude: Double = 0.0

        var latitude: Double = 0.0

        var altitude: Double = 0.0

        var theta: Double = 0.0

        var pitchX: Double = 0.0

        var rollY: Double = 0.0

        var orientation: Double = 0.0

        fun build() = SensorData(this)
    }
}
