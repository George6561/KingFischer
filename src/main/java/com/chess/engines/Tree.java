package com.chess.engines;

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
 * Class: Tree
 *
 * This class represents a tree data structure used to store the state of a chess game.
 * Each node in the tree represents a single position on the chessboard, and edges
 * between nodes represent moves that transition from one state to another.
 *
 * Key functionalities include:
 * - Managing a tree of board states, starting from an initial root position.
 * - Adding child nodes representing possible moves from a given position.
 * - Navigating the tree to explore different game paths based on moves.
 * - Resetting the tree to the root node for a new game.
 *
 * Dependencies:
 * - `Node` for representing individual board states as nodes in the tree.
 * - `ChessBoard` for handling chessboard states.
 *
 * Usage:
 * - Instantiate a `Tree` object with an initial `ChessBoard` state.
 * - Use `addChild` to add moves and update the tree with new positions.
 * - Navigate to specific children with `moveToChild`, or reset to the starting state with `resetToRoot`.
 */
class Tree {
    private Node root;  // Root node of the tree
    private Node currentNode;  // Tracks the current node in the game

    /**
     * Constructor to initialize the tree with the root node.
     *
     * @param initialBoard The initial chessboard state for the game.
     */
    public Tree(ChessBoard initialBoard) {
        this.root = new Node(initialBoard, null, null); // Root node has no parent or move.
        this.currentNode = root;
    }

    /**
     * Retrieves the current node in the tree.
     *
     * The current node represents the current position in the game. This method
     * is useful for accessing the board state and other details about the current
     * game position.
     *
     * @return The current `Node` in the tree.
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Adds a child node to a given parent node, representing a move from the parent state.
     *
     * This method creates a new `Node` with the specified board state and move,
     * then links it to the parent node as a child. This is used to expand the tree
     * with possible moves from a given position.
     *
     * @param parent The parent node to which the new child node will be added.
     * @param newBoard The chessboard state after applying the move.
     * @param move An array representing the move [fromRow, fromCol, toRow, toCol].
     * @return The newly created child node.
     */
    public Node addChild(Node parent, ChessBoard newBoard, int[] move) {
        Node child = new Node(newBoard, parent, move); // Create a new child node.
        parent.addChild(child); // Link the child to the parent.
        return child;
    }

    /**
     * Advances the current node to a child node based on the specified move.
     *
     * This method navigates to a child node whose move matches the provided move.
     * If no matching child is found, an exception is thrown.
     *
     * @param move The move to match, represented as an array [fromRow, fromCol, toRow, toCol].
     * @throws IllegalArgumentException If no child node matches the specified move.
     */
    public void moveToChild(int[] move) {
        for (Node child : currentNode.getChildren()) {
            if (child.getMove() != null && isSameMove(child.getMove(), move)) {
                currentNode = child;
                return;
            }
        }
        throw new IllegalArgumentException("Move not found among children");
    }

    /**
     * Resets the tree to the root node.
     *
     * This method resets the `currentNode` to the root of the tree, effectively
     * resetting the game state to the initial position.
     */
    public void resetToRoot() {
        currentNode = root;
    }

    /**
     * Compares two moves for equality.
     *
     * This utility method checks if two moves (represented as integer arrays)
     * are identical, taking into account both their length and individual elements.
     *
     * @param move1 The first move to compare.
     * @param move2 The second move to compare.
     * @return `true` if the moves are identical, `false` otherwise.
     */
    private boolean isSameMove(int[] move1, int[] move2) {
        if (move1 == null || move2 == null || move1.length != move2.length) {
            return false;
        }
        for (int i = 0; i < move1.length; i++) {
            if (move1[i] != move2[i]) {
                return false;
            }
        }
        return true;
    }
}
