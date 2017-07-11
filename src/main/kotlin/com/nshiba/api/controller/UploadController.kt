package com.nshiba.api.controller

import com.nshiba.entity.SensorCsv
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/upload")
class UploadController {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @CrossOrigin
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestBody sensor: Array<SensorCsv>): Array<SensorCsv> {
        return sensor
    }
}
