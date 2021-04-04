package com.wutsi.telegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.`annotation`.EnableCaching
import kotlin.String

@SpringBootApplication
@EnableCaching
public class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
