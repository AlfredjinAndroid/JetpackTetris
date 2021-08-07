package com.jxd.jetpacktetris.game

sealed class GameState {
    object Pause : GameState()
    object Running : GameState()
    object Over : GameState()
}