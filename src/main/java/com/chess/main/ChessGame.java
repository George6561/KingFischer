package com.chess.main;

import com.chess.board.ChessBoard;
import com.chess.engines.MonteCarlo;
import com.chess.events.GameEndListener;
import com.chess.stockfish.StockfishConnector;
import com.chess.window.ChessWindow;
import javafx.application.Platform;

import java.io.File;
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
 * This class serves as the main game controller for the chess application. It integrates
 * the user interface (UI), chessboard state, and AI engines (Stockfish and Monte Carlo)
 * to facilitate a playable chess game. It manages the game's lifecycle, including moves,
 * event listeners, and endgame handling.
 *
 * Key functionalities include:
 * - Managing game state and alternating turns between Stockfish (White) and Monte Carlo (Black).
 * - Handling UI updates and user interactions through `ChessWindow`.
 * - Communicating with Stockfish for move calculations and game state evaluations.
 * - Saving and formatting completed games using `GameSaver`.
 *
 * Dependencies:
 * - `ChessWindow` for UI interaction.
 * - `ChessBoard` for managing board state.
 * - `MonteCarlo` and `StockfishConnector` as AI engines.
 *
 * Usage:
 * - Instantiate `ChessGame` with a `ChessWindow` instance.
 * - Start the game using `startGame()`.
 * - Add listeners for endgame events with `addGameEndListener()`.
 */
public class ChessGame {

    private ChessWindow chessWindow;           // UI component for the chessboard
    private StockfishConnector stockfish;      // Stockfish engine for White
    private MonteCarlo monteCarlo;             // Monte Carlo engine for Black
    private List<String> rawMoves;             // List of raw moves in UCI notation
    private List<GameEndListener> gameEndListeners; // Event listeners for game end
    private boolean isWhiteToMove = true;      // Flag to track which side's turn it is

    /**
     * Constructor to initialize the ChessGame with a given ChessWindow instance.
     *
     * @param chessWindow The ChessWindow instance for displaying the chessboard and handling UI.
     */
    public ChessGame(ChessWindow chessWindow) {
        this.chessWindow = chessWindow;
        this.stockfish = new StockfishConnector();
        this.monteCarlo = new MonteCarlo(chessWindow, stockfish, this); // Initialize MonteCarlo instance
        this.rawMoves = new ArrayList<>(); // Initialize raw moves list
        this.gameEndListeners = new ArrayList<>(); // Initialize the event listeners list
    }

    /**
     * Starts the chess game by initializing the Stockfish engine and entering the main game loop.
     *
     * @throws IOException If an I/O error occurs while communicating with Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI updates.
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
     * Adds a game end listener to the list of listeners.
     *
     * @param listener The listener to be notified when the game ends.
     */
    public void addGameEndListener(GameEndListener listener) {
        gameEndListeners.add(listener);
    }

    /**
     * Removes a game end listener from the list.
     *
     * @param listener The listener to be removed.
     */
    public void removeGameEndListener(GameEndListener listener) {
        gameEndListeners.remove(listener);
    }

    /**
     * Notifies all listeners about the end of the game and saves the game to a file.
     */
    private void triggerGameEndEvent() {
        for (GameEndListener listener : gameEndListeners) {
            listener.onGameEnd(this.monteCarlo);
        }

        try {
            List<String> rawMoves = getRawMoves();
            ChessBoard board = chessWindow.getBoard();
            board.resetBoard();

            String formattedMoves = GameSaver.formatMoves(rawMoves, board);
            File savedFile = GameSaver.saveGame(rawMoves);

            System.out.println("Game saved to: " + savedFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    /**
     * Initializes the Stockfish engine by sending necessary setup commands.
     *
     * @throws IOException If an I/O error occurs while communicating with Stockfish.
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
     * Main game loop alternating between Stockfish (White) and Monte Carlo (Black).
     *
     * @throws IOException If an I/O error occurs while interacting with Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI updates.
     */
    private void playGameLoop() throws IOException, InterruptedException {
        String boardStateBeforeLoop = chessWindow.getBoard().toString();

        while (true) {
            if (isWhiteToMove) {
                makeStockfishMove();
            } else {
                monteCarlo.makeRandomMove();
            }

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

            String boardStateAfterLoop = chessWindow.getBoard().toString();

            if (boardStateBeforeLoop.equals(boardStateAfterLoop)) {
                System.out.println("Game over detected. Board state unchanged.");
                triggerGameEndEvent();
                break;
            }

            if (chessWindow.getBoard().isCheckmate(chessWindow.getBoard().currentPlayer())) {
                System.out.println("Checkmate detected. Game over.");
                triggerGameEndEvent();
                break;
            }

            boardStateBeforeLoop = boardStateAfterLoop;
            isWhiteToMove = !isWhiteToMove;

            Thread.sleep(500);
        }

        chessWindow.getBoard().resetBoard();
    }

    /**
     * Executes Stockfish's move as White and updates the game state.
     *
     * @throws IOException If an I/O error occurs while interacting with Stockfish.
     * @throws InterruptedException If the thread is interrupted during UI updates.
     */
    private void makeStockfishMove() throws IOException, InterruptedException {
        stockfish.sendCommand("go movetime 1000");
        String bestMove = stockfish.getBestMove();

        if (bestMove == null || bestMove.isEmpty()) {
            System.out.println("Stockfish could not find a move. Game over.");
            return;
        }

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
     * Retrieves the raw move history as a list.
     *
     * @return A list of moves in UCI notation.
     */
    public List<String> getRawMoves() {
        return rawMoves;
    }

    /**
     * Retrieves the entire move history as a single formatted string.
     *
     * @return The move history in UCI notation as a single string.
     */
    public String getMoveHistory() {
        return String.join(" ", rawMoves);
    }
}
