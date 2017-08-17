package com.nshiba.entity

data class UploadTargetVideoData(val pointId: Int,
                                 val preSignedUploadUrl: String,
                                 val pointGps: GpsData)
