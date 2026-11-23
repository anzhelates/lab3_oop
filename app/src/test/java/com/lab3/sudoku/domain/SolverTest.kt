package com.lab3.sudoku.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolverTest {

    @Test
    fun solvesEmptyBoard() {
        val b = Board(3)
        assertTrue(Solver.solve(b, randomize = true))
        assertTrue(Validator.isSolved(b))
    }

    @Test
    fun unsolvableBoardReturnsFalse() {
        val b = Board(2)
        b[0, 0] = Cell(1)
        b[0, 1] = Cell(2)
        b[0, 2] = Cell(3)
        b[1, 3] = Cell(4)
        assertFalse(Solver.solve(b))
    }

    @Test
    fun countsSolutions() {
        assertTrue(Solver.countSolutions(Board(2)) >= 2)

        val full = Board(2).also { Solver.solve(it) }
        assertEquals(1, Solver.countSolutions(full))
    }
}