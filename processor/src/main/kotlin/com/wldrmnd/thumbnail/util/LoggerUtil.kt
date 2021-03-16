package com.wldrmnd.thumbnail.api.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun getLogger(forClass: Class<*>): Logger {
    return LoggerFactory.getLogger(forClass)
}