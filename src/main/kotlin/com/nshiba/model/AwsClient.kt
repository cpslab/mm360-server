package com.nshiba.model

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.nshiba.entity.ProjectListItemData
import com.nshiba.toStringFromJackson
import java.io.*
import java.util.*


class AwsClient {

    private val ENDPOINT_URL = "https://s3-ap-northeast-1.amazonaws.com"

    private val REGION = "ap-northeast-1"

    private val bucketName = "mm360-video"

    private val rootDir = "projects"

    private val s3Client by lazy { createClient() }

    private val projectListPath = "project-list.json"

    private val sensorFileName = "data.json"

    private val sensorRawPath = "sensor-raw"

    private fun createClient(): AmazonS3 = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(ENDPOINT_URL, REGION))
            .withCredentials(EnvironmentVariableCredentialsProvider())
            .build()

    fun putSensorData(projectName: String, json: String) = putObject("$projectName/$sensorFileName", json)

    fun putObject(filename: String, data: String): String = putObject(filename, data.byteInputStream())

    fun putObject(filename: String, inputStream: InputStream): String =
            try {
                s3Client.putObject(bucketName, "$rootDir/$filename", inputStream, null)
                "success"
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    fun updateProjectList(projectList: List<ProjectListItemData>): String =
            try {
                s3Client.putObject(bucketName, projectListPath,
                        projectList.toStringFromJackson().byteInputStream(), null)
                "success"
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    fun fetchProjectList(): String =
            try {
                val s3Object = s3Client.getObject(bucketName, projectListPath)
                s3Object.objectContent.reader().readText()
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    fun fetchSensorData(projectName: String): String =
            try {
                val path = "$rootDir/$projectName/data.json"
                val s3Object = s3Client.getObject(bucketName, path)
                println(path)
                s3Object.objectContent.reader().readText()
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    fun fetchSensorRawData(projectName: String, filename: String) =
            try {
                val path = "$rootDir/$projectName/$sensorRawPath/$filename"
                val s3Object = s3Client.getObject(bucketName, path)
                println(path)
                s3Object.objectContent.reader().readText()
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    fun generateSingedUrl(projectName: String, filename: String, contentType: String): String =
            try {
                val expiration = Date()
                var msec = expiration.time
                msec += (1000 * 60 * 60).toLong() // Add 1 hour.
                expiration.time = msec

                val generatePresignedUrlRequest
                        = GeneratePresignedUrlRequest(bucketName, "projects/$projectName/$filename")
                generatePresignedUrlRequest.method = HttpMethod.PUT
                generatePresignedUrlRequest.contentType = contentType
                generatePresignedUrlRequest.expiration = expiration

                s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString()
            } catch (ioe: IOException) {
                "File IO Error: ${ioe.message}"
            } catch (ase: AmazonServiceException) {
                showASEMessage(ase)
            } catch (ace: AmazonClientException) {
                showACEMessage(ace)
            }

    private fun showASEMessage(ase: AmazonServiceException) =
            """Caught an AmazonServiceException,
                    which means your request made it to Amazon S3,
                    but was rejected with an error response for some reason.
                    Error Message: ${ase.message},
                    HTTP Status Code: ${ase.statusCode},
                    AWS Error Code: ${ase.errorCode},
                    Error Type: ${ase.errorType},
                    Request ID: ${ase.requestId}"""

    private fun showACEMessage(ace: AmazonClientException) =
            """Caught an AmazonClientException,
                which means the client encountered
                an internal error while trying to communicate with S3,
                such as not being able to access the network.
                Error Message: ${ace.message}"""

}

