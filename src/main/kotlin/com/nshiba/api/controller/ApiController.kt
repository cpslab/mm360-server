package com.nshiba.api.controller

import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.nshiba.model.AwsClient


@RestController
class ApiController {

    private val filename = "/test/data.json"

    private val awsClient = AwsClient()

    @RequestMapping(path = arrayOf("/api/upload/csv"), method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/upload/csv"), method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestParam(name = "file") sensors: List<MultipartFile>): String {
        val model = SensorCalculateModel(sensors.map { it.inputStream.reader().readText() })
        val uploadData = model.createData()
        println(uploadData.toString())
        val json = ObjectMapper().writeValueAsString(uploadData)

        return awsClient.putObject(filename, json)
    }

    @RequestMapping(path = arrayOf("/api/projects"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProject(): String {
        return awsClient.fetchProjectList()
    }


    @RequestMapping(path = arrayOf("/api/project/{name}"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectData(@PathVariable name: String): String {
        return awsClient.fetchProjectData(name)
    }

    @RequestMapping(path = arrayOf("/api/project/{name}/policy"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectPolicy(@PathVariable name: String): Pair<String, String> {
        return awsClient.generateSignature(name)
    }
}
