package com.bastman.kjson.jackson.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


class Json(private val builder: JsonBuilder) {

    private val objectMapper = builder.getObjectMapperCopy()
    fun builder() = builder

    fun <T> decode(json: String, valueType: Class<T>): T = objectMapper.readValue(json, valueType)
    fun <T> decode(json: String, valueTypeRef: TypeReference<T>): T = objectMapper.readValue(json, valueTypeRef)
    inline fun <reified T> decode(content: String): T {
        return decode(content, object : TypeReference<T>() {})
    }

    fun decodeTree(content: String): JsonNode = objectMapper.readTree(content)
    fun encode(value: Any?) = objectMapper.writeValueAsString(value)


    class JsonBuilder(objectMapper: ObjectMapper = jacksonObjectMapper()) {

        private val objectMapper = objectMapper.copy()

        fun build() = Json(builder = this)

        companion object {
            fun default() = JsonBuilder()
        }

        fun getObjectMapperCopy() = objectMapper.copy()

        private fun modifyMapper(block: (ObjectMapper) -> ObjectMapper): JsonBuilder = JsonBuilder(
                block.invoke(objectMapper.copy())
        )

        fun withModules(vararg modules: Module) = modifyMapper {
            it.registerModules(*modules)
        }

        fun withSerializationFeatureConfig(feature: SerializationFeature, state: Boolean) = modifyMapper {
            it.configure(feature, state)
        }

        fun withDeserializationFeatureConfig(feature: DeserializationFeature, state: Boolean) = modifyMapper {
            it.configure(feature, state)
        }

        fun withMapperFeatureFeatureConfig(feature: MapperFeature, state: Boolean) = modifyMapper {
            it.configure(feature, state)
        }

        fun withJsonParserFeatureFeatureConfig(feature: JsonParser.Feature, state: Boolean) = modifyMapper {
            it.configure(feature, state)
        }

        fun withJsonGeneratorFeatureFeatureConfig(feature: JsonGenerator.Feature, state: Boolean) = modifyMapper {
            it.configure(feature, state)
        }

        fun withMapperFeature(vararg feature: MapperFeature) = modifyMapper {
            it.enable(*feature)
        }

        fun withoutMapperFeature(vararg feature: MapperFeature) = modifyMapper {
            it.disable(*feature)
        }

        fun withJsonParserFeature(vararg feature: JsonParser.Feature) = modifyMapper {
            it.enable(*feature)
        }

        fun withoutJsonParserFeature(vararg feature: JsonParser.Feature) = modifyMapper {
            it.disable(*feature)
        }

        fun withJsonGeneratorFeature(vararg feature: JsonGenerator.Feature) = modifyMapper {
            it.enable(*feature)
        }

        fun withoutJsonGeneratorFeature(vararg feature: JsonGenerator.Feature) = modifyMapper {
            it.disable(*feature)
        }


        fun withSerializationFeature(vararg features: SerializationFeature) = modifyMapper {
            it.enable(features.first(), *features.drop(1).toTypedArray())
        }

        fun withoutSerializationFeature(vararg features: SerializationFeature) = modifyMapper {
            it.disable(features.first(), *features.drop(1).toTypedArray())
        }

        fun withDeserializationFeature(vararg features: DeserializationFeature) = modifyMapper {
            it.enable(features.first(), *features.drop(1).toTypedArray())
        }

        fun withoutDeserializationFeature(vararg features: DeserializationFeature) = modifyMapper {
            it.disable(features.first(), *features.drop(1).toTypedArray())
        }

    }


}

