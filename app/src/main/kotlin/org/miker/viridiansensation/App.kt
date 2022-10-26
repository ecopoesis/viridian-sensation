package org.miker.viridiansensation

import arrow.core.firstOrNone
import arrow.core.flattenOption
import arrow.core.lastOrNone
import arrow.core.tail
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.miker.viridiansensation.JacksonConfig.auto

data class DataSet(val events: List<Event>) {
    companion object {
        val lens = Body.auto<DataSet>().toLens()
    }
}

data class Sessions(val sessionsByUser: Map<String, List<Session>>) {
    companion object {
        val lens = Body.auto<Sessions>().toLens()
    }
}

class App: CliktCommand() {
    private val key: String by option("-k", "--key").required()
    private val client: (Request) -> Response = ApacheClient()
    private val events: List<Event> by lazy {
        DataSet.lens(
            client(
                Request(GET, "https://candidate.hubteam.com/candidateTest/v3/problem/dataset")
                    .query("userKey", key)
            )
        ).events
    }

    override fun run() {
        val request = Sessions.lens(
            Sessions(process(events)),
            Request(POST, "https://candidate.hubteam.com/candidateTest/v3/problem/result")
                .query("userKey", key)
        )

        val response = client(request)
        println(response)
    }

    fun process(events: List<Event>): Map<String, List<Session>> = events
        .groupBy { event -> event.visitorId }
        .mapValues { (_, events) ->
            events
                .sortedBy { event -> event.timestamp }
                .superSplit { a, b ->
                    b.dateTime.minusMinutes(10).isAfter(a.dateTime)
                }
                .map { events ->
                    Session(
                        if (events.size > 1) events.last().timestamp - events.first().timestamp else 0,
                        events.map { event -> event.url },
                        events.first().timestamp
                    )
                }
        }
}

fun main(args: Array<String>) = App().main(args)

/**
 * Splits the [List]s into sublists where for adjacent sublists a and b [predicate](a.tail, b.head) is true.
 */
fun <T> List<T>.superSplit(predicate: (T, T) -> Boolean): List<List<T>> {
    /**
     * I hate that I have to use a [MutableList] here by type-erasure is killing me.
     */
    tailrec fun inner(result: MutableList<List<T>>, current: List<T>, remaining: List<T>): List<List<T>> {
        val a = current.lastOrNone()
        val b = remaining.firstOrNone()
        return when {
            b.isEmpty() -> {
                result.add(current)
                result
            }
            a.isEmpty() -> inner(result, listOf(remaining.firstOrNone()).flattenOption().toList(), remaining.tail())
            predicate(a.orNull()!!, b.orNull()!!) -> {
                result.add(current)
                inner(result, emptyList(), remaining)
            }
            else -> inner(result, current + b.orNull()!!, remaining.tail())
        }
    }

    return inner(mutableListOf(), emptyList(), this).filter { it.isNotEmpty() }
}
