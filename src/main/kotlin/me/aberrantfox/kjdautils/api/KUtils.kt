package me.aberrantfox.kjdautils.api

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.CommandExecutor
import me.aberrantfox.kjdautils.internal.command.CommandRecommender
import me.aberrantfox.kjdautils.internal.di.DIService
import me.aberrantfox.kjdautils.internal.event.EventRegister
import me.aberrantfox.kjdautils.internal.listeners.CommandListener
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder


class KUtils(val config: KJDAConfiguration) {
    private var listener: CommandListener? = null
    private var executor: CommandExecutor? = null
    private var container: CommandsContainer? = null
    private val diService = DIService()

    val jda = JDABuilder(AccountType.BOT).setToken(config.token).buildBlocking()
    var logger = DefaultLogger()

    init {
        jda.addEventListener(EventRegister)
    }

    fun registerInjectionObject(vararg obj: Any) = obj.forEach { diService.addElement(it) }

    fun registerCommands(commandPath: String, prefix: String): CommandsContainer {
        config.commandPath = commandPath
        config.prefix = prefix

        val container = produceContainer(commandPath, diService)
        CommandRecommender.addAll(container.listCommands())

        val executor = CommandExecutor(config, container, jda)
        val listener = CommandListener(config, container, jda, logger, executor)

        this.container = container
        this.executor = executor
        this.listener = listener

        registerListeners(listener)

        return container
    }

    fun registerCommandPrecondition(condition: (CommandEvent) -> Boolean) = listener?.addPrecondition(condition)

    fun registerListeners(vararg listeners: Any) =
            listeners.forEach {
                EventRegister.eventBus.register(it)
            }
}

fun startBot(token: String, operate: KUtils.() -> Unit = {}): KUtils {
    val util = KUtils(KJDAConfiguration(token))
    util.operate()
    return util
}