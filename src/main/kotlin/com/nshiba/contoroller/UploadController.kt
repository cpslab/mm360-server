package com.nshiba.contoroller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/upload")
class UploadController {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }
}
