package com.ioki.midiserver

import com.google.gson.GsonBuilder
import com.ioki.midiserver.model.BandMember
import com.ioki.midiserver.model.PlayRequest
import io.javalin.Javalin
import io.javalin.websocket.WsSession
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom
import javax.sound.midi.MidiSystem
import javax.sound.midi.Synthesizer

val LOGGER = LoggerFactory.getLogger("com.ioki.MidiServerApplication")
val SYNTH = MidiSystem.getSynthesizer().also(Synthesizer::open)
val INSTRUMENTS = SYNTH.availableInstruments
val CHANNELS = SYNTH.channels

val CLIENTS = mutableMapOf<String, BandMember>()
val GSON = GsonBuilder().create()

fun main() {
    Javalin.create()
        .enableStaticFiles("/public")
        .ws("/midi") { webSocket ->
            webSocket.onConnect { session ->
                val randomInstrumentId = ThreadLocalRandom.current().nextInt(0, INSTRUMENTS.size)
                val bandMember = BandMember(
                    session.id,
                    generateName(),
                    randomInstrumentId,
                    INSTRUMENTS[randomInstrumentId].name,
                    ThreadLocalRandom.current().nextInt(0, CHANNELS.size),
                    getColorName(),
                    session
                )
                CLIENTS[session.id] = bandMember
                SYNTH.loadInstrument(INSTRUMENTS[bandMember.instrument])
                session.send(GSON.toJson(bandMember))
                LOGGER.info(
                    "User ${bandMember.name} connected. " +
                            "instrument=${INSTRUMENTS[bandMember.instrument].name} " +
                            "channel=${bandMember.channel}"
                )
            }

            webSocket.onMessage { session, msg ->
                handleMessage(session, msg)
            }

            webSocket.onClose { session, statusCode, reason ->
                val bandMember = CLIENTS[session.id]!!
                SYNTH.unloadInstrument(INSTRUMENTS[bandMember.instrument])
                CLIENTS.remove(session.id)
                LOGGER.info("${bandMember.name} disconnected")
            }
        }
        .start(8080)
}

fun handleMessage(session: WsSession, msg: String) {
    val playRequest = GSON.fromJson(msg, PlayRequest::class.java)
    CLIENTS[session.id]?.let {
        if (playRequest.shouldPlay) {
            playSound(it, playRequest.note)
        } else {
            stopSound(it, playRequest.note)
        }
    }
}

fun playSound(bandMember: BandMember, note: Int) {
    val channel = CHANNELS[bandMember.channel]
    val instrument = INSTRUMENTS[bandMember.instrument]
    channel.programChange(instrument.patch.program)
    channel.noteOn(note, 100)
}

fun stopSound(bandMember: BandMember, note: Int) {
    CHANNELS[bandMember.channel].noteOff(note)
}