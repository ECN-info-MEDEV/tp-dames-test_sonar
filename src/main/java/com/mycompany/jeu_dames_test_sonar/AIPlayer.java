package com.mycompany.jeu_dames_test_sonar;


import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
/**
 * Responsible for the checkers artifical intelligence.
 * 
 * @author Mckenna Cisler 
 * @version 11.24.2015
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class AIPlayer extends Player
{
    // global variables
    boolean isWhite;
    
    /**
     * Constructor for objects of class AIPlayer.
     * Initializes this AI's color.
     * @param color This "player's" color.
     */
    public AIPlayer(boolean isWhite)
	{
		this.isWhite = isWhite;
    }

    /**
     * Gets a move, generated by the AI.
     * @param board The board to apply the move to
     * @return Returns the board, modified according to the computer's move
     */
    public Board getMove(Board board)
    {
        // create list of possible pieces and their moves
        HashMap<Piece, Move[]> possibleChoices = new HashMap<Piece, Move[]>();
        
        // loop over all peices on the board until we get one that has some moves to work with
        // (we should get the best ones last by starting at the top - and this will come out best if we have repeats later in this function)
        for (int x = 0; x < board.size; x++)
        {
            for (int y = 0; y < board.size; y++)
            {
                // if there's a piece here the same color as ours...
                Piece piece = board.getValueAt(x, y);
                if (piece != null && piece.isWhite == this.isWhite)
                {
                    // ...find its moves
                    Move[] possibleMoves = piece.getAllPossibleMoves(board);
                    
                    // and add them with the piece to our list if there is at least one
                    if (possibleMoves != null)
                        possibleChoices.put(piece, possibleMoves);
                }
            }
        }
               
        // record furthest back and furthest forward peice to alternate between (just assign the first for now)
        Piece furthestBackwardPiece = possibleChoices.keySet().toArray(new Piece[1])[0]; // convert to array to make it work
        Piece furthestForwardPiece = possibleChoices.keySet().toArray(new Piece[1])[0];
        
        // iterate over our collected possibilites, looking for the best canidate 
        // (based on second answer here: http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap)
        HashMap<Move, Piece> bestMovesPerPiece = new HashMap<Move, Piece>();
        for (Piece piece : possibleChoices.keySet())
        {                
            // check if this piece is furthest back or forward (out of ones with moves)
            int thisPieceY = piece.getCoordinates()[1];
            if (thisPieceY > furthestForwardPiece.getCoordinates()[1])
            {
                // things are switched if we're the other color (furthest "forward" is furthest back)
                if (isWhite)
                    furthestForwardPiece = piece;
                else
                    furthestBackwardPiece = piece;
            }
            else if (thisPieceY < furthestBackwardPiece.getCoordinates()[1])
            {
                // things are switched if we're the other color (furthest "forward" is furthest back)
                if (isWhite)
                    furthestBackwardPiece = piece;
                else
                    furthestForwardPiece = piece;
            }
            
            // for each list of possible moves, iterate over all of them and record their jump numbers
            Move[] possibleMoves = possibleChoices.get(piece);
            Move maxJumpMove = possibleMoves[0]; // just use first for now
            int maxJumpMoveLength = 0;
            for (int i = 0; i < possibleMoves.length; i++)
            {
                // get the jump length by the number of pieces jumped (ignore if this isn't a jump move)
                Piece[] jumpedPieces = possibleMoves[i].getJumpedPieces(board);
                if (jumpedPieces != null)
                {
                    int jumpLength = jumpedPieces.length;
                    
                    // if it's the best so far, make it so (we are assuming the best are at the end, so drift that way by replacing similar ones as we go along)
                    if (jumpLength >= maxJumpMoveLength)
                    {
                        maxJumpMoveLength = jumpLength;
                        maxJumpMove = possibleMoves[i];
                    }
                }
            }
            
            // add this best move to our array for the pieces (the piece is a property of the move becasue we focus on the moves)
            bestMovesPerPiece.put(maxJumpMove, piece);
        }
        
        // iterate over our best possible pieces and moves, and find the best
        Move absoluteBestMove = bestMovesPerPiece.keySet().toArray(new Move[1])[0]; // use first for now, converting to array of moves
        int absoluteBestMoveJumpLength = 0;
        for (Move move : bestMovesPerPiece.keySet())
        {
            // get the length of this piece's best move's jump (it would be better to calculate this automatically with each new move, but its okay to repeat above)
            Piece[] jumpedPieces = move.getJumpedPieces(board);
            
            // we're only looking at jumping moves here
            if (jumpedPieces != null)
            {
                int thisBestMoveJumpLength = jumpedPieces.length;
            
                // if this one is best now, make it so
                if (thisBestMoveJumpLength >= absoluteBestMoveJumpLength)
                {
                    absoluteBestMoveJumpLength = thisBestMoveJumpLength;
                    absoluteBestMove = move;
                }
            }
        }
        
        // if we have a jump to do, do it...
        if (absoluteBestMoveJumpLength > 0)
        {
            board.applyMoveToBoard(absoluteBestMove, bestMovesPerPiece.get(absoluteBestMove));
        }
        else // ...otherwise, choose at 50-50 random either the furthest forward or furthest back movable piece (to balance agressiveness)
        {
            int randomNum = new Random().nextInt(2);
            if (randomNum == 0)
            {
                board.applyMoveToBoard(getKeyByValue(bestMovesPerPiece, furthestBackwardPiece), furthestBackwardPiece);
            }
            else
            {
                board.applyMoveToBoard(getKeyByValue(bestMovesPerPiece, furthestForwardPiece), furthestForwardPiece);
            }  
        }
        
        return board;
    }
    
    /**
     * Note: copied from http://stackoverflow.com/a/2904266
     * Returns a key in a hashmap that correpsonds to the given value
     * @param map The map to search in
     * @param value The value to search for
     * @return Returns the key found in the map, may be null if not found
     */
    private <T, E> T getKeyByValue(HashMap<T, E> map, E value) 
    {
        for (Entry<T, E> entry : map.entrySet()) 
        {
            if (Objects.equals(value, entry.getValue())) 
            {
                return entry.getKey();
            }
        }
        return null;
    }
}