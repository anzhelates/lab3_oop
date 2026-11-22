package com.lab3.sudoku.domain

enum class GameMode(val boxSize: Int) {
    SMALL(2),
    CLASSIC(3),
    LARGE(4)
}

const val MAX_HINTS = 3

interface DifficultyStrategy {
    val removeFraction: Double
}

object EasyStrategy : DifficultyStrategy {
    override val removeFraction = 0.5
}

object MediumStrategy : DifficultyStrategy {
    override val removeFraction = 0.6
}

object HardStrategy : DifficultyStrategy {
    override val removeFraction = 0.7
}

val allDifficulties: List<DifficultyStrategy> = listOf(EasyStrategy, MediumStrategy, HardStrategy)