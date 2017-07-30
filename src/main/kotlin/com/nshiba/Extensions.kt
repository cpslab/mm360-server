package com.nshiba

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.*

fun Any.toStringFromJackson(): String = ObjectMapper().writeValueAsString(this)

inline fun <reified T : Any> String.toObject(): T = jacksonObjectMapper().readValue<T>(this)

fun Double.radian(): Double = this * Math.PI / 180
