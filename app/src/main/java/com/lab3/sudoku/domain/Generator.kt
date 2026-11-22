package com.lab3.sudoku.domain

class Puzzle(val board: Board, val solution: Board)

object Generator {

    fun generate(mode: GameMode, difficulty: DifficultyStrategy): Puzzle {
        val solution = Board(mode.boxSize)
        Solver.solve(solution, randomize = true)

        val board = solution.copy()
        val size = board.size
        var toRemove = (size * size * difficulty.removeFraction).toInt()

        for (index in (0 until size * size).shuffled()) {
            if (toRemove == 0) break
            val row = index / size
            val col = index % size
            val backup = board[row, col]
            board[row, col] = Cell()
            if (Solver.countSolutions(board) == 1) toRemove-- else board[row, col] = backup
        }

        for (r in 0 until size) for (c in 0 until size) {
            val v = board[r, c].value
            if (v != 0) board[r, c] = Cell(v, isFixed = true)
        }
        return Puzzle(board, solution)
    }
}