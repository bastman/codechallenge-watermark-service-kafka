package com.bastman.codechallenge.watermarkservice.logging


import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AppLogger {
    fun get(clazz: Class<*>): Logger = LoggerFactory.getLogger(clazz)
}
