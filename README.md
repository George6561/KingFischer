Features
Core Functionality
Game Mechanics:

Interactive chessboard implemented using ChessBoard and ChessWindow classes.
Move generation and validation using Stockfish.
Game states tracked and displayed in real-time.
Support for special moves like castling and pawn promotion.
Stockfish Integration:

Communicates with the Stockfish engine via the StockfishConnector class.
Retrieves optimal moves, position evaluations, and legal moves.
Adjustable analysis depth and move time for flexible gameplay and analysis.
Move Storage and Analysis:

Tracks move history using the MoveStorage class.
Evaluates game states and stores position scores.
User Interface
JavaFX-Based GUI:

Visual representation of the chessboard and pieces in ChessWindow and MainWindow.
Drag-and-drop functionality for piece movement.
Dynamically updates the board and provides user feedback.
Scalable Design:

Modular structure for easy extensibility.
Separation of concerns between game logic (ChessGame), UI (ChessWindow), and engine communication (StockfishConnector).
