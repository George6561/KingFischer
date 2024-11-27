package com.chess.montecarlo;

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
 * Class: UCTSelector
 *
 * This class is responsible for selecting the best child node based on the Upper Confidence Bound
 * for Trees (UCT) formula. It balances exploration and exploitation during Monte Carlo Tree Search
 * simulations.
 *
 * Key functionalities include:
 * - Calculating UCT values for child nodes.
 * - Selecting the node with the highest UCT value for further exploration.
 * - Configuring the exploration constant to tune the balance between exploration and exploitation.
 *
 * Dependencies:
 * - `MonteCarloNode` for accessing node statistics such as visit counts and win scores.
 *
 * Usage:
 * - Instantiate a `UCTSelector` object.
 * - Use `selectChild` to identify the best child node from a given parent.
 * - Configure the exploration constant to adjust the UCT formula.
 */
public class UCTSelector {
    
}
