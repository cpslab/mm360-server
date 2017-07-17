package com.nshiba.api.controller

import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import com.fasterxml.jackson.databind.ObjectMapper




@RestController
@RequestMapping("/api/upload/csv")
class UploadController {

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    private val filename = "s3://mm360-video/test/data.json"

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestParam(name = "file") sensors: List<MultipartFile>): String? {
        val model = SensorCalculateModel(sensors.map { String(it.bytes) })
        val uploadData = model.createData()

        val resource = resourceLoader.getResource(filename)
        val writableResource = resource as WritableResource
        try {
            val output = writableResource.outputStream
            val json = ObjectMapper().writeValueAsString(uploadData)
            output.write(json.toByteArray())
            return "upload success! filename is: $filename"
        } catch (e: Exception) {
            e.fillInStackTrace()
            return e.message
        }
    }
}
