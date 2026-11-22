package com.lab3.sudoku.domain

data class Cell(
    val value: Int = 0,
    val isFixed: Boolean = false
)