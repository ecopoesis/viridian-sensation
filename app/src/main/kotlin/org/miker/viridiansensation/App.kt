package org.miker.viridiansensation

import arrow.core.Option
import com.github.ajalt.clikt.core.CliktCommand
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.miker.viridiansensation.JacksonConfig.auto

data class Post(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String,
    val likes: Option<Int>
)

val postLens = Body.auto<Post>().toLens()
val postListLens = Body.auto<List<Post>>().toLens()

class App: CliktCommand() {
    override fun run() {
        val client = ApacheClient()
        val request = Request(GET, "https://jsonplaceholder.typicode.com/comments")
            .query("postId", "1")
        val response = client(request)
        val deserialized = postListLens(response)
        println(deserialized)
    }
}

fun main(args: Array<String>) = App().main(args)
