/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.jeu_dames_test_sonar;

/**
 *
 * @author cguziolo
 */

public abstract class Player
{
    /**
     * Gets a move, by asking the given player what move they want to do.
     * @param board The board to apply the move to
     * @return Returns the board, modified according to the player's move
     */
    public abstract Board getMove(Board board);
}
