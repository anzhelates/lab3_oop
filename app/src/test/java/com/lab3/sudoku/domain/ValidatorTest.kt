package com.lab3.sudoku.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorTest {

    @Test
    fun detectsConflictInRowColumnAndBox() {
        val b = Board(3)
        b[0, 0] = Cell(5)

        assertFalse(Validator.isValid(b, 0, 8, 5))
        assertFalse(Validator.isValid(b, 8, 0, 5))
        assertFalse(Validator.isValid(b, 1, 1, 5))
    }

    @Test
    fun allowsValueWithoutConflict() {
        val b = Board(3)
        b[0, 0] = Cell(5)

        assertTrue(Validator.isValid(b, 3, 3, 5))
        assertTrue(Validator.isValid(b, 0, 1, 6))
    }

    @Test
    fun isSolvedOnlyForCompleteValidBoard() {
        val solved = Board(2).also { Solver.solve(it) }
        assertTrue(Validator.isSolved(solved))

        val incomplete = solved.copy().also { it[0, 0] = Cell() }
        assertFalse(Validator.isSolved(incomplete))
    }
}