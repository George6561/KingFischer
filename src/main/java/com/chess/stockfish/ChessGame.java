package com.chess.stockfish;

import com.chess.window.ChessWindow;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
 * Class: ChessGame
 *
 * This class serves as the main logic handler for a chess game application, managing 
 * interactions between the user interface (ChessWindow) and the Stockfish chess engine. 
 * It handles game initialization, move calculations, board updates, and game-end conditions.
 *
 * Key methods:
 * - `startGame()`: Initializes and starts the chess game, facilitating the main game loop.
 * - `playGameLoop()`: Orchestrates the sequence of moves between the Stockfish engine 
 *   playing both sides.
 * - `makeStockfishMove()`: Handles the move generation and application process for the 
 *   Stockfish engine.
 *
 * Dependencies:
 * - JavaFX (`Platform`) for UI thread updates.
 * - CountDownLatch for synchronizing asynchronous UI updates.
 * - ChessWindow for UI representation of the chessboard and board manipulation.
 * - StockfishConnector for communication with the Stockfish chess engine.
 *
 * Usage:
 * - Create an instance of ChessGame, passing a valid ChessWindow object to the constructor.
 * - Call `startGame()` to initialize the game and begin the main loop.
 */
public class ChessGame {

    private ChessWindow chessWindow;           // UI component for the chessboard
    private StockfishConnector stockfish;      // Stockfish engine for both sides
    private List<String> rawMoves;             // List of raw moves in UCI notation
    private boolean isWhiteToMove = true;      // Flag to track whose turn it is
    
    /**
     * Constructor to initialize the ChessGame with a given ChessWindow
     * instance.
     *
     * @param chessWindow The ChessWindow instance for displaying the chessboard
     * and handling UI.
     */
    public ChessGame(ChessWindow chessWindow) {
        this.chessWindow = chessWindow;
        this.stockfish = new StockfishConnector();
        this.rawMoves = new ArrayList<>(); // Initialize raw moves list
    }

    /**
     * Starts the chess game by initializing the Stockfish engine and entering
     * the main game loop.
     *
     * @throws IOException If an I/O error occurs while communicating with
     * Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI
     * updates.
     */
    public void startGame() throws IOException, InterruptedException {
        if (stockfish.startEngine()) {
            try {
                initializeStockfish();
                displayInitialBoard();
                playGameLoop();
            } finally {
                stockfish.stopEngine();
            }
        } else {
            System.out.println("Failed to start Stockfish engine.");
        }
    }

    /**
     * Initializes the Stockfish engine by sending necessary setup commands.
     *
     * @throws IOException If an I/O error occurs while communicating with
     * Stockfish.
     */
    private void initializeStockfish() throws IOException {
        stockfish.sendCommand("uci");
        stockfish.getResponse();
        stockfish.sendCommand("isready");
        stockfish.getResponse();
        stockfish.sendCommand("position startpos");
    }

    /**
     * Displays the initial board state in the ChessWindow UI.
     */
    private void displayInitialBoard() {
        Platform.runLater(() -> {
            try {
                chessWindow.displayChessPieces(-1, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Main game loop where Stockfish plays both sides.
     *
     * @throws IOException If an I/O error occurs while interacting with
     * Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI
     * updates.
     */
    private void playGameLoop() throws IOException, InterruptedException {
        String boardStateBeforeLoop = chessWindow.getBoard().toString();

        while (true) {
            // Make Stockfish move for the current side
            makeStockfishMove();

            // Update the UI to reflect the new board state
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    chessWindow.displayChessPieces(-1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
            latch.await();

            // Check for game-ending conditions
            String boardStateAfterLoop = chessWindow.getBoard().toString();
            if (boardStateBeforeLoop.equals(boardStateAfterLoop)) {
                System.out.println("Game over detected. Board state unchanged.");
                break;
            }

            if (chessWindow.getBoard().isCheckmate(chessWindow.getBoard().currentPlayer())) {
                System.out.println("Checkmate detected. Game over.");
                break;
            }

            boardStateBeforeLoop = boardStateAfterLoop;
            isWhiteToMove = !isWhiteToMove; // Switch sides
            Thread.sleep(500); // Pause between moves for readability
        }

        chessWindow.getBoard().resetBoard();
    }

    /**
     * Executes Stockfish's move for the current side and updates the game
     * state.
     *
     * @throws IOException If an I/O error occurs while interacting with
     * Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI
     * updates.
     */
    private void makeStockfishMove() throws IOException, InterruptedException {
        // Build the Stockfish position command with the current game state
        String positionCommand = "position startpos moves " + getMoveHistory();
        stockfish.sendCommand(positionCommand);

        // Ask Stockfish for the best move
        stockfish.sendCommand("go movetime 1000");
        String bestMove = stockfish.getBestMove();

        if (bestMove == null || bestMove.isEmpty()) {
            System.out.println("Stockfish could not find a move. Game over.");
            return;
        }

        // Apply the move to the board and update the move history
        updateMoveHistory(bestMove);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                chessWindow.movePiece(bestMove);
                chessWindow.displayChessPieces(-1, -1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await();

        chessWindow.getBoard().printBoardWithIndices();
    }

    /**
     * Updates the move history with the given move.
     *
     * @param move The move in UCI notation to add to the history.
     */
    public void updateMoveHistory(String move) {
        rawMoves.add(move);
    }

    /**
     * Retrieves the move history as a single formatted string.
     *
     * @return The move history in UCI notation.
     */
    public String getMoveHistory() {
        return String.join(" ", rawMoves);
    }

}
