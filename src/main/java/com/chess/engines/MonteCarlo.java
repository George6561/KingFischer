package com.chess.engines;

import com.chess.board.ChessBoard;
import com.chess.main.ChessGame;
import com.chess.window.ChessWindow;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import com.chess.stockfish.StockfishConnector;

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
 * Class: StockfishConnector
 *
 * This class provides an interface for communicating with the Stockfish chess engine.
 * It is used to start, stop, and interact with the engine to evaluate positions, calculate 
 * best moves, and perform other operations that enhance the chess-playing capabilities of 
 * the application.
 *
 * Key functionalities include:
 * - Starting and stopping the Stockfish engine.
 * - Sending commands to and receiving responses from the Stockfish engine.
 * - Retrieving the best move and other analytical information from the engine.
 * - Setting options for the engine such as analysis depth, number of threads, and debug mode.
 * - Querying legal moves and verifying the validity of a given move.
 * - Handling game state updates to keep Stockfish informed of the current position on the chessboard.
 *
 * Dependencies:
 * - The Stockfish engine executable, referenced by `ENGINE_SOURCE`. Ensure that the executable path is correctly specified.
 * - Java I/O classes (`BufferedReader`, `BufferedWriter`, etc.) for interacting with Stockfish.
 *
 * Usage:
 * - `startEngine()`: Starts the Stockfish engine process.
 * - `sendCommand(String command)`: Sends a specific UCI command to the engine.
 * - `getBestMove()`: Retrieves the best move suggested by Stockfish based on the current game state.
 * - `stopEngine()`: Stops the engine and releases all related resources.
 *
 * Notes:
 * - The engine operates using the Universal Chess Interface (UCI), which allows for clear command and response protocols.
 * - Some methods rely on a proper sequence of commands to ensure the engine is in the correct state before processing further instructions.
 * - Use `isEngineReady()` to ensure the engine is ready for a new command after initialization or heavy operations.
 */
public class MonteCarlo implements ChessEngine {

    private ChessWindow chessWindow;
    private ChessGame chessGame; // Reference to the main ChessGame instance
    private Random random;
    private StockfishConnector stockfish;

    // Variables for learning
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int draws;
    private Map<String, Integer> moveStatistics; // Key: move notation, Value: success rate

    /**
     * Constructor for the MonteCarlo class.
     *
     * @param chessWindow The ChessWindow instance used for displaying and
     * interacting with the chess game.
     * @param engine The Stockfish engine instance used for engine interactions
     * and move updates.
     * @param chessGame The ChessGame instance for accessing and updating the
     * main move history and game state.
     */
    public MonteCarlo(ChessWindow chessWindow, StockfishConnector engine, ChessGame chessGame) {
        this.chessWindow = chessWindow;
        this.stockfish = engine;
        this.chessGame = chessGame;
        this.random = new Random();
        this.gamesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.moveStatistics = new HashMap<>();
    }

    /**
     * Returns a random legal move for the Black player in standard chess
     * notation.
     *
     * @return A string representing the random move in standard chess notation.
     * @throws IOException If an I/O error occurs while interacting with the
     * ChessBoard.
     */
    @Override
    public String getNextMove() throws IOException {
        List<int[]> legalMoves = chessWindow.getBoard().getAllLegalMoves(ChessBoard.Player.BLACK);

        if (legalMoves.isEmpty()) {
            System.out.println("Black has no legal moves. Game over.");
            return null;
        }

        int[] randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
        String from = chessWindow.getBoard().toChessNotation(randomMove[0], randomMove[1]);
        String to = chessWindow.getBoard().toChessNotation(randomMove[2], randomMove[3]);
        return from + to;
    }

    /**
     * Executes the random legal move obtained from getNextMove() and updates
     * the game state.
     *
     * @throws IOException If an I/O error occurs while interacting with the
     * ChessBoard or Stockfish.
     * @throws InterruptedException If the thread is interrupted while waiting
     * for UI updates.
     */
    public void makeRandomMove() throws IOException, InterruptedException {
        try {
            String randomMoveNotation = getNextMove();

            if (randomMoveNotation == null) {
                return; // No legal moves available; the game is over.
            }

            // Update move statistics with initial weight if not present
            moveStatistics.putIfAbsent(randomMoveNotation, 0);

            // Update the main game move history
            chessGame.updateMoveHistory(randomMoveNotation);

            // Execute the move and update the UI
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    chessWindow.movePiece(randomMoveNotation);
                    chessWindow.displayChessPieces(-1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
            latch.await();

            // Print the updated board state
            chessWindow.getBoard().printBoardWithIndices();

            // Send the updated position to Stockfish
            stockfish.sendCommand("position startpos moves " + chessGame.getMoveHistory().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Records the outcome of the game and updates the statistics.
     *
     * @param result The result of the game ("win", "loss", or "draw").
     */
    public void recordGameOutcome(String result) {
        gamesPlayed++;
        switch (result.toLowerCase()) {
            case "win":
                wins++;
                break;
            case "loss":
                losses++;
                break;
            case "draw":
                draws++;
                break;
        }
        System.out.println("Game result recorded: " + result);
        printStatistics();
    }

    /**
     * Prints the current statistics of the Monte Carlo engine.
     */
    private void printStatistics() {
        System.out.println("Games Played: " + gamesPlayed);
        System.out.println("Wins: " + wins);
        System.out.println("Losses: " + losses);
        System.out.println("Draws: " + draws);
        System.out.println("Move Success Rates: " + moveStatistics);
    }

    /**
     * Updates the move success rate for a given move based on the game result.
     *
     * @param move The move in standard chess notation.
     * @param isSuccess True if the move contributed to a win; false otherwise.
     */
    public void updateMoveStatistics(String move, boolean isSuccess) {
        moveStatistics.put(move, moveStatistics.getOrDefault(move, 0) + (isSuccess ? 1 : 0));
    }
}
