package com.plcoding.testingcourse.part8.domain

import java.time.LocalDateTime

object TimeProvider {

    fun now(): LocalDateTime = LocalDateTime.now()
}