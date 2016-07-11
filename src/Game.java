import java.util.Random;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements Runnable {
    //Common properties
    private Thread animator;

    //Instance properties
    private Font font;
    private Board board = null;
    private Menu menu = null;
    private Store store = null;
    public MatchStone matchstone = null;
    private int[] HighScores;

    //Game state properties --needs heavy refactoring. UBASAK
    public static int GameMode = 2;
    private long lastDraw;
    private boolean notall = true;
    public boolean Key = false;
    private int KeyMove = 0;
    public static boolean falling = false;
    private int falling_times = 0;
    public static boolean GAMEOVER = true;
    public static int Level = 6;
    public static int puan = 0;
    private long timePressed = 0; //about when a key is pressed or released
    public static int flashing = 0;
    public static int Continue = 0;
    private int transCell = 0; //When the game ends using this propert board will be covered with a transparent layer

    public void initGame() {
        System.out.println("Game::InitGame");
        setKeyBindings();
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(Constants.B_WIDTH, Constants.B_HEIGHT));
        setDoubleBuffered(true);
    }

    private void setKeyBindings() {
        ActionMap actionMap = getActionMap();
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = getInputMap(condition);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), Constants.KEY_LEFT_ARROW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), Constants.KEY_RIGHT_ARROW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), Constants.KEY_UP_ARROW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), Constants.KEY_DOWN_ARROW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Constants.KEY_SOFTKEY1);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), Constants.KEY_SOFTKEY2);

        actionMap.put(Constants.KEY_LEFT_ARROW, new KeyAction(Constants.KEY_LEFT_ARROW));
        actionMap.put(Constants.KEY_RIGHT_ARROW, new KeyAction(Constants.KEY_RIGHT_ARROW));
        actionMap.put(Constants.KEY_UP_ARROW, new KeyAction(Constants.KEY_UP_ARROW));
        actionMap.put(Constants.KEY_DOWN_ARROW, new KeyAction(Constants.KEY_DOWN_ARROW));
        actionMap.put(Constants.KEY_SOFTKEY1, new KeyAction(Constants.KEY_SOFTKEY1));
        actionMap.put(Constants.KEY_SOFTKEY2, new KeyAction(Constants.KEY_SOFTKEY2));
    }

    @
    Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @
    Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        myPaint(g, notall);
    }

    private void cycle() {
        //Any updates
        if (!GAMEOVER) {
            board.CheckGameOver(matchstone);
            if (System.currentTimeMillis() - lastDraw >= Constants.speed) {
                if (board.Check(matchstone)) {
                    board.Update(matchstone);
                    //matchstone = null;
                    matchstone = new MatchStone();
                    //Key = false;
                    board.CheckIsFull();
                } else {
                    matchstone.CellY++;
                }
                lastDraw = System.currentTimeMillis();
            }
        }
    }

    @
    Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

            cycle();
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = Constants.DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    public Game() {
        initGame();
        font = new Font("TimesRoman", Font.PLAIN, 20);
        board = new Board();
        menu = new Menu();
        store = new Store();
        HighScores = new int[10];
        lastDraw = System.currentTimeMillis();
        store.loadScores();
    }

    private class KeyAction extends AbstractAction {
        public KeyAction(String actionCommand) {
            putValue(ACTION_COMMAND_KEY, actionCommand);
        }

        @
        Override
        public void actionPerformed(ActionEvent actionEvt) {
            String keyCode = actionEvt.getActionCommand();
            System.out.println(GameMode + " - " + GAMEOVER + " - " + menu.menuPosition + " - " + keyCode + " pressed");
            if (!GAMEOVER) {
                if (timePressed == 0)
                    timePressed = System.currentTimeMillis();
                activeGameActions(keyCode);
            } else { //GAMEOVER = true
                passiveGameActions(keyCode);
            }
        }

        /* UBASAK - For J2ME applications I was using keyreleased and keyrepeated events, check if they are necessary.
        @Override
        protected void keyReleased(int keyCode) {
            //System.out.println("Key "+Key);
            //System.out.println("Key released "+getGameAction(keyCode));
            timePressed = 0;
            Key = false;
            KeyMove = 0;
        }

        @Override
        protected void keyRepeated(int keyCode) {
            //int action = getGameAction(keyCode);
            action = keyCode;
            if(!Key){
                Key=true;
                System.out.println("false olmus");
            }
            if(!GAMEOVER){
                //if((System.currentTimeMillis() - timePressed)>=50)
                //  timePressed = System.currentTimeMillis();
                if(GameMode == 0){
                    //switch (action) {
                    switch (keyCode) {
                    case Constants.KEY_UP_ARROW:
                        keyPressed(keyCode);
                    break;
                    default:
                        break;
                    }
                }
            }
        }
        */
    } //TAdapter

    private void activeGameActions(String keyCode) {
        if( GameMode == 0) {
            switch (keyCode) {
                case Constants.KEY_LEFT_ARROW:
                    board.move(-1, matchstone);
                    break;
                case Constants.KEY_RIGHT_ARROW:
                    board.move(1, matchstone);
                    break;
                case Constants.KEY_DOWN_ARROW:
                    board.move(0, matchstone);
                    break;
                case Constants.KEY_UP_ARROW:
                    matchstone.Rotate();
                    break;
                case Constants.KEY_SOFTKEY2:
                    //System.out.println("Oyun durduruldu!");
                    //SaveGame();
                    GameMode = 2;
                    break;
                default:
                    Key = true;
                    KeyMove = 0;
                    break;
            }
        } else if (GameMode == 1) {
            //Game is going on but flashing cause of Letting it down!
            //Need to implement this againg UBASAK
            /*
            drawAll(g);
            if(flashing%2==0){
                for(int i=0; i<10; i++){
                    for(int j=0; j<20; j++){
                        if(TBTest[i][j]==1){
                            g.setClip(0,0,BOARD_WIDTH,BOARD_HEIGHT);
                            g.drawImage(back,i*6+startX,j*6+startY);
                        }               
                    }
                }
            } */
            if (++flashing == 10) {
                GameMode = 0;
                board.LetItDown();
                falling = false;
                board.CheckIsFull();
            }
        } else if (GameMode == 2) {
            switch (keyCode) {
                case Constants.KEY_DOWN_ARROW:
                case Constants.KEY_UP_ARROW:
                    menu.navigate(keyCode);
                    break;
                case Constants.KEY_SOFTKEY1:
                    if (menu.menuPosition == 0) {
                        System.out.println("Continue");
                        GAMEOVER = false;
                        GameMode = 0;
                    } else if (menu.menuPosition == 1) {
                        board.Score();
                        GAMEOVER = true;
                        GameMode = 2;
                        //Continue=0;
                    } else if (menu.menuPosition == 3) {
                        System.exit(0);
                        //we are exiting but there are some flows, check this. UBASAK
                        GameMode = 3;
                        menu.menuPosition = 0;
                    }
            }
            menu.draw(1,true,0);
        } else if(GameMode == 3) {
            switch (keyCode) {
                case Constants.KEY_DOWN_ARROW:
                case Constants.KEY_UP_ARROW:
                    menu.navigate(keyCode);
                    break;
                case Constants.KEY_SOFTKEY1:
                    System.exit(0);
                    break;
            }
            menu.draw(2,true,0);
        }
    }

    private void passiveGameActions(String keyCode) {
        if( GameMode == 0) {
            //g.drawString("GAME OVER",0,60);
            //System.out.println("Case 0");
            if (Constants.KEY_SOFTKEY1 == keyCode) {
                System.out.println("Game Over");
                GameMode = 2;
            }
            /* This code looks like old or new keypress code 
            drawAll(g);
            int xy[][] ={ {startX,startX+6*10,startX+6*10,startX},
                          {128-startY-transCell*6,128-startY-transCell*6,128-startY,128-startY} };
            dg.fillPolygon(xy[0],0,xy[1],0,4,0x88888888);
            //g.drawString("GAME OVER",0,60);
            if(transCell!=20)
                transCell++;
            else{
                g.setColor(0x000000);
                g.drawString("GAME OVER",0,60);
            }
            */
        } else if( GameMode == 1) {
            //SPLASH
            GameMode = 2;
        } else if (GameMode == 2) {
            //Main menu
                switch (keyCode) {
                    case Constants.KEY_DOWN_ARROW:
                    case Constants.KEY_UP_ARROW:
                    case Constants.KEY_LEFT_ARROW:
                    case Constants.KEY_RIGHT_ARROW:
                        menu.navigate(keyCode);
                        break;
                    case Constants.KEY_SOFTKEY1:
                        int menuValue = menu.getValue(Continue, menu.menuPosition);
                        if (menuValue == 5) { //EXIT
                            System.exit(0);
                        } else if (menuValue == 3) { //HELP

                        } else if (menuValue == 0) { //NEW ONE
                            System.out.println("Starts");
                            NewGame();
                        } else if (menuValue == 1) { //CONTINUE
                            matchstone = new MatchStone(); //UBASAK, why do we need to create???
                            store.SaveLoad(false);
                            System.out.println("Loaded");
                            GAMEOVER = false;
                            GameMode = 0;
                            Continue = 0;
                        } else if (menuValue == 2) { //FINISH
                        } else if (menuValue == 4) { //ABOUT
                        }
                        break;
                }
                if(Continue==1){
                    menu.draw(1,false,0);
                }
                else{
                    menu.draw(0,false,0);
                }
            }
    }

    private void myPaint(Graphics g, boolean all) {
        g.setFont(font);
        if (all) paintBoard(g);
        else board.drawAll(g);
        drawSplash(g);
        if(GameMode == 2 || GameMode == 3)
            menu.draw(g);
    }

    private void paintBoard(Graphics g) {
        board.drawAll(g);
        g.setClip(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        if( !GAMEOVER && matchstone != null ) {
            matchstone.DrawShape(g, Board.balls, Constants.CELL_SIZE);
        }
    }

    private void drawSplash(Graphics g) {
        if( GameMode != 2 ) return;
        g.drawImage(Board.splash,0,0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT,null);        
    }

    private void NewGame() {
        GAMEOVER = false;
        GameMode = 0;
        board = new Board();
        lastDraw = System.currentTimeMillis();
        puan = 0;
        matchstone = new MatchStone();
        if (matchstone.CellY != 0) {
            System.out.println("WARNING : cell_y is not ZERO");
        }
        matchstone.CellY = 0;
        transCell = 0;
    }
}
