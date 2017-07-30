package com.nshiba.api.controller

import com.nshiba.model.SensorCalculateModel
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.nshiba.entity.ProjectListItemData
import com.nshiba.entity.UploadData
import com.nshiba.model.AwsClient
import com.nshiba.toObject


@RestController
class ApiController {

    private val sensorFileName = "data.json"

    private val awsClient = AwsClient()

    @CrossOrigin(origins = arrayOf("*"))
    @RequestMapping(path = arrayOf("/api/create"), method = arrayOf(RequestMethod.POST))
    internal fun createProject(@RequestBody name: String): String {
        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>().toMutableList()
        val projectId = projectList.lastOrNull()?.projectId?.inc() ?: 1
        val project = ProjectListItemData(projectId = projectId, projectName = name)
        projectList.add(project)
        return awsClient.updateProjectList(projectList)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/{name}/sensor"), method = arrayOf(RequestMethod.POST))
    internal fun uploadSensor(@PathVariable name: String, @RequestBody data: String): String {
        val dataList = data.split(";")
        println("uploadData: ${dataList.size}")

        val model = SensorCalculateModel(dataList)
        val uploadData = model.createData()
        val json = ObjectMapper().writeValueAsString(uploadData)
        val filename = "$name/$sensorFileName"
        awsClient.putObject(filename, json)

        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>().toMutableList()
        println(projectList)
        projectList.map {
            when (it.projectName) {
                name -> {
                    it.dataPath = "projects/$filename"
                    return@map it
                }
                else -> it
            }
        }
        println(projectList)

        return awsClient.updateProjectList(projectList)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/{name}/sensor"), method = arrayOf(RequestMethod.GET))
    internal fun fetchSensor(@PathVariable name: String): String {
        return awsClient.fetchSensorData(name)
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/projects"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProject(): String {
        return awsClient.fetchProjectList()
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/project/{name}"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectData(@PathVariable name: String): List<ProjectListItemData> {
        val projectList = awsClient.fetchProjectList().toObject<List<ProjectListItemData>>()
        return projectList.filter { it.projectName == name }
    }

    @CrossOrigin(origins = arrayOf("http://localhost:3000"))
    @RequestMapping(path = arrayOf("/api/policy"), method = arrayOf(RequestMethod.GET))
    internal fun fetchProjectPolicy(@RequestParam("project_name") projectName: String,
                                    @RequestParam("filename") filename: String,
                                    @RequestParam("content_type") contentType: String): String? {
        println("$projectName/$filename")
        return awsClient.generateSingedUrl(projectName, filename, contentType)
    }
}
