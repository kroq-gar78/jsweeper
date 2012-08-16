package org.kroqgar78.jsweeper;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Jsweeper
{
	public static ImageIcon mineImage = new ImageIcon("res/mine.png");
	public static ImageIcon flagImage = new ImageIcon("res/flag.png");
	
	public static class Cell extends JButton implements MouseListener
	{
		public static final int EMPTY = 0;
		public static final int MINE = -1;
		
		public Cell(int x, int y, int val)
		{
			super();
			this.x = x;
			this.y = y;
			this.val = val;
			
			super.addMouseListener(this);
		}
		public Cell(int x, int y)
		{
			this(x, y, EMPTY);
		}
		
		//public int getX() { return this.x; }
		//public void setX(int x) { this.x = x; }
		
		//public int getY() { return this.y; }
		//public void setY(int y) { this.y = y; }
		
		public int[] getPosition() { return new int[] {x, y}; }
		/*public void setPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		public void setPosition(int[] pos) { setPosition(pos[0], pos[1]); }*/
		
		public int getValue() { return this.val; }
		public void setValue(int val)
		{
			this.val = val;
			super.setText("");
			super.setIcon(null);
		}
		public void incrementValue()
		{
			if(isMine()) return; // don't do anything if it's a mine
			this.val++;
			//super.setText(Integer.toString(this.val));
		}
		
		public boolean isMine() { return val == MINE; }
		public void setMine(boolean mine) { setValue(mine?MINE:EMPTY); }
		
		public void clickCell()
		{
			setEnabled(false);
			super.setIcon(null);
			flagged = false;
			
			if(!clicked)
			{
				clicked = true;
				if(val == MINE)
				{
					super.setText("");
					super.setIcon(mineImage);
					Jsweeper.inst.gameOver();
				}
				else if(val == EMPTY)
				{
					super.setText("");
					Cell[] adjCells = inst.getAdjacentCells(x, y);
					for( int i = 0; i < adjCells.length; i++ )
					{
						if( adjCells[i].getValue() >= 0 && !adjCells[i].clicked ) adjCells[i].clickCell();
					}
				}
				else super.setText(Integer.toString(this.val));
				inst.update();
			}
		}
		
		public void flagCell()
		{
			clicked = false;
			flagged = true;
			super.setText("");
			super.setIcon(flagImage);
		}
		
		public void unflagCell()
		{
			clicked = false;
			flagged = false;
			super.setText("");
			super.setIcon(null);
		}
		
		public void toggleFlag()
		{
			if(!flagged) flagCell();
			else unflagCell();
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {}
		
		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			containsMouse = true;
			//getModel().setArmed(pressed);
			//getModel().setPressed(pressed);
		}
		@Override
		public void mouseExited(MouseEvent arg0)
		{
			containsMouse = false;
			//getModel().setArmed(true);
			//getModel().setPressed(false);
		}
		@Override
		public void mousePressed(MouseEvent arg0)
		{
			pressed = true;
			if(clicked) // highlight other adjacent cells if already clicked
			{
				Cell[] adjCells = inst.getAdjacentCells(x, y);
				for( int i = 0; i < adjCells.length; i++ )
				{
					if(adjCells[i].flagged) continue;
					adjCells[i].getModel().setArmed(true);
					adjCells[i].getModel().setPressed(true);
				}
			}
			//getModel().setArmed(pressed);
			//getModel().setPressed(pressed);
		}
		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			pressed = false;
			//getModel().setArmed(pressed);
			//getModel().setPressed(pressed);
			if(clicked) // highlight other adjacent cells if already clicked
			{
				Cell[] adjCells = inst.getAdjacentCells(x, y);
				for( int i = 0; i < adjCells.length; i++ )
				{
					adjCells[i].getModel().setArmed(false);
					adjCells[i].getModel().setPressed(false);
				}
				if(containsMouse)
				{
					int adjFlagCount = 0;
					for( int i = 0; i < adjCells.length; i++ )
					{
						if( adjCells[i].flagged ) adjFlagCount++;
					}
					if(adjFlagCount == this.val)
					{
						for( int i = 0; i < adjCells.length; i++ )
						{
							if(adjCells[i].flagged) continue;
							adjCells[i].clickCell();
						}
					}
				}
			}
			else
			{
				if(!containsMouse) return;
				System.out.println(clicked);
				System.out.println("Button pressed at (" + x + "," + y + ")" );
				if(arg0.getButton() == MouseEvent.BUTTON1 && !flagged) clickCell();
				if(arg0.getButton() == MouseEvent.BUTTON3 && !clicked) toggleFlag();
				System.out.println(clicked);
			}
		}
		
		boolean containsMouse, clicked, flagged, pressed = false;
		private int x, y;
		private int val;
	}
	
	static Jsweeper inst;
	
	private JFrame frame;
	private int[] size;
	private Cell[][] cells;
	private Random rand;
	private int numMines;
	
	public Jsweeper( int width, int height, int numMines )
	{
		frame = new JFrame("Jsweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		size = new int[] {height,width}; // dimensions of the field; rows by columns
		
		rand = new Random(System.currentTimeMillis());
		
		this.numMines = numMines;
		
		generateField();
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("JSweeper");
		JMenuItem startItem = new JMenuItem("Start over");
		startItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				restartGame();
			}
		});
		
		menu.add(startItem);
		menubar.add(menu);
		
		frame.setJMenuBar(menubar);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private void recountCells()
	{
		// calculate values of the cells by iterating through mines and incrementing values around them
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
				if(!cells[i][j].isMine()) continue;

				Cell[] adjCells = getAdjacentCells(i, j);
				for( int k = 0; k < adjCells.length; k++ )
				{
					cells[adjCells[k].getPosition()[0]][adjCells[k].getPosition()[1]].incrementValue();
				}
			}
		}
	}
	
	public Cell[][] getCells() { return this.cells; }
	
	public Cell[] getAdjacentCells(int x, int y)
	{
		ArrayList<Cell> cellTmp = new ArrayList<Cell>();
		
		if( x<(size[0]-1) ) cellTmp.add(cells[x+1][y]); //  below
		if( y<(size[0]-1) ) cellTmp.add(cells[x][y+1]); // right
		if( x<(size[0]-1) && y<(size[0]-1) ) cellTmp.add(cells[x+1][y+1]); // below & right
		if( x>0 ) cellTmp.add(cells[x-1][y]); // above
		if( x>0 && y<(size[0]-1) ) cellTmp.add(cells[x-1][y+1]); // above & right 
		if( y>0 ) cellTmp.add(cells[x][y-1]); // left
		if( x>0 && y>0 ) cellTmp.add(cells[x-1][y-1]); // above & left
		if( x<(size[0]-1) && y>0 ) cellTmp.add(cells[x+1][y-1]); // down & left
		
		return cellTmp.toArray(new Cell[cellTmp.size()]);
	}
	
	public void generateField()
	{
		Container contentPane = frame.getContentPane();
		contentPane.removeAll(); // wipe the content pane just in case it already had stuff
		contentPane.setLayout(new GridLayout(size[0], size[1]));
		cells = new Cell[size[0]][size[1]]; // position is (dist from top, dist from left)
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
				//if(cells[i][j] != null) contentPane.remove(cells[i][j]); // remove old buttons if regenerating
				cells[i][j] = new Cell(i, j);
				cells[i][j].setPreferredSize(new Dimension(42, 32));
				contentPane.add(cells[i][j]);
			}
		}
		
		// place mines
		for( int i = 0; i < numMines; i++ )
		{
			// keep generating random positions until it finds one that isn't a mine
			int x, y;
			do
			{
				x = rand.nextInt(cells.length);
				y = rand.nextInt(cells[0].length);
			}
			while(cells[x][y].isMine());
			
			cells[x][y].setMine(true);
			System.out.println("Placed mine at (" + x + "," + y + ")");
		}
		
		recountCells();
	}
	
	public void restartGame()
	{
		generateField();
		frame.pack();
	}
	
	public void gameOver()
	{
		//JOptionPane.showMessageDialog(frame, "", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
		int userOption = JOptionPane.showConfirmDialog(frame, "Game Over!!!\nWould you like to play again?\n... you suck btw");
		if(userOption == JOptionPane.YES_OPTION)
		{
			restartGame();
		}
		else if(userOption == JOptionPane.NO_OPTION)
		{
			this.frame.setVisible(false);
			System.exit(0);
		}
	}
	
	public void gameWin()
	{
		int userOption = JOptionPane.showConfirmDialog(frame, "Good job. You won.\n... you still suck btw");
		if(userOption == JOptionPane.YES_OPTION)
		{
			restartGame();
		}
		else if(userOption == JOptionPane.NO_OPTION)
		{
			this.frame.setVisible(false);
			System.exit(0);
		}
	}
	
	public void update()
	{
		// victory conditions: all non-mine cells are clicked
		// iterate through all non-mine cells to see if the conditions are true
		boolean allClicked = true;
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
				
				if( !cells[i][j].clicked && !cells[i][j].isMine() )
				{
					allClicked = false;
					break;
				}
			}
		}
		if(allClicked)
		{
			gameWin();
		}
	}
	
	public static void main(String[] args)
	{
		inst = new Jsweeper(16, 16, 40);
	}
}
