package io.github.yusaka39.reversi.alphabeta

import io.github.yusaka39.reversi.game.Board
import io.github.yusaka39.reversi.game.Grid
import io.github.yusaka39.reversi.game.constants.Sides
import io.github.yusaka39.reversi.game.interfaces.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

/**
 * Created by yusaka on 12/10/16.
 */

class AlphaBetaPlayer(override val side: Sides, override val name: String,
                      val evaluate: (Board) -> Int, val depth: Int) : Player {

    override fun handleTurn(board: Board, validMoves: List<Grid>): Grid {
        val latch = CountDownLatch(validMoves.count())
        val treeToScore = ConcurrentHashMap<Node, Int>()
        validMoves.forEach {
            thread {
                val tree = Node(this.side, it, board.clone().apply {
                    this.put(this@AlphaBetaPlayer.side, it)
                })
                treeToScore[tree] = evaluateScore(tree)
                latch.countDown()
            }
        }
        latch.await()
        return treeToScore.maxBy { it.value }!!.key.move
    }

    fun evaluateScore(node: Node): Int {

        fun alphaBeta(node: Node, depth: Int, a: Int, b: Int): Int {
            val children = node.getChildren()
            if (depth == 0 || children.isEmpty()) {
                return this.evaluate(node.board)
            }

            if (node.side == this.side) {
                var alpha = a
                for (child in children) {
                    alpha = alphaBeta(child, depth - 1, alpha, b).let {
                        if (alpha > it) alpha else it
                    }
                    if (alpha > b) {
                        return b
                    }
                }
                return alpha
            } else {
                var beta = b
                for (child in children) {
                    beta = alphaBeta(child, depth -1, a, beta).let {
                        if (beta > it) it else beta
                    }
                    if (a > beta) {
                        return  a
                    }
                }
                return beta
            }
        }
        return alphaBeta(node, this.depth, Int.MIN_VALUE, Int.MAX_VALUE)
    }
}

class Node(val side: Sides, val move: Grid, val board: Board) {
    fun getChildren(): List<Node> {
        fun genChildren(side: Sides, board: Board): List<Node> {
            return board.getValidMoves(side).map {
                Node(side, it, board.clone().apply {
                    this.put(side, it)
                })
            }
        }

        val opposite = side.reverse()

        return when {
            this.board.canPut(opposite) -> genChildren(opposite, this.board)
            this.board.canPut(side) -> genChildren(this.side, this.board)
            else -> emptyList()
        }
    }
}
