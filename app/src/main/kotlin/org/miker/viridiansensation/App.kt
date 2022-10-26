package org.miker.viridiansensation

import arrow.core.None
import arrow.core.Option
import com.github.ajalt.clikt.core.CliktCommand
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.miker.viridiansensation.JacksonConfig.auto

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String,
    val likes: Option<Int>
)

val commentLens = Body.auto<Comment>().toLens()
val commentListLens = Body.auto<List<Comment>>().toLens()

class App: CliktCommand() {
    val client = ApacheClient()

    override fun run() {
        post()
    }

    fun get() {
        val request = Request(GET, "https://jsonplaceholder.typicode.com/comments")
            .query("postId", "1")
        val response = client(request)
        val deserialized = commentListLens(response)
        println(deserialized)
    }

    fun post() {
        val request = commentLens(
            Comment(1, 1, "foo", "bar", "baz", None),
            Request(POST, "https://jsonplaceholder.typicode.com/comments").header("x-custom", "foo")
        )
        val response = client(request)
        println(response)
    }
}

fun main(args: Array<String>) = App().main(args)
