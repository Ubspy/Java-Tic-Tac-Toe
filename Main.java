import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main extends JFrame 
{
	private static final long serialVersionUID = 1L; //No clue why this is here

	public Main() throws IOException //Constructor, opens window
	{
		super("TicExpoToe"); //Sets window name
		setSize(825, 825); //Sets size
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setVisible(true); 
		
		final Game game = new Game(this); //Initializes panel
		setContentPane(game);
		repaint(); //Calls paintComponent
	}
	
	public static void main(String args[]) throws IOException
	{
		new Main(); //Creates window
	}
}

class Game extends JPanel
{
	private static final long serialVersionUID = 1L; //No clue why this is here
	
	private int[][] _board = new int[3][3]; //Board matrix
	private int _gameState, _isWin = 0; 
	private boolean _playerOne = false, _prevPlayerOne = false, _playing = false, leftCorner = false;
	
	Point b = null; PointerInfo p; //This is to get the mouse location
	
	static Image xImg, oImg;
	
	public Game(final JFrame frame) throws IOException
	{
		setSize(825, 825); 
		setLayout(null);
		setDoubleBuffered(false);
		
		xImg = ImageIO.read(new File("x.png"));
		oImg = ImageIO.read(new File("o.png"));
		
		for(int x = 0; x < 3; x++)
		{	for(int y = 0; y < 3; y++)
			{
				_board[x][y] = 0;
			}
		}
		
		frame.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) { _playerOne = !_playerOne; _playing = true; }
			@Override
			public void mousePressed(MouseEvent arg0) {  }
			@Override
			public void mouseExited(MouseEvent arg0) {  }
			@Override
			public void mouseEntered(MouseEvent arg0) {  }
			@Override
			public void mouseClicked(MouseEvent arg0) {  }
			//Triggers the if statement in the thread
		});
		
		askMultiplayer(frame); //Opens dialogue box for multiplayer
		
		Thread t = new Thread(new Runnable() { //Will run forever without crashing the program
			@Override
			public void run()
			{
			while(true)
			{
				p = MouseInfo.getPointerInfo(); b = p.getLocation(); //Gets mouse location
				b.x -= frame.getLocationOnScreen().x;
				b.y -= frame.getLocationOnScreen().y;

				if(_playerOne != _prevPlayerOne) //Triggered when clicked
				{
					int tmp;
					
					//Sees which player is active
					if(_playerOne == true) { tmp = 1; } 
					else if(_gameState == 1) { tmp = 2; }
					else { tmp = 0; }
					
					//First row
					if(b.x > 100 && b.x < 300)
					{
						if(b.y > 100 && b.y < 300) { if(_board[0][0] == 0) { _board[0][0] = tmp; _prevPlayerOne = _playerOne; //First column
						if(tmp == 1) { leftCorner = true; } else if(tmp == 2) { leftCorner = false; } } } //Fixes AI bug
						else if(b.y > 300 && b.y < 500) { if(_board[0][1] == 0) { _board[0][1] = tmp; _prevPlayerOne = _playerOne; } } //Second column
						else if(b.y > 500 && b.y < 700) { if(_board[0][2] == 0) { _board[0][2] = tmp; _prevPlayerOne = _playerOne; } } //Third column
					}
					
					//Second row
					else if(b.x > 300 && b.x < 500)
					{
						if(b.y > 100 && b.y < 300) { if(_board[1][0] == 0) { _board[1][0] = tmp; _prevPlayerOne = _playerOne; } } //First column
						else if(b.y > 300 && b.y < 500) { if(_board[1][1] == 0) { _board[1][1] = tmp; _prevPlayerOne = _playerOne; } } //Second column
						else if(b.y > 500 && b.y < 700) { if(_board[1][2] == 0) { _board[1][2] = tmp; _prevPlayerOne = _playerOne; } } //Thirs column
					}
					
					//Third row
					else if(b.x > 500 && b.x < 700)
					{
						if(b.y > 100 && b.y < 300) { if(_board[2][0] == 0) { _board[2][0] = tmp; _prevPlayerOne = _playerOne; } } //First column
						else if(b.y > 300 && b.y < 500) { if(_board[2][1] == 0) { _board[2][1] = tmp; _prevPlayerOne = _playerOne; } } //Second columns
						else if(b.y > 500 && b.y < 700) { if(_board[2][2] == 0) { _board[2][2] = tmp; _prevPlayerOne = _playerOne; } } //Third columns
					}
				}
				
				if(_playerOne && _playing && _gameState == 0) //If it's the AIs' turn
				{
					_playerOne = false; //Sets it to the players turn
					makeMove(); //Takes the AIs' turn
				}
				
				_isWin = checkVictory(); //Checks victory and ends the game if so
				if(_isWin == 1) { endGame(_isWin, frame); break; }
				else if(_isWin == 2) { endGame(_isWin, frame); break; }
				else if(_isWin == 3) { tieGame(frame); break; }
				
				repaint(); //Puts the board and icons on
			}
			}
		}); t.start(); //Starts the thread
	}
	
	void initBoard(Graphics g)
	{
		//Vertical lines
		g.drawLine(100, 100, 100, 700);
		g.drawLine(300, 100, 300, 700);
		g.drawLine(500, 100, 500, 700);
		g.drawLine(700, 100, 700, 700);
		
		//Horizonal lines
		g.drawLine(100, 100, 700, 100);
		g.drawLine(100, 300, 700, 300);
		g.drawLine(100, 500, 700, 500);
		g.drawLine(100, 700, 700, 700);	
	}
	
	public void askMultiplayer(JFrame frame)
    {
   	Object[] options =
   		{
   			"Single-Player",
   			"Multi-player",
   		};
   	int n = JOptionPane.showOptionDialog(frame,
   			"Do you have any friends?",
   			"Do you wanna get rekt?",
   			JOptionPane.YES_NO_OPTION,
   			JOptionPane.QUESTION_MESSAGE,
   			null, options, options[1]);
   	 
   	_gameState = n;
    }
	
	void showBoard(Graphics g)
	{		
		for(int x = 0; x < 3; x++)
		{	for(int y = 0; y < 3; y++)
			{
				if(_board[x][y] == 1)
				{
					g.drawImage(xImg, (x*200+100), (y*200+100), null);
				}
				else if(_board[x][y] == 2)
				{
					g.drawImage(oImg, (x*200+100), (y*200+100), null);
				}
			}
		}
	}
	
	int checkVictory()
	{
		if(leftCorner)
		{
			_board[0][0] = 1;
		}
		
		//Checks horizontal
		for(int x = 0; x < 3; x++)
		{
			if(_board[x][0] == _board[x][1]) { if(_board[x][1] == _board[x][2]) { if(_board[x][2] == 1) { return 1; } } }
			if(_board[x][0] == _board[x][1]) { if(_board[x][1] == _board[x][2]) { if(_board[x][2] == 2) { return 2; } } }
		}
		
		//Checks vertical
		for(int y = 0; y < 3; y++)
		{
			if(_board[0][y] == _board[1][y]) { if(_board[1][y] == _board[2][y]) { if(_board[2][y] == 1) { return 1; } } }
			if(_board[0][y] == _board[1][y]) { if(_board[1][y] == _board[2][y]) { if(_board[2][y] == 2) { return 2; } } }
		}
		
		//Checks diagonal
		if(_board[0][0] == _board[1][1]) { if(_board[1][1] == _board[2][2]){ if(_board[2][2] == 1) { return 1; } } }
		if(_board[0][0] == _board[1][1]) { if(_board[1][1] == _board[2][2]){ if(_board[2][2] == 2) { return 2; } } }
		
		//Checks diagonal in the other direction
		if(_board[0][2] == _board[1][1]) { if(_board[1][1] == _board[2][0]){ if(_board[2][0] == 1) { return 1; } } }
		if(_board[0][2] == _board[1][1]) { if(_board[1][1] == _board[2][0]){ if(_board[2][0] == 2) { return 2; } } }
		
		int tmp = 0;
		
		//Checks tie
		for(int x = 0; x < 3; x++)
		{	
			for(int y = 0; y < 3; y++)
			{
				if(_board[x][y] != 0) { tmp++; }
			}
		}
		
		if(tmp == 9) { return 3; }
		return 0;
	}
	
	void endGame(int playerWin, JFrame frame)
	{
		repaint();
		int tmp = playerWin;
		Object[] options = { "Close game" };
	  	JOptionPane.showOptionDialog(frame,
			"Player " + tmp + " wins!",
			"GAME OVER!",
			JOptionPane.OK_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, options, options[0]);
	  	System.exit(0);
	}
	
	void tieGame(JFrame frame)
	{
		repaint();
		Object[] options = { "Close game" };
	  	JOptionPane.showOptionDialog(frame,
			"Tie game!",
			"GAME OVER!",
			JOptionPane.OK_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, options, options[0]);
	  	System.exit(0);
	}
	
	void makeMove()
	{
		try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); } //Waits 1/5 of a second	
		
		Move bestMove = getBestMove(2); //Gets best move
		_board[bestMove.x][bestMove.y] = 2; //Places AI player
	}
	
	Move getBestMove(int player)
	{
		//Check to see if there's already a victory state
		int vic = checkVictory();
		
		if(vic == 1) { return new Move(-10); }
		else if(vic == 2) { return new Move(10); }
		else if(vic == 3) { return new Move(0); }
		
		ArrayList<Move> moves = new ArrayList<>(); //For different combination of moves
		
		//Finds all possible moves
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				if(_board[x][y] == 0)
				{
					//Sets values for Move class
					Move move = new Move(); 
					move.x = x;
					move.y = y;
					
					//Calls it self to get the next moves best score, depending on active sensing player
					_board[x][y] = player;
					if(player == 2) { move.score = getBestMove(1).score; }
					else if(player == 1) { move.score = getBestMove(2).score; }
					moves.add(move);
					_board[x][y] = 0;
				}
			}
		}
		
		//Finds best move
		int bestMove = 0;
		if(player == 2)
		{
			int bestScore = -10000000;
			for(int i = 0; i < moves.size(); i++) //Will go through all the moves
			{
				if(moves.get(i).score > bestScore) //Sees if this set of moves is the best one
				{
					bestMove = i;
					bestScore = moves.get(i).score; //Sets best score
				}
			}
		}
		else if(player == 1)
		{
			int bestScore = 10000000;
			for(int i = 0; i < moves.size(); i++)
			{
				if(moves.get(i).score < bestScore)
				{
					bestMove = i;
					bestScore = moves.get(i).score;
				}
			}
		}
		
		return moves.get(bestMove);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		initBoard(g);
		showBoard(g);
	}
}

class Move
{
	public Move() {}
	public Move(int Score) { score = Score; }
	int x;
	int y;
	int score;
}
