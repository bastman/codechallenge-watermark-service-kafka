package com.bastman.kjson.jackson.fs


import com.bastman.kjson.jackson.codec.Json
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class JsonIOException(message: String) : RuntimeException(message)

inline fun <reified T> Json.load(path: Path): T {
    return try {
        decode(String(Files.readAllBytes(path)))
    } catch(e: Throwable) {

        throw JsonIOException(
                "Failed to load json from path=$path reason=${e.message}"
        )
    }
}

inline fun <reified T> Json.load(uri: URI, charset: Charset = Charsets.UTF_8): T {
    return try {
        decode(uri.toURL().readText(charset))
    } catch(e: Throwable) {

        throw JsonIOException(
                "Failed to load json from uri=$uri reason=${e.message}"
        )
    }
}


fun Json.loadResourceText(
        resource: String,
        loader: ClassLoader = this.javaClass.classLoader,
        charset: Charset = Charsets.UTF_8
): String {
    return try {
        val url: URL = loader.getResource(resource)
                ?: throw RuntimeException("Resource not found!")

        url.readText(charset)
    } catch (all: Throwable) {

        throw JsonIOException(
                "Failed to load json from resource=$resource using classloader of ${loader} reason: ${all.message}"
        )
    }
}

inline fun <reified T> Json.loadResource(
        resource: String,
        loader: ClassLoader = this.javaClass.classLoader,
        charset: Charset = Charsets.UTF_8
): T {
    return try {
        val text = loadResourceText(resource = resource, loader = loader, charset = charset)
        decode(text)
    } catch(e: Throwable) {

        throw JsonIOException(
                "Failed to load json from resource=$resource reason=${e.message}"
        )
    }
}


