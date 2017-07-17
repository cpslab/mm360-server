package com.nshiba.api.controller

import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.nshiba.model.AwsClient


@RestController
@RequestMapping("/api/upload/csv")
class UploadController {

    private val filename = "/test/data.json"

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestParam(name = "file") sensors: List<MultipartFile>): String? {
        val model = SensorCalculateModel(sensors.map { String(it.bytes) })
        val uploadData = model.createData()
        val json = ObjectMapper().writeValueAsString(uploadData)

        val awsClient = AwsClient()
        return awsClient.putObject(filename, filename, json)
    }
}
