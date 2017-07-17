package com.nshiba.model

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsRequest
import java.io.*


class AwsClient {

    private val ENDPOINT_URL = "https://s3-ap-northeast-1.amazonaws.com"

    private val REGION = "us-west-2"

    private val ACCESS_KEY = "AKIAJLVSECTVFSHSBTLA"

    private val SECRET_KEY = "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"

    private val bucketName = "mm360-video"

    private val rootDir = "projects"

    private val s3Client by lazy { createClient() }

    private fun createClient(): AmazonS3Client {
        val credential = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        val client = AmazonS3Client(credential)
        return client
    }

    fun putObject(filename: String, data: String): String {
        return try {
            val inputStream = ByteArrayInputStream(data.toByteArray())
            s3Client.putObject(bucketName, rootDir + filename, inputStream, null)
            "upload success"
        } catch (ioe: IOException) {
            "File IO Error: ${ioe.message}"
        } catch (ase: AmazonServiceException) {
            showASEMessage(ase)
        } catch (ace: AmazonClientException) {
            showACEMessage(ace)
        }
    }

    fun fetchProjectList(): String {
        return try {
            val request = ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withPrefix("$rootDir/")
                    .withDelimiter("/")
            val listObjects = s3Client.listObjects(request).commonPrefixes

            listObjects.map { it.replace("$rootDir/", "") }.toString()
        } catch (ioe: IOException) {
            "File IO Error: ${ioe.message}"
        } catch (ase: AmazonServiceException) {
            showASEMessage(ase)
        } catch (ace: AmazonClientException) {
            showACEMessage(ace)
        }
    }

    fun fetchProjectData(projectName: String): String {
        return try {
            val s3Object = s3Client.getObject(bucketName, "$rootDir/$projectName/data.json")
            val path = "$rootDir/$projectName/data.json"
            println(path)
            s3Object.objectContent.reader().readText()
        } catch (ioe: IOException) {
            "File IO Error: ${ioe.message}"
        } catch (ase: AmazonServiceException) {
            showASEMessage(ase)
        } catch (ace: AmazonClientException) {
            showACEMessage(ace)
        }
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
