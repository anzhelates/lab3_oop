package com.lab3.sudoku.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BoardTest {

    @Test
    fun sizeIsBoxSizeSquared() {
        assertEquals(4, Board(2).size)
        assertEquals(9, Board(3).size)
    }

    @Test
    fun copyIsIndependentOfOriginal() {
        val original = Board(2)
        original[0, 0] = Cell(1)

        val copy = original.copy()
        copy[0, 0] = Cell(3)

        assertEquals(1, original[0, 0].value)
        assertEquals(3, copy[0, 0].value)
    }
}