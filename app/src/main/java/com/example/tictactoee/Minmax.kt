package com.example.tictactoee
// algorithm to find out best moves in a tictactoe game

class MinMax {
    data class Move(var row: Int, var col: Int)

    companion object {
        var player = 'x'
        var opponent = 'o'

        private fun isMovesLeft(board: Array<CharArray>): Boolean {
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == '_') {
                        return true
                    }
                }
            }
            return false
        }

        fun evaluate(b: Array<CharArray>): Int {
            for (row in 0..2) {
                if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                    if (b[row][0] == player)
                        return +10
                    else if (b[row][0] == opponent)
                        return -10
                }
            }

            for (col in 0..2) {
                if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                    if (b[0][col] == player)
                        return +10
                    else if (b[0][col] == opponent)
                        return -10
                }
            }

            if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
                if (b[0][0] == player)
                    return +10
                else if (b[0][0] == opponent)
                    return -10
            }

            if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
                if (b[0][2] == player)
                    return +10
                else if (b[0][2] == opponent)
                    return -10
            }

            return 0
        }

        fun miniMax(board: Array<CharArray>, depth: Int, isMax: Boolean): Int {
            val score = evaluate(board)

            if (score == 10)
                return score

            if (score == -10)
                return score

            if (!isMovesLeft(board))
                return 0

            if (isMax) {
                var best = Int.MIN_VALUE

                for (i in 0..2) {
                    for (j in 0..2) {
                        if (board[i][j] == '_') {
                            board[i][j] = player
                            best = Math.max(best, miniMax(board, depth + 1, !isMax))
                            board[i][j] = '_'
                        }
                    }
                }
                return best
            }
            else {
                var best = Int.MAX_VALUE

                for (i in 0..2) {
                    for (j in 0..2) {
                        if (board[i][j] == '_') {
                            board[i][j] = opponent
                            best = Math.min(best, miniMax(board, depth + 1, !isMax))
                            board[i][j] = '_'
                        }
                    }
                }
                return best
            }
        }

        fun findBestMove(board: Array<CharArray>): Move {
            var bestVal = Int.MIN_VALUE
            val bestMove = Move(-1, -1)

            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == '_') {
                        board[i][j] = player
                        val moveVal = miniMax(board, 0, false)
                        board[i][j] = '_'

                        if (moveVal > bestVal) {
                            bestMove.row = i
                            bestMove.col = j
                            bestVal = moveVal
                        }
                    }
                }
            }

            return bestMove
        }

//           @JvmStatic
//           fun main(args: Array<String>) {
//            val board = arrayOf(
//                charArrayOf('x', 'o', 'x'),
//                charArrayOf('o', 'o', 'x'),
//                charArrayOf('_', '_', '_')
//            )
//
//            val bestMove = findBestMove(board)
//
//            println("The Optimal Move is:")
//            println("ROW: ${bestMove.row} COL: ${bestMove.col}")
//        }
    }
}
