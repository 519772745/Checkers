// 概述
// 包: Checkers
// 目的: 扩展自Processing的PApplet的主游戏应用程序类。
// --> 它设置游戏的图形用户界面，初始化游戏状态，并处理用户输入和游戏更新。
// 特点:
// 用于定义游戏常量（如单元格大小和棋盘尺寸）的静态字段。
// 一个用于游戏棋盘的数组（Cell[][] board）和用于管理游戏中棋子的集合。
// 方法settings()、setup()和draw()用于初始化窗口、设置游戏状态，并分别逐帧绘制游戏框架。
// mousePressed(MouseEvent e)用于处理基于用户点击的棋子选择和移动。
// 用于图形操作的setFill(int colourCode, int blackOrWhite)等实用方法。
// 每个类都专门用于处理游戏的特定方面：
// CheckersPiece和Cell管理游戏的逻辑状态，而App将这些组件与Processing集成，
// --> 以管理游戏的图形界面和用户交互。它们共同形成一个有机结构，允许开发跳棋游戏。

package Checkers;

// Utilizes HashMap for storing collections of objects, where each item has a key and value. 
// Used here for managing pieces in play with their respective colors as keys.
// 使用HashMap存储对象集合，其中每个项都有一个键和一个值。
// 在此处使用于通过其相应颜色作为键来管理游戏中的棋子。
import java.util.Arrays;
import java.util.HashMap;
// Utilizes HashSet for storing unique elements, ensuring no duplicates. 
// Used for managing cells and pieces where uniqueness is essential, such as tracking selected cells or pieces in play.
// 使用HashSet存储唯一元素，确保没有重复。
// 用于管理单元格和棋子等需要唯一性的元素，例如跟踪选定的单元格或正在进行的棋子。
import java.util.HashSet;

//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;

// Importing core PApplet class from the Processing library, which is the basis for creating drawing windows and handling events.
// Processing library imports for graphical interface and interactions
// 从Processing库中导入核心PApplet类，该类是创建绘图窗口和处理事件的基础。
// 用于图形界面和交互的Processing库导入
import processing.core.PApplet;
// Import for handling mouse events, enabling interactive components like clicking on pieces or cells.
// 导入处理鼠标事件的库，使得可以进行诸如点击棋子或单元格之类的交互组件。
import processing.event.MouseEvent;

public class App extends PApplet {

    // Constants for game configuration, defining the visual and structural aspects of the checkers board.

    public static final int CELLSIZE = 48; // 棋盘上每个单元格的大小，影响游戏视觉表示的整体比例
    public static final int SIDEBAR = 0; //未使用的侧边栏的宽度，可能保留用于未来功能，例如游戏统计信息或控制。
    public static final int BOARD_WIDTH = 8; // 跳棋棋盘的宽度和高度，定义一个8x8网格。
    public static final int[] BLACK_RGB = {181, 136, 99}; // 黑色单元格的RGB值
    public static final int[] WHITE_RGB = {240, 217, 181}; // 白色单元格的RGB值

