package com.nshiba.entity

import org.springframework.web.multipart.MultipartFile

data class UploadData(val extraField: String, val files: Array<MultipartFile>)
