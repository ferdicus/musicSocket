package com.ioki.midiserver

import io.javalin.Javalin

fun main() {
    val app = Javalin.create()
        .enableStaticFiles("/public")
        .ws("/midi") { webSocket ->
            webSocket.onConnect { session ->
                session.send("You are connected!")
            }
        }
        .start(8080)
}