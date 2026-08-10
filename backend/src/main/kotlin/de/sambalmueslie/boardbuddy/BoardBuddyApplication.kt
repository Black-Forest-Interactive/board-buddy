package de.sambalmueslie.boardbuddy

import com.fasterxml.jackson.annotation.JsonInclude
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

@OpenAPIDefinition(
    info = Info(
        title = "Board Buddy Backend",
        version = "1.0.0-SNAPSHOT",
    )
)

class BoardBuddyApplication {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BoardBuddyApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.build()
                .packages("de.sambalmueslie.boardbuddy")
                .mainClass(BoardBuddyApplication::class.java)
                .start()
        }
    }

    @Singleton
    internal class ObjectMapperBuilderBeanEventListener : BeanCreatedEventListener<JsonMapper.Builder> {
        override fun onCreated(event: BeanCreatedEvent<JsonMapper.Builder>): JsonMapper.Builder {
            // Jackson 3's ObjectMapper is immutable once built, so customization now happens on the
            // JsonMapper.Builder bean before Micronaut calls build() on it (java.time support is built
            // into jackson-databind since Jackson 3, so no separate JavaTimeModule is needed anymore).
            return event.bean
                .addModule(
                    KotlinModule.Builder()
                        .withReflectionCacheSize(512)
                        .configure(KotlinFeature.NullToEmptyCollection, true)
                        .configure(KotlinFeature.NullToEmptyMap, true)
                        .configure(KotlinFeature.NullIsSameAsDefault, true)
                        .configure(KotlinFeature.StrictNullChecks, false)
                        .build()
                )
                .changeDefaultPropertyInclusion { value -> value.withValueInclusion(JsonInclude.Include.ALWAYS) }
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        }
    }
}