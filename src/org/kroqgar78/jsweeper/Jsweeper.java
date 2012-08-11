package org.kroqgar78.jsweeper;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Jsweeper
{
	public static ImageIcon mineImage = new ImageIcon("res/mine.png");
	
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
			if(val == MINE)
			{
				super.setText("");
				super.setIcon(mineImage);
			}
			else if(val == EMPTY)
			{
				super.setText("");
				Cell[] adjCells = inst.getAdjacentCells(x, y);
				for( int i = 0; i < adjCells.length; i++ )
				{
					if( adjCells[i].getValue() >= 0 && adjCells[i].isEnabled() ) adjCells[i].clickCell();
				}
			}
			else super.setText(Integer.toString(this.val));
		}
		
		private int x, y;
		private int val;

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			System.out.println("Button pressed at (" + getPosition()[0] + "," + getPosition()[1] + ")" );
			if(arg0.getButton() == MouseEvent.BUTTON1) clickCell();
			if(arg0.getButton() == MouseEvent.BUTTON3);
		}
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
		
		Container contentPane = frame.getContentPane();
		size = new int[] {height,width}; // dimensions of the field; rows by columns
		contentPane.setLayout(new GridLayout(size[0], size[1]));
		
		rand = new Random(System.currentTimeMillis());
		
		this.numMines = numMines;
		cells = new Cell[size[0]][size[1]]; // position is (dist from top, dist from left)
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
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
		
		this.recountCells();
		
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
	
	public static void main(String[] args)
	{
		inst = new Jsweeper(16, 16, 40);
	}
}
