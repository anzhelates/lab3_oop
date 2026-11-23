package com.lab3.sudoku.domain

class Board(val boxSize: Int) {

    init {
        require(boxSize in 2..3) { "boxSize must be 2 or 3" }
    }

    val size = boxSize * boxSize
    private val cells = Array(size) { Array(size) { Cell() } }

    operator fun get(row: Int, col: Int): Cell = cells[row][col]

    operator fun set(row: Int, col: Int, cell: Cell) {
        cells[row][col] = cell
    }

    fun copy(): Board {
        val result = Board(boxSize)
        for (r in 0 until size) for (c in 0 until size) result[r, c] = this[r, c]
        return result
    }

    fun isFull(): Boolean = cells.all { row -> row.all { it.value != 0 } }
}