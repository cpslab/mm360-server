package com.nshiba.api.controller

import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.nshiba.entity.*
import com.nshiba.model.AwsClient
import com.nshiba.toObject
import com.nshiba.toStringFromJackson
import org.springframework.http.MediaType


@RestController
class ApiController {

    private val sensorFileName = "data.json"

    private val awsClient = AwsClient()

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/create"), method = arrayOf(RequestMethod.POST))
    internal fun createProject(@RequestBody name: String): String {
        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>().toMutableList()
        val projectId = projectList.lastOrNull()?.projectId?.inc() ?: 1
        val project = ProjectListItemData(projectId = projectId, projectName = name)
        projectList.add(project)
        return awsClient.updateProjectList(projectList)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/project/{name}/sensor"),
                    method = arrayOf(RequestMethod.POST),
                    consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    internal fun uploadSensor(@PathVariable name: String,
                              @RequestBody data: Array<CapturePointData>): String {
        val json = ObjectMapper().writeValueAsString(data)
        val filename = "$name/$sensorFileName"
        awsClient.putSensorData(name, json)

        val projectList = awsClient.fetchProjectList()
                .toObject<List<ProjectListItemData>>().toMutableList()
        println(projectList)
        projectList.map {
            if (it.projectName == name) {
                it.dataPath = "projects/$filename"
            }
            it
        }
        println(projectList)

        return awsClient.updateProjectList(projectList)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/project/{name}/sensor"),
                    method = arrayOf(RequestMethod.GET))
    internal fun fetchSensor(@PathVariable name: String): String {
        println(name)
        return awsClient.fetchSensorData(name)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/project/{name}/pre-signed-url-list"), method = arrayOf(RequestMethod.GET))
    internal fun fetchPreSignedUploadUrlList(@PathVariable name: String): List<UploadTargetVideoData> {
        val sensorDataList = awsClient.fetchSensorData(name).toObject<List<CapturePointData>>()
        val preSignedUploadUrlList = sensorDataList.mapIndexed { index, (_, _, sensorData) ->
            val url = fetchProjectPolicy(name, "point$index.mp4", "video/mp4")
            UploadTargetVideoData(index, url, sensorData[0].gps)
        }
        return preSignedUploadUrlList
    }

//    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @CrossOrigin(origins = arrayOf("*"))
    @RequestMapping(path = arrayOf("/api/projects"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProject(): String {
        return awsClient.fetchProjectList()
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/project/{name}"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectData(@PathVariable name: String): List<ProjectListItemData> {
        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>()
        return projectList.filter { it.projectName == name }
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/project/{name}"), method = arrayOf(RequestMethod.PATCH))
    internal fun updateProjectData(@PathVariable name: String, @RequestBody body: ProjectListItemData): String {
        println("update project data: patch")
        println(body)

        updateProjectList(body)
        updateSensorData(body, name)

        return "success"
    }

    private fun updateSensorData(data: ProjectListItemData, name: String) {
        val sensorData = awsClient.fetchSensorData(name).toObject<List<CapturePointData>>()
        data.pointVideoPathList.forEach { (id, path) ->
            sensorData.toMutableList()
                    .filter { it.id == "video$id" }
                    .map { it.path = path }
        }
        println("sensor data: ${sensorData.map { "id: ${it.id}, path: ${it.path}" }}")
        awsClient.putSensorData(name, sensorData.toStringFromJackson())
    }

    private fun updateProjectList(data: ProjectListItemData) {
        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>()
        val updatedList = projectList.map {
            if (it.projectId == data.projectId) { data } else { it }
        }
        awsClient.updateProjectList(updatedList)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000", "https://cpslab.github.io/mm360/"))
    @RequestMapping(path = arrayOf("/api/policy"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectPolicy(@RequestParam("project_name") projectName: String,
                                    @RequestParam("filename") filename: String,
                                    @RequestParam("content_type") contentType: String): String {
        println("$projectName/$filename")
        return awsClient.generateSingedUrl(projectName, filename, contentType)
    }
}
