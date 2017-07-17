package com.nshiba.model

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import java.io.FileWriter
import java.io.IOException


class AwsClient() {

    private val ENDPOINT_URL = "https://s3-ap-northeast-1.amazonaws.com"

    private val REGION = "us-west-2"

    private val ACCESS_KEY = "AKIAJLVSECTVFSHSBTLA"

    private val SECRET_KEY = "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"

    private val bucketName = "mm360-video"

    private val s3Client by lazy { createClient() }

    fun putObject(filename: String, key: String, data: String): String {
        return try {
            val file = File(filename)
            val writer = FileWriter(file)
            writer.write(data)
            writer.close()

            s3Client.putObject(bucketName, key, file)

            "upload success"
        } catch (ioe: IOException) {
            "File IO Error: ${ioe.message}"
        } catch (ase: AmazonServiceException) {
            "Caught an AmazonServiceException, " +
                    "which means your request made it to Amazon S3, " +
                    "but was rejected with an error response for some reason.\n" +
                    "Error Message: ${ase.message}, " +
                    "HTTP Status Code: ${ase.statusCode}, " +
                    "AWS Error Code: ${ase.errorCode}, " +
                    "Error Type: ${ase.errorType}, " +
                    "Request ID: ${ase.requestId}"
        } catch (ace: AmazonClientException) {
            "Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.\n" +
                    "Error Message: ${ace.message}"
        }
    }

    private fun createClient(): AmazonS3Client {
        val client = AmazonS3Client(ProfileCredentialsProvider())
        return client
    }
}
