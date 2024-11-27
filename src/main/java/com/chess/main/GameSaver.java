package com.chess.main;

import com.chess.board.ChessBoard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * Class: GameSaver
 *
 * This utility class provides functionality to save completed chess games to disk
 * in a machine-readable format. It ensures the games are stored in a sequentially
 * numbered file within a predefined directory for easy access and analysis.
 *
 * Key functionalities include:
 * - Formatting raw moves into readable chess notation.
 * - Simulating moves on a chessboard to determine special notations like checks and castling.
 * - Saving games into incrementally named files in the "games" folder.
 *
 * Dependencies:
 * - `ChessBoard` for simulating moves and checking game states.
 *
 * Usage:
 * - Use `saveGame` to save a game's move history to a file.
 * - Use `formatMoves` to convert a list of moves into readable notation.
 */
public class GameSaver {

    private static final String GAMES_FOLDER_NAME = "games"; // Folder to save games
    private static final String FILE_PREFIX = "game_";       // Prefix for filenames
    private static final String FILE_EXTENSION = ".txt";     // File extension for saved games

    /**
     * Saves a completed chess game to disk.
     *
     * This method formats the game's move history, ensures the appropriate
     * folder exists, and writes the formatted game to a sequentially named file.
     *
     * @param rawMoves The list of moves in raw UCI notation.
     * @return The `File` object representing the saved game file.
     * @throws IOException If an error occurs during file creation or writing.
     */
    public static File saveGame(List<String> rawMoves) throws IOException {
        ChessBoard board = new ChessBoard();
        board.resetBoard(); // Ensure the board starts at the default state

        String formattedMoves = formatMoves(rawMoves, board);

        // Ensure the "games" folder exists
        File gamesFolder = new File(GAMES_FOLDER_NAME);
        if (!gamesFolder.exists() && !gamesFolder.mkdir()) {
            throw new IOException("Failed to create directory: " + GAMES_FOLDER_NAME);
        }

        // Determine the next available filename
        File gameFile = getAvailableGameFile();

        // Write the formatted moves to the file
        try (FileWriter writer = new FileWriter(gameFile)) {
            writer.write(formattedMoves);
        }

        return gameFile;
    }

    /**
     * Formats a list of raw moves into human-readable chess notation.
     *
     * This method processes the raw moves, simulates them on a chessboard,
     * and adds special notations for castling, captures, checks, and checkmates.
     *
     * @param rawMoves The list of raw moves in UCI notation.
     * @param board The `ChessBoard` instance used to simulate moves.
     * @return A string representing the formatted moves.
     */
    public static String formatMoves(List<String> rawMoves, ChessBoard board) {
        StringBuilder formattedMoves = new StringBuilder();
        int moveCounter = 1;

        for (int i = 0; i < rawMoves.size(); i++) {
            String move = rawMoves.get(i);

            int fromRow = 8 - Character.getNumericValue(move.charAt(1));
            int fromCol = move.charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(move.charAt(3));
            int toCol = move.charAt(2) - 'a';

            int piece = board.getPieceAt(fromRow, fromCol);
            int destinationPiece = board.getPieceAt(toRow, toCol);

            if (piece == 0) {
                System.err.println("Error: No piece found at source (" + fromRow + ", " + fromCol + ")");
                continue; // Skip invalid move
            }

            String castling = detectCastling(piece, fromRow, fromCol, toRow, toCol);
            if (!castling.isEmpty()) {
                if (i % 2 == 0) {
                    formattedMoves.append(moveCounter).append(". ").append(castling).append(" ");
                } else {
                    formattedMoves.append(castling).append("\n");
                    moveCounter++;
                }
                board.movePiece(fromRow, fromCol, toRow, toCol);
                continue;
            }

            String capture = destinationPiece != 0
                ? (Math.abs(piece) == 1 ? (char) ('a' + fromCol) + "x" : "x")
                : "";

            String pieceNotation = getPieceNotation(piece);
            String moveNotation = board.toChessNotation(toRow, toCol);

            String fullMoveNotation = Math.abs(piece) == 1
                ? capture + moveNotation
                : pieceNotation + capture + moveNotation;

            board.movePiece(fromRow, fromCol, toRow, toCol);
            board.nextMove();

            if (board.isCheckmate(board.currentPlayer())) {
                fullMoveNotation += "#";
            } else if (board.isInCheck(board.currentPlayer())) {
                fullMoveNotation += "+";
            }

            if (i % 2 == 0) {
                formattedMoves.append(moveCounter).append(". ").append(fullMoveNotation).append(" ");
            } else {
                formattedMoves.append(fullMoveNotation).append("\n");
                moveCounter++;
            }
        }

        return formattedMoves.toString();
    }

    /**
     * Detects if a move is castling.
     *
     * @param piece The piece being moved.
     * @param fromRow The starting row of the piece.
     * @param fromCol The starting column of the piece.
     * @param toRow The destination row of the piece.
     * @param toCol The destination column of the piece.
     * @return A string indicating castling ("O-O" or "O-O-O") or an empty string if not castling.
     */
    private static String detectCastling(int piece, int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(piece) == 6) {
            if (fromCol == 4 && toCol == 6) {
                return "O-O";
            } else if (fromCol == 4 && toCol == 2) {
                return "O-O-O";
            }
        }
        return "";
    }

    /**
     * Determines the next available game file name in the "games" folder.
     *
     * @return A `File` object representing the available file.
     */
    private static File getAvailableGameFile() {
        int firstBlock = 0, secondBlock = 0, thirdBlock = 0;

        while (true) {
            String filename = String.format(
                "%s%03d_%03d_%03d%s", FILE_PREFIX, firstBlock, secondBlock, thirdBlock, FILE_EXTENSION
            );
            File potentialFile = new File(GAMES_FOLDER_NAME, filename);

            if (!potentialFile.exists()) {
                return potentialFile;
            }

            thirdBlock++;
            if (thirdBlock > 999) {
                thirdBlock = 0;
                secondBlock++;
            }
            if (secondBlock > 999) {
                secondBlock = 0;
                firstBlock++;
            }
        }
    }

    /**
     * Maps a piece's numerical value to its standard chess notation.
     *
     * @param piece The numerical value of the piece.
     * @return A string representing the piece in chess notation.
     */
    private static String getPieceNotation(int piece) {
        return switch (Math.abs(piece)) {
            case 2 -> "R";
            case 3 -> "N";
            case 4 -> "B";
            case 5 -> "Q";
            case 6 -> "K";
            default -> "";
        };
    }
}
