@file:Suppress("unused")

package me.jakejmattson.kutils.api

import com.google.common.eventbus.EventBus
import com.google.gson.Gson
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.dsl.configuration.BotConfiguration
import me.jakejmattson.kutils.internal.utils.diService
import net.dv8tion.jda.api.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.reflect.KClass

/**
 * @param repository The repository URL for KUtils.
 * @param kutilsVersion The version of KUtils being used.
 * @param kotlinVersion The version of Kotlin used by KUtils.
 * @param jdaVersion The version of JDA used by KUtils.
 */
data class KUtilsProperties(val repository: String,
                            val kutilsVersion: String,
                            val kotlinVersion: String,
                            val jdaVersion: String)

private val propFile = KUtilsProperties::class.java.getResource("/kutils-properties.json").readText()

/**
 * @property jda An instance of JDA that allows access to the Discord API.
 * @property configuration All of the current configuration details for this bot.
 * @property properties Various meta KUtils properties.
 */
abstract class Discord {
    @Deprecated("To be removed")
    abstract val jda: JDA
    abstract val configuration: BotConfiguration
    val properties = Gson().fromJson(propFile, KUtilsProperties::class.java)!!

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any> getInjectionObjects(a: KClass<A>) = diService[A::class]

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>) =
        Args2(getInjectionObjects(a), getInjectionObjects(b))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>) =
        Args3(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>, d: KClass<D>) =
        Args4(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c), getInjectionObjects(d))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>, d: KClass<D>, e: KClass<E>) =
        Args5(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c), getInjectionObjects(d), getInjectionObjects(e))
}

internal fun buildDiscordClient(jdaBuilder: JDABuilder, botConfiguration: BotConfiguration, eventBus: EventBus) =
    object : Discord() {
        override val configuration = botConfiguration

        override val jda = jdaBuilder
            .build()
            .apply {
                addEventListener(object : EventListener {
                    override fun onEvent(event: GenericEvent) = eventBus.post(event)
                })
            }
            .apply { awaitReady() }
    }