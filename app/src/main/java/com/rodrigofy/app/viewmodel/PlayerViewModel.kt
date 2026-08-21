package com.rodrigofy.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigofy.app.data.DemoData
import com.rodrigofy.app.data.PlaybackState
import com.rodrigofy.app.data.RepeatMode
import com.rodrigofy.app.data.SpotifyTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Drives the floating player bar + full-screen player. Ticks a simulated
 * playback position once per second. Wiring this up to the real Spotify
 * App Remote / Web Playback SDK is left as an exercise — see the
 * "Known Limitations" section in README.md.
 */
class PlayerViewModel : ViewModel() {

    private val _playbackState = MutableStateFlow(
        PlaybackState(track = DemoData.featuredTrack, isPlaying = false)
    )
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var tickerJob: Job? = null

    fun play(track: SpotifyTrack) {
        _playbackState.value = _playbackState.value.copy(
            track = track,
            isPlaying = true,
            positionMs = 0L
        )
        startTicker()
    }

    fun togglePlayPause() {
        val isPlaying = !_playbackState.value.isPlaying
        _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
        if (isPlaying) startTicker() else stopTicker()
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(shuffle = !_playbackState.value.shuffle)
    }

    fun cycleRepeatMode() {
        val next = when (_playbackState.value.repeat) {
            RepeatMode.OFF -> RepeatMode.CONTEXT
            RepeatMode.CONTEXT -> RepeatMode.TRACK
            RepeatMode.TRACK -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeat = next)
    }

    fun seekTo(positionMs: Long) {
        _playbackState.value = _playbackState.value.copy(positionMs = positionMs)
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val state = _playbackState.value
                val track = state.track ?: continue
                if (!state.isPlaying) continue
                val nextPosition = state.positionMs + 1000
                _playbackState.value = if (nextPosition >= track.durationMs) {
                    state.copy(positionMs = 0L, isPlaying = state.repeat != RepeatMode.OFF)
                } else {
                    state.copy(positionMs = nextPosition)
                }
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTicker()
    }
}
