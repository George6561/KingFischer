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
 * Class: Node
 *
 * This class represents a single node in a tree structure used to store chessboard states.
 * Each node corresponds to a specific board state and keeps track of the move that led
 * to the state, its parent node, and child nodes for possible transitions.
 *
 * Key functionalities include:
 * - Storing the chessboard state for the current position.
 * - Managing references to parent and child nodes.
 * - Storing statistics like visit count and win score for simulations.
 *
 * Dependencies:
 * - `ChessBoard` for handling chessboard states.
 *
 * Usage:
 * - Create a new `Node` instance for a chessboard state.
 * - Link child nodes to represent moves from the current state.
 * - Track statistics for simulation-based evaluation.
 */
class Node {
    private ChessBoard boardState;  // The state of the chessboard at this node
    private List<Node> children;   // List of child nodes
    private Node parent;           // Reference to the parent node
    private int[] move;            // The move that led to this position
    private int visitCount;        // Number of times this node has been visited
    private double winScore;       // Score used for simulations (Monte Carlo)

    /**
     * Constructor to initialize a node with a chessboard state, parent node, and move.
     *
     * @param boardState The chessboard state at this node.
     * @param parent The parent node that led to this state (null for root).
     * @param move The move that led to this state (null for root).
     */
    public Node(ChessBoard boardState, Node parent, int[] move) {
        this.boardState = boardState;
        this.children = new ArrayList<>();
        this.parent = parent;
        this.move = move;
        this.visitCount = 0;
        this.winScore = 0.0;
    }

    // Getters and Setters

    /**
     * Gets the chessboard state stored in this node.
     *
     * @return The `ChessBoard` object representing the board state.
     */
    public ChessBoard getBoardState() {
        return boardState;
    }

    /**
     * Gets the list of child nodes of this node.
     *
     * @return A list of child nodes.
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Gets the parent node of this node.
     *
     * @return The parent `Node`.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Gets the move that led to this node's position.
     *
     * @return An array representing the move [fromRow, fromCol, toRow, toCol].
     */
    public int[] getMove() {
        return move;
    }

    /**
     * Gets the number of times this node has been visited.
     *
     * @return The visit count.
     */
    public int getVisitCount() {
        return visitCount;
    }

    /**
     * Gets the win score of this node.
     *
     * @return The win score.
     */
    public double getWinScore() {
        return winScore;
    }

    /**
     * Increments the visit count of this node by one.
     */
    public void incrementVisitCount() {
        this.visitCount++;
    }

    /**
     * Adds a win score to this node's cumulative score.
     *
     * @param score The score to add.
     */
    public void addWinScore(double score) {
        this.winScore += score;
    }

    /**
     * Adds a child node to this node.
     *
     * @param child The child `Node` to add.
     */
    public void addChild(Node child) {
        children.add(child);
    }
}

