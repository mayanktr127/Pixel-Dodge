package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class RetroAudioPlayer(context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    // Plays a tone or sequence of frequencies in Hz
    private fun playSquareTone(frequencies: List<Float>, durationsMs: List<Int>, volume: Float = 0.5f) {
        if (!soundEnabled) return
        scope.launch {
            try {
                val sampleRate = 22050
                var totalDurationMs = 0
                for (d in durationsMs) totalDurationMs += d
                val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                var currentSample = 0
                for (i in frequencies.indices) {
                    val freq = frequencies[i]
                    val duration = durationsMs[i]
                    val samples = (sampleRate * (duration / 1000.0)).toInt()
                    val period = sampleRate / freq

                    for (j in 0 until samples) {
                        if (currentSample >= totalSamples) break
                        // 8-bit square wave
                        val sampleValue = if ((j % period) < (period / 2)) {
                            (Short.MAX_VALUE * volume).toInt().toShort()
                        } else {
                            (Short.MIN_VALUE * volume).toInt().toShort()
                        }
                        buffer[currentSample++] = sampleValue
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Auto-release after playing
                Thread.sleep(totalDurationMs.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun playBlip() {
        playSquareTone(listOf(440f, 660f), listOf(25, 30), 0.25f)
    }

    fun playCoin() {
        vibrate(30)
        playSquareTone(listOf(987.77f, 1318.51f), listOf(60, 100), 0.35f)
    }

    fun playPowerUp() {
        vibrate(60)
        playSquareTone(listOf(523.25f, 659.25f, 783.99f, 1046.50f), listOf(50, 50, 50, 100), 0.4f)
    }

    fun playDodge() {
        playSquareTone(listOf(350f, 500f), listOf(30, 40), 0.2f)
    }

    fun playHit() {
        vibrate(100)
        playSquareTone(listOf(180f, 120f, 80f), listOf(50, 70, 90), 0.5f)
    }

    fun playGameOver() {
        vibrate(200)
        playSquareTone(listOf(440f, 370f, 311f, 220f), listOf(120, 120, 140, 300), 0.5f)
    }

    private fun vibrate(durationMs: Long) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
