package com.bastman.codechallenge.watermarkservice.domain

enum class BookTopic {
    Business,
    Science,
    Media
    ;
}

sealed class Watermark {

    data class Journal(
            val title: String, val author: String, val content: String
    ) : Watermark()

    data class Book(
            val title: String, val author: String, val content: String, val topic: BookTopic
    ) : Watermark()
}


sealed class SourcePublication {

    data class Journal(
            val title: String, val author: String, val content: String
    ) : SourcePublication()

    data class Book(
            val title: String, val author: String, val content: String, val topic: BookTopic
    ) : SourcePublication()
}

sealed class WatermarkedPublication {

    data class Journal(
            val title: String,
            val author: String,
            val content: String,
            val watermark: Watermark.Journal
    ) : WatermarkedPublication()

    data class Book(
            val title: String,
            val author: String,
            val content: String,
            val topic: BookTopic,
            val watermark: Watermark.Book
    ) : WatermarkedPublication()
}

fun watermarkPublication(sourcePublication: SourcePublication): WatermarkedPublication = when (sourcePublication) {
    is SourcePublication.Book -> {
        WatermarkedPublication.Book(
                title = sourcePublication.title,
                author = sourcePublication.author,
                content = sourcePublication.content,
                topic = sourcePublication.topic,
                watermark = Watermark.Book(
                        title = sourcePublication.title,
                        author = sourcePublication.author,
                        content = sourcePublication.content,
                        topic = sourcePublication.topic
                )
        )
    }
    is SourcePublication.Journal -> {
        WatermarkedPublication.Journal(
                title = sourcePublication.title,
                author = sourcePublication.author,
                content = sourcePublication.content,
                watermark = Watermark.Journal(
                        title = sourcePublication.title,
                        author = sourcePublication.author,
                        content = sourcePublication.content
                )
        )
    }
}

