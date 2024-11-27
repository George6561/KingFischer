package com.chess.ml;

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
 * Class: NeuralNetworkInterface
 *
 * This class provides a generic interface for interacting with a neural network in the chess application.
 * It abstracts the underlying implementation of the neural network, allowing for flexibility in integrating
 * different models or frameworks (e.g., TensorFlow, PyTorch).
 *
 * Key functionalities include:
 * - Accepting input data, such as chessboard states and move encodings.
 * - Processing the input through the neural network to produce predictions.
 * - Returning evaluation results, including position values and move probabilities.
 *
 * Dependencies:
 * - External machine learning libraries or APIs for model execution.
 *
 * Usage:
 * - Implement this interface in a concrete class (e.g., `TensorFlowInterface`, `PyTorchInterface`).
 * - Use `predict` to pass input data to the neural network and retrieve predictions.
 * - Integrate with higher-level classes like `Evaluator` for decision-making.
 */
public class NeuralNetworkInterface {
    
}
