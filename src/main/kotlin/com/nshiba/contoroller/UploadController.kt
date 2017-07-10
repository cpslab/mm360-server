package com.nshiba.contoroller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UploadController {

    @RequestMapping("/upload")
    internal fun upload(@RequestParam("name") name: String): String {
        return "hello. $name."
    }
}
