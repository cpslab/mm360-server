package com.nshiba.model

import com.nshiba.entity.CapturePointData
import com.nshiba.entity.GpsData
import com.nshiba.entity.LinkData
import com.nshiba.entity.SensorData
import com.nshiba.radian

class SensorCalculateModel(private val sensorData: List<String>){

    private lateinit var data: List<CapturePointData>

    fun createData(): List<CapturePointData> {
        val sensorArray = validateSensorArray(sensorData)
        data = sensorArray.mapIndexed { i, list ->
            CapturePointData("video$i", "theta$i", list)
        }

        data.forEach { target ->
            data.filter { it != target }.forEach { calcGps(target, it) }
        }

        return data
    }

    private fun calcGps(point1: CapturePointData, point2: CapturePointData) {
        val maxIndex = Math.min(point1.sensorData.size, point2.sensorData.size) - 1
        for (i in 0..maxIndex) {
            point1.sensorData[i].links.add(
                    calcGpeDirection(point2.id, point1.sensorData[i].gps, point2.sensorData[i].gps))
        }
    }

    private fun validateSensorArray(sensorData: List<String>): List<List<SensorData>> {

        val sensorStringArrays = sensorData
                .filter { !it.isEmpty() }
                .map {
                    it.trim().split("\n").map { x -> x.split(",") }.drop(1)
                }

        val sensorArrays = sensorStringArrays.map {
            it.map {
                SensorData.build {
                    unixtime = it[0].toLong()
                    timestamp = it[1]
                    longitude = it[3].toDouble()
                    latitude = it[2].toDouble()
                    altitude = it[4].toDouble()
                    theta = it[7].toDouble()
                    pitchX = it[8].toDouble()
                    rollY = it[9].toDouble()
                    orientation = it[10].toDouble()
                }
            }
        }

        return sensorArrays
    }

    fun calcGpeDirection(targetId: String, point1: GpsData, point2: GpsData): LinkData {
        val Y = cos(point2.longitude.radian()) * sin(point2.latitude.radian() - point1.latitude.radian())
        val X = cos(point1.longitude.radian()) * sin(point2.longitude.radian()) - sin(point1.longitude.radian()) * cos(point2.longitude.radian()) * cos(point2.latitude.radian() - point1.latitude.radian())

        var dirE0 = atan2(Y, X) * 180 / Math.PI
        if (dirE0 < 0) {
            dirE0 += 360
        }
        val dirN0 = dirE0 % 360

        val left = sin(point1.latitude.radian()) * sin(point2.latitude.radian())
        val right = cos(point1.latitude.radian()) * cos(point2.latitude.radian()) * cos(point2.longitude.radian() - point1.longitude.radian())
        val distance = 6378.14 * Math.acos(left + right)

        return LinkData(id = targetId, theta = dirN0, distance = distance, phi = 0.0)
    }

    private fun cos(num: Double): Double = Math.cos(num)

    private fun sin(num: Double): Double = Math.sin(num)

    private fun atan2(a: Double, b: Double): Double = Math.atan2(a, b)
}
