package com.lab3.sudoku.domain

object Solver {
    fun solve(board: Board, randomize: Boolean = false): Boolean {
        val (row, col) = findEmpty(board) ?: return true
        val values = if (randomize) (1..board.size).shuffled() else (1..board.size).toList()
        for (v in values) {
            if (Validator.isValid(board, row, col, v)) {
                board[row, col] = Cell(v)
                if (solve(board, randomize)) return true
                board[row, col] = Cell()
            }
        }
        return false
    }

    fun countSolutions(board: Board, limit: Int = 2): Int = count(board.copy(), limit)

    private fun count(board: Board, limit: Int): Int {
        val (row, col) = findEmpty(board) ?: return 1
        var total = 0
        for (v in 1..board.size) {
            if (Validator.isValid(board, row, col, v)) {
                board[row, col] = Cell(v)
                total += count(board, limit - total)
                board[row, col] = Cell()
                if (total >= limit) break
            }
        }
        return total
    }

    private fun findEmpty(board: Board): Pair<Int, Int>? {
        for (r in 0 until board.size)
            for (c in 0 until board.size)
                if (board[r, c].value == 0) return r to c
        return null
    }
}