package com.ioki.midiserver

import com.google.gson.GsonBuilder
import com.ioki.midiserver.model.BandMember
import com.ioki.midiserver.model.PlayRequest
import com.ioki.midiserver.model.getAvailableChannel
import com.ioki.midiserver.model.releaseChannel
import io.javalin.Javalin
import io.javalin.websocket.WsSession
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import javax.sound.midi.MidiSystem
import javax.sound.midi.Synthesizer

val LOGGER = LoggerFactory.getLogger("com.ioki.MidiServerApplication")
val SYNTH = MidiSystem.getSynthesizer().also(Synthesizer::open)
val INSTRUMENTS = SYNTH.availableInstruments
val CHANNELS = SYNTH.channels

val BAND_MEMBERS = ConcurrentHashMap<String, BandMember>()
val DISCO_MEMBERS = ConcurrentHashMap.newKeySet<WsSession>()
val GSON = GsonBuilder().create()

val DISCO_SUBJECT = PublishSubject.create<String>()

val DISCO_DISPOSABLE = DISCO_SUBJECT
    .observeOn(Schedulers.io())
    .flatMap { message ->
        Observable.fromIterable(DISCO_MEMBERS)
            .map { user -> user to message }
    }
    .subscribe { (user, message) ->
        user.send(message)
    }

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
                    getAvailableChannel(),
                    getColorName(),
                    session
                )
                BAND_MEMBERS[session.id] = bandMember
                SYNTH.loadInstrument(INSTRUMENTS[bandMember.instrument])
                session.send(GSON.toJson(bandMember))
                LOGGER.info(
                    "User ${bandMember.name} connected. " +
                            "instrument=${INSTRUMENTS[bandMember.instrument].name} " +
                            "channel=${bandMember.channel}"
                )
            }

            webSocket.onClose { session, statusCode, reason ->
                BAND_MEMBERS[session.id]?.let { bandMember ->
                    SYNTH.unloadInstrument(INSTRUMENTS[bandMember.instrument])
                    BAND_MEMBERS.remove(session.id)
                    releaseChannel(bandMember.channel)
                    LOGGER.info("${bandMember.name} disconnected")
                }
            }

            webSocket.onMessage { session, msg ->
                handleMessage(session, msg)
            }

        }
        .ws("/disco") { webSocket ->
            webSocket.onConnect { session ->
                DISCO_MEMBERS.add(session)
            }

            webSocket.onClose { session, statusCode, reason ->
                DISCO_MEMBERS.remove(session)
            }
        }
        .start(8080)
}

fun handleMessage(session: WsSession, msg: String) {
    val playRequest = GSON.fromJson(msg, PlayRequest::class.java)
    BAND_MEMBERS[session.id]?.let {
        it.playing = playRequest.shouldPlay

        if (playRequest.shouldPlay) {
            playSound(it, playRequest.note)
            DISCO_SUBJECT.onNext(GSON.toJson(BAND_MEMBERS.values))
        } else {
            stopSound(it, playRequest.note)
            DISCO_SUBJECT.onNext(GSON.toJson(BAND_MEMBERS.values))
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