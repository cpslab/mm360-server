package com.nshiba

import com.fasterxml.jackson.databind.ObjectMapper

fun Any.toStringFromJackson(): String? = ObjectMapper().writeValueAsString(this)

fun Double.radian(): Double = this * Math.PI / 180
