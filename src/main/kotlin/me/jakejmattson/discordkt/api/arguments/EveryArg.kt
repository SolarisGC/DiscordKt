package me.jakejmattson.discordkt.api.arguments

import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.internalLocale

/**
 * Consumes all remaining arguments. Does not accept empty strings.
 */
open class EveryArg(override val name: String = "Text",
                    override val description: String = internalLocale.everyArgDescription) : ArgumentType<String> {
    /**
     * Consumes all remaining arguments. Does not accept empty strings.
     */
    companion object : EveryArg()

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<String> {
        if (args.size in 0..1 && arg.isEmpty())
            return Error("Cannot be empty")

        return Success(args.joinToString(" "), args.size)
    }

    override suspend fun generateExamples(event: CommandEvent<*>) = listOf("This is a sample sentence.")
}