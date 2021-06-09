package com.gregantech.timepass.view.live.lifecycleobserver

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.gregantech.timepass.view.live.lifecycleobserver.listener.PlayerLifeCycleActionListener

internal class LivePlayerLifeCycleObserver(
    private val playerLifeCycleActionListener: PlayerLifeCycleActionListener
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResumed() {
        playerLifeCycleActionListener.startPlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        playerLifeCycleActionListener.pausePlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroyed() {
        playerLifeCycleActionListener.releasePlayer()
    }
}