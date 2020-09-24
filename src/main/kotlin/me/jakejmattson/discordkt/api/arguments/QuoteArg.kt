package me.jakejmattson.discordkt.api.arguments

import me.jakejmattson.discordkt.api.dsl.*

/**
 * Accepts a group of arguments surrounded by quotation marks.
 */
open class QuoteArg(override val name: String = "Quote") : ArgumentType<String>() {
    /**
     * Accepts a group of arguments surrounded by quotation marks.
     */
    companion object : QuoteArg()

    override suspend fun convert(arg: String, args: List<String>, event: GlobalCommandEvent<*>): ArgumentResult<String> {
        val quotationMark = '"'

        if (!arg.startsWith(quotationMark))
            return Error("No opening quotation mark")

        val rawQuote = if (!arg.endsWith(quotationMark))
            args.takeUntil { !it.endsWith(quotationMark) }.joinToString(" ")
        else
            arg

        if (!rawQuote.endsWith(quotationMark))
            return Error("No closing quotation mark")

        val quote = rawQuote.trim(quotationMark)
        val consumedCount = quote.split(" ").size

        return Success(quote, consumedCount)
    }

    override fun generateExamples(event: GlobalCommandEvent<*>) = listOf("\"A Quote\"")
    override fun formatData(data: String) = "\"$data\""
}

private fun List<String>.takeUntil(predicate: (String) -> Boolean): List<String> {
    val result = this.takeWhile(predicate).toMutableList()
    val index = result.size

    if (index in indices)
        result.add(this[index])

    return result
}