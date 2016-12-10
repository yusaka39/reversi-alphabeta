package io.github.yusaka39.reversi.alphabeta

import io.github.yusaka39.reversi.game.constants.Sides
import io.github.yusaka39.reversi.game.factory.AbstractPlayerFactory
import io.github.yusaka39.reversi.game.interfaces.Player

/**
 * Created by yusaka on 12/10/16.
 */

class AlphaBetaPlayerFactory : AbstractPlayerFactory() {
    override fun create(side: Sides): Player =
            AlphaBetaPlayer(side, "ALPHA_BETA")

}