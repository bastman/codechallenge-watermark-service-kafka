package com.bastman.codechallenge.watermarkservice.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

enum class BookTopic {
    Business,
    Science,
    Media
    ;
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes(
        JsonSubTypes.Type(value = Watermark.Book::class, name = Watermark.TYPE_BOOK),
        JsonSubTypes.Type(value = Watermark.Journal::class, name = Watermark.TYPE_JOURNAL)
)
sealed class Watermark {
    companion object {
        const val TYPE_JOURNAL = "Watermark.Journal"
        const val TYPE_BOOK = "Watermark.Book"
    }

    data class Journal(
            val title: String, val author: String, val content: String
    ) : Watermark()

    data class Book(
            val title: String, val author: String, val content: String, val topic: BookTopic
    ) : Watermark()
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes(
        JsonSubTypes.Type(value = SourcePublication.Book::class, name = SourcePublication.TYPE_BOOK),
        JsonSubTypes.Type(value = SourcePublication.Journal::class, name = SourcePublication.TYPE_JOURNAL)
)
sealed class SourcePublication {
    companion object {
        const val TYPE_JOURNAL = "SourcePublication.Journal"
        const val TYPE_BOOK = "SourcePublication.Book"
    }

    data class Journal(
            val title: String, val author: String, val content: String
    ) : SourcePublication()

    data class Book(
            val title: String, val author: String, val content: String, val topic: BookTopic
    ) : SourcePublication()
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes(
        JsonSubTypes.Type(value = WatermarkedPublication.Book::class, name = WatermarkedPublication.TYPE_BOOK),
        JsonSubTypes.Type(value = WatermarkedPublication.Journal::class, name = WatermarkedPublication.TYPE_JOURNAL)
)
sealed class WatermarkedPublication {
    companion object {
        const val TYPE_JOURNAL = "WatermarkedPublication.Journal"
        const val TYPE_BOOK = "WatermarkedPublication.Book"
    }

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

