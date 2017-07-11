package com.nshiba.contoroller

import com.nshiba.model.SensorCsv
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/upload")
class UploadController {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    internal fun upload(@RequestParam("name") name: String = "World"): String {
        return "hello. $name."
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    internal fun post(@RequestBody sensor: Array<SensorCsv>): Array<SensorCsv> {
        return sensor
    }
}
