package com.lab3.sudoku.domain

object Validator {
    fun isValid(board: Board, row: Int, col: Int, value: Int): Boolean {
        for (i in 0 until board.size) {
            if (i != col && board[row, i].value == value) return false
            if (i != row && board[i, col].value == value) return false
        }
        val boxRow = row / board.boxSize * board.boxSize
        val boxCol = col / board.boxSize * board.boxSize
        for (r in boxRow until boxRow + board.boxSize) {
            for (c in boxCol until boxCol + board.boxSize) {
                if ((r != row || c != col) && board[r, c].value == value) return false
            }
        }
        return true
    }

    fun isSolved(board: Board): Boolean {
        if (!board.isFull()) return false
        for (r in 0 until board.size) {
            for (c in 0 until board.size) {
                if (!isValid(board, r, c, board[r, c].value)) return false
            }
        }
        return true
    }
}