    // Color schemes for the board and pieces, allowing for visual customization beyond the traditional black and white.
    // Array to hold custom color schemes for the board (white & black, green, blue)
    // 用于棋盘和棋子的颜色方案，允许超出传统黑白之外的视觉自定义。
    // 用于保存棋盘的自定义颜色方案的数组（白色和黑色、绿色、蓝色）
    public static final float[][][] coloursRGB = new float[][][] {
            // 默认方案-黑色和白色
            {
                    {WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]},
                    {BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]}
            },
            // Green scheme, potentially for highlighting or alternative themes.
            {
                    {105, 138, 76}, // Green for white cells
                    {105, 138, 76}  // Green for black cells
            },
            // Blue scheme, offering another alternative visual theme.
            {
                    {196,224,232}, // Light blue
                    {170,210,221}  // Slightly darker blue
            }
    };


    // 确定窗口大小的静态变量
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE;


    public static final int FPS = 60 ; // Frames per second for the animation

    /* --------------------------------------- */
    // 数据存储
    /* --------------------------------------- */

    // 存储游戏状态：棋盘布局、当前和选定的棋子以及哪些棋子正在游戏中

    private Cell[][] board; // Represents the game board as a grid of cells.
    private CheckersPiece currentSelected; // 当前选定的棋子，如果有的话。
    private HashSet<Cell> selectedCells = new HashSet<>();; // 高光显示可以移动的单元格
    private HashMap<Character, HashSet<CheckersPiece>> piecesInPlay = new HashMap<>(); // Active pieces,  通过颜色（'w'表示白色，'b'表示黑色）进行区分。
    private char currentPlayer = 'w'; // 当前的轮次, alternating between 'w' (white) and 'b' (black).
    private int round=0;
    // 默认构造函数
    public App() {

    }


    // Essential Processing methods for setting up the window, initializing the game state, and drawing the board and pieces.
    // 设置窗口 | 初始化游戏状态 | 绘制棋盘和棋子
    // Setup method to initialize window settings
    @Override
    public void settings() {
        size(WIDTH, HEIGHT); // Set the size of the application window based on the board dimensions.
    }

    // Initial setup for the game, executed once at the beginning
    // 游戏的初始设置，在开始时执行一次
    @Override
    public void setup() {
        frameRate(FPS); // Set the frame rate 设置帧率

        // 初始化棋盘并用棋子填充它
        // 设置用于存储游戏数据的数据结构
        // Initialize the board and populate it with pieces
        // Set up the data structures used for storing data in the game
        this.board = new Cell[BOARD_WIDTH][BOARD_WIDTH];
        HashSet<CheckersPiece> w = new HashSet<>();
        HashSet<CheckersPiece> b = new HashSet<>();
        piecesInPlay.put('w', w);
        piecesInPlay.put('b', b);

        // Populate the board with pieces in initial positions
        // 在初始位置上用棋子填充棋盘
        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2] = new Cell(i2,i);
                // Place white and black pieces on the board in their initial positions
                if ((i2+i) % 2 == 1) {
                    if (i < 3) {
                        // 在前三行初始化白色棋子
                        board[i][i2].setPiece(new CheckersPiece('w'));
                        w.add(board[i][i2].getPiece());
                    } else if (i >= 5) {
                        // 在后三行初始化黑色棋子
                        board[i][i2].setPiece(new CheckersPiece('b'));
                        b.add(board[i][i2].getPiece());
                    }
                }
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     * 键盘按下
     */
    @Override
    public void keyPressed(){

    }

    /**
     * Receive key released signal from the keyboard.
     * 键盘弹起
     */
    @Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Check if the user clicked on a piece which is theirs - make sure only whoever's current turn it is, can click on pieces
        // 检查用户是否点击了属于自己的棋子 - 确保只有轮到当前玩家时才能点击棋子

        // 获得棋子当前的坐标
        int x = e.getX();
        int y = e.getY();

        //如果越界，则返回
        if (x < 0 || x >= App.WIDTH || y < 0 || y >= App.HEIGHT) return;

        Cell clicked = board[y/App.CELLSIZE][x/App.CELLSIZE];

        //如果点了棋子，而且是己方轮次的棋子
        if (clicked.getPiece() != null && clicked.getPiece().getColour() == currentPlayer) {

            // deselect a piece:
            if (clicked.getPiece() == currentSelected) {
                selectedCells=null;
                currentSelected = null;
            }
            // select a piece:
            else {
                currentSelected = clicked.getPiece();

                /*To find reachable areas*/
                selectedCells = null;
                selectedCells=clicked.getPiece().getAvailableMoves(board);
            }


        }
        /* Check if user clicked on an available move - move the selected piece there.
        * */
        else if(currentSelected != null && currentSelected.getColour() == currentPlayer){

            for (Cell cell:selectedCells){

                /* If the user clicks on a movable area, then

                1. Check if there is a need to capture the target piece
                2. Set the original position to empty
                3. Move the piece to the new position
                4. Check if piece should be promoted and promote it
                5. Reset, set the currently selected piece and available paths to empty.
                6. The game round +=1.*/
                if (clicked.getX()==cell.getX() && clicked.getY()==cell.getY()){

                    // 1. Check if there is a need to capture the target piece
                    if (Math.abs(clicked.getX()-currentSelected.getPosition().getX()) ==2){
                        int tempX=(clicked.getX()+currentSelected.getPosition().getX())/2;
                        int tempY=(clicked.getY()+currentSelected.getPosition().getY())/2;

                        CheckersPiece tempPiece=board[tempY][tempX].getPiece();

                        // capture
                        if(tempPiece.getColour()!=currentSelected.getColour()){
                            piecesInPlay.get(tempPiece.getColour()).remove(tempPiece);

                            board[tempY][tempX].setPiece(null);
                        }

                    }

                    // 2.Set the original position to empty
                    board[currentSelected.getPosition().getY()][currentSelected.getPosition().getX()].setPiece(null);

                    // 3.Move the piece to the new position
                    board[clicked.getY()][clicked.getX()].setPiece(currentSelected);

                    // 4.Check if piece should be promoted and promote it
                    if (currentSelected.getColour()=='w' && clicked.getY()==7){
                        currentSelected.promote();
                    }else if (currentSelected.getColour()=='b' && clicked.getY()==0){
                        currentSelected.promote();
                    }

                    // 5. Reset, set the currently selected piece and available paths to empty.
                    currentSelected=null;
                    selectedCells=null;

                    // 6. The game round +=1
                    round++;
                    break;
                }
            }
        }


        /* Then it's the other player's turn.
        *  When the round number is even, it's the turn of the white pieces.
        *  When the round number is odd, it's the turn of the black pieces.*/
        if (round%2 == 0){
            currentPlayer='w';
        }else {
            currentPlayer='b';
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame. 
     */
    public void draw() {
        this.noStroke(); // Disable drawing outlines to prepare for drawing filled shapes.
        background(WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]); // Set the background color of the board.

        // Draw the board and the pieces.
        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                if (currentSelected != null && board[i][i2].getPiece() == currentSelected) {
                    // Highlight the selected cell if it contains the current selected piece.
                    // 如果包含当前选定的棋子，则突出显示选定的单元格。
                    this.setFill(1, (i2+i) % 2);
                    this.rect(i2*App.CELLSIZE, i*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                } else if ((i2+i) % 2 == 1) {
                    // Regular drawing for unselected cells.
                    // 未选定单元格的常规绘制。
                    this.fill(BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]);
                    this.rect(i2*App.CELLSIZE, i*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                }

                board[i][i2].draw(this); // Draw the piece in the cell, if any.
            }
        }
        /* Highlight the available paths:*/
        if (selectedCells!=null){
            for (Cell cell : selectedCells) {
                this.setFill(2, (cell.getY()+cell.getX()) % 2);
                this.rect( cell.getX()*App.CELLSIZE, cell.getY()*App.CELLSIZE,App.CELLSIZE, App.CELLSIZE);
            }
        }

        // Check for end game condition where one player has no more pieces.
        if (piecesInPlay.get('w').size() == 0 || piecesInPlay.get('b').size() == 0) {
            // Display the winner.
            fill(255);
            stroke(0);
            strokeWeight(4.0f);
            rect(App.WIDTH*0.2f-5, App.HEIGHT*0.4f-25, 150, 40); // Draw a rectangle for the text background.
            fill(200,0,200);
            textSize(24.0f); // Set text size
            if (piecesInPlay.get('w').size() == 0) {
                text("Black wins!", App.WIDTH*0.2f, App.HEIGHT*0.4f);
            } else if (piecesInPlay.get('b').size() == 0) {
                text("White wins!", App.WIDTH*0.2f, App.HEIGHT*0.4f);
            }
        }
    }

    /**
     * Set fill colour for cell background
     * @param colourCode The colour to set
     * @param blackOrWhite Depending on if 0 (white) or 1 (black) then the cell may have different shades
     */
    public void setFill(int colourCode, int blackOrWhite) {
        // Set the fill color for drawing cells, allowing for different themes or highlights.
        this.fill(coloursRGB[colourCode][blackOrWhite][0], coloursRGB[colourCode][blackOrWhite][1], coloursRGB[colourCode][blackOrWhite][2]);
    }


    public static void main(String[] args) {
        PApplet.main("Checkers.App"); // Launch the Processing application.
    }


}