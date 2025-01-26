// General Description
// Package: Checkers
// Purpose: Represents a single piece in the checkers game, holding information about the piece's color ('w' for white, 'b' for black) and its position on the board.
// Methods:
// Constructors to set the piece's color.
// getColour() and setPosition(Cell p) for accessing and updating the piece's color and position.
// getPosition() for retrieving the current position.
// getAvailableMoves(Cell[][] board) (not implemented) intended to calculate possible moves.
// capture() and promote() methods (not fully implemented) for handling captures and promotions of pieces.
// draw(App app) for drawing the piece on the board using Processing methods.

package Checkers;

import java.util.HashSet;
import java.util.Set;

public class CheckersPiece {

    // The color of the checkers piece ('w' for white, 'b' for black)
    private char colour;
    // To mark whether the current piece is a king
    private boolean isKing= false;
    // The current position of the piece on the board
    private Cell position;

    private HashSet<Cell> availableMoves;
    // Constructor: Initializes a new piece with a given color
    public CheckersPiece(char c) {
        this.colour = c;
    }

    // Returns the color of the piece
    public char getColour() {
        return this.colour;
    }

    // Sets the position of the piece to a given cell
    public void setPosition(Cell p) {
        this.position = p;
    }

    // Returns the current position of the piece
    public Cell getPosition() {
        return this.position;
    }

    public HashSet<Cell> getAvailableMoves(Cell[][] board) {
        //TODO: Get available moves for this piece depending on the board layout, and whether this piece is a king or not
        //How to record if the move is a capture or not? Maybe make a new class 'Move' that stores this information, along with the captured piece?
        // TODO：根据棋盘布局和该棋子是否为王，获取此棋子的可用移动方式
        // 如何记录移动是否为吃子？也许创建一个名为'Move'的新类来存储此信息，以及被吃掉的棋子？

        availableMoves=new HashSet<Cell>();

        //从Cell获取是否成王
        int fromX=position.getX();
        int fromY=position.getY();
        System.out.println("fromY:"+ fromY +"  fromX"+fromX);

        if (colour=='w' || isKing){
            int toX=fromX-1;
            int toY=fromY+1;

            check(fromX,fromY,toX,toY,board);


            toX=fromX+1;
            toY=fromY+1;
            check(fromX,fromY,toX,toY,board);

        }

        //为黑的 左前 & 右前
        if (colour=='b' || isKing){
            int toX=fromX-1;
            int toY=fromY-1;

            check(fromX,fromY,toX,toY,board);


            toX=fromX+1;
            toY=fromY-1;

            check(fromX,fromY,toX,toY,board);
        }

        return availableMoves;
    }

    // To check if a move is out of bounds:
    private boolean isValidMove(int toX, int toY, Cell[][] board) {
        return toX >= 0 && toX < board.length && toY >= 0 && toY < board[0].length;
    }

    //To check if two pieces have the same color, used for capturing
    private boolean isDifferentColor(char checkColor){

        return colour!= checkColor;
    }

    /* To check if a specific direction is a valid move
    *
    * If the direction is movable, store the position (Cell) on the board into availableMoves.
    * If the direction is not movable, then explore one more step in that direction to check if it is movable*/
    private void check(int fromX, int fromY,int toX, int toY, Cell[][] board) {
        //If the direction is movable, store the position (Cell) on the board into availableMoves.
        if (isValidMove(toX,toY,board)){
            if(board[toY][toX].getPiece() == null){
                availableMoves.add(board[toY][toX]);
            }
            //If the direction is not movable, then explore one more step in that direction to check if it is movable
            else {
                toX=toX+(toX-fromX);
                toY=toY+(toY-fromY);
                if (isValidMove(toX,toY,board) && board[toY][toX].getPiece() == null){
                    availableMoves.add(board[toY][toX]);
                }
            }
        }


    }
    public void capture() {
        //capture this piece
    }

    public void promote() {
        System.out.println("promote");
        //promote this piece
        isKing=true;
    }

    // Draws the piece on the board using the Processing library.
    // This method takes an instance of the App class, which extends PApplet from Processing, to access drawing methods.
    public void draw(App app)
    {
        // Set the stroke weight for the outline of the piece
        app.strokeWeight(5.0f);

        if (colour == 'w') {
            // White piece
            app.fill(255); // white fill
            app.stroke(0); // black stroke

            app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.8f, App.CELLSIZE*0.8f);

            if (isKing){
                app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.4f, App.CELLSIZE*0.4f);
            }
        } else if (colour == 'b') {
            // Black piece
            app.fill(0); // black fill
            app.stroke(255);// white stroke
            app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.8f, App.CELLSIZE*0.8f);

            if (isKing){
                app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.4f, App.CELLSIZE*0.4f);
            }
        }

        // Draw the piece as an ellipse (circle) at the piece's position, adjusting the coordinates based on the cell size
        // The method elipse takes 4 parameters
        // Syntax:  ellipse(a, b, c, d)
        // Parameters
        // a	(float)	x-coordinate of the ellipse
        // b	(float)	y-coordinate of the ellipse
        // c	(float)	width of the ellipse by default
        // d	(float)	height of the ellipse by default

        // Disable the stroke for subsequent drawings

        app.noStroke();
        //试着绘制同心圆
    }
}