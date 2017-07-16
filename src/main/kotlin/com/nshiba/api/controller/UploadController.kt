package com.nshiba.api.controller

import com.nshiba.entity.CapturePointData
import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/upload/csv")
class UploadController {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestParam(name = "file") sensors: List<MultipartFile>): Array<CapturePointData> {
        val model = SensorCalculateModel(sensors.map { String(it.bytes) })
        return model.createJsonData()
    }
}
