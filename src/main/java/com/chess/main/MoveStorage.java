package com.chess.main;

import com.chess.board.ChessBoard;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2024
 * George Miller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ----------------------------------------------------------------------------
 *
 * Class: MoveStorage
 *
 * This class is used to store moves and their scores during a game. Each move
 * is represented as an array containing the board state and the move score.
 * It provides functionalities to store moves, retrieve the history, and display
 * the move history as a formatted string.
 *
 * Key functionalities include:
 * - Storing board states and their scores as a list.
 * - Clearing the move history.
 * - Converting the move history to a formatted string for display.
 *
 * Dependencies:
 * - `ChessBoard` for representing the current state of the chessboard.
 */

public class MoveStorage {

    // List to store each board state along with its score
    private List<double[]> moveHistory;

    /**
     * Constructor to initialize the move storage list.
     */
    public MoveStorage() {
        this.moveHistory = new ArrayList<>();
    }

    /**
     * Stores a move by copying the board state and appending the score.
     *
     * @param move The current state of the chessboard.
     * @param score The score of the move as evaluated by Stockfish.
     */
    public void storeMove(ChessBoard move, double score) {
        // Get the board state as a 1D array
        int[] boardArray = move.getBoardArray();

        // Convert the board array to a double array and append the score
        double[] boardWithScore = new double[boardArray.length];
        for (int i = 0; i < boardArray.length - 1; i++) {
            boardWithScore[i] = boardArray[i]; // Copy board elements
        }
        boardWithScore[boardArray.length - 1] = score; // Append score

        // Add the board state with the score to the move history
        moveHistory.add(boardWithScore);
    }

    /**
     * Retrieves the move history.
     *
     * @return The list of board states with their corresponding scores.
     */
    public List<double[]> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Clears the move history, typically used to reset the game or start a new
     * one.
     */
    public void clearMoveHistory() {
        moveHistory.clear();
    }

    /**
     * Returns a string representation of the move history, displaying the board
     * and rating for each stored move in a format like:
     * [-2 -3 -4 ... 2 0.56]
     *
     * @return A formatted string showing the board and its rating as a list of
     * vectors.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < moveHistory.size(); i++) {
            String moveLabel = (i % 2 == 0) ? "[White move " + ((i / 2) + 1) + " - Rating] " : "[Black move " + ((i / 2) + 1) + " - Rating] ";
            sb.append(moveLabel);

            double[] move = moveHistory.get(i);
            for (int j = 0; j < 64; j++) {
                sb.append((int) move[j]).append(" ");
            }

            // Append the rating as the last element in the line
            sb.append(move[64]).append("\n");
        }

        return sb.toString();
    }
}
