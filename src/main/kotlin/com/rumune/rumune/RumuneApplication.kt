package com.rumune.rumune

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RumuneApplication

fun main(args: Array<String>) {
    runApplication<RumuneApplication>(*args)
}
