package com.bastman.codechallenge.watermarkservice.configuration

class AppConfigurationException(
        message: String? = null,
        cause: Throwable? = null
) : RuntimeException(message, cause)