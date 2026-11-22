package com.lab3.sudoku.domain

enum class GameMode(val boxSize: Int) {
    SMALL(2),
    CLASSIC(3),
    LARGE(4)
}

interface DifficultyStrategy {
    val removeFraction: Double
    val hints: Int
}

object EasyStrategy : DifficultyStrategy {
    override val removeFraction = 0.5
    override val hints = 5
}

object MediumStrategy : DifficultyStrategy {
    override val removeFraction = 0.6
    override val hints = 3
}

object HardStrategy : DifficultyStrategy {
    override val removeFraction = 0.7
    override val hints = 1
}

val allDifficulties: List<DifficultyStrategy> = listOf(EasyStrategy, MediumStrategy, HardStrategy)