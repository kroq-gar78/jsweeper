package org.kroqgar78.jsweeper;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Jsweeper
{
	public static class Cell extends JButton
	{
		public static final int MINE = -1;
		
		public Cell(int x, int y, int val)
		{
			super();
			this.x = x;
			this.y = y;
			this.val = this.val;
		}
		public Cell(int x, int y)
		{
			this(x, y, 0);
		}
		
		//public int getX() { return this.x; }
		public void setX(int x) { this.x = x; }
		
		//public int getY() { return this.y; }
		public void setY(int y) { this.y = y; }
		
		public int[] getPosition() { return new int[] {x, y}; }
		public void setPosition(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		public void setPosition(int[] pos) { setPosition(pos[0], pos[1]); }
		
		public int getValue() { return this.val; }
		public void setValue(int val) { this.val = val; }
		public void incrementValue() { this.val++; }
		
		public boolean isMine() { return val == MINE; }
		public void setMine(boolean mine) { this.val = (mine?MINE:0); }
		
		private int x, y;
		private int val;
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Jsweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = frame.getContentPane();
		int[] size = new int[] {8,8}; // dimensions of the field; rows by columns
		contentPane.setLayout(new GridLayout(size[0], size[1]));
		
		ImageIcon mineImage = new ImageIcon("res/mine.png");
		Random rand = new Random(new java.util.GregorianCalendar().getTimeInMillis());
		
		int numMines = 10;
		int currentMines = 0;
		//JButton[][] buttons = new JButton[size[0]][size[1]]; // position is (dist from top, dist from left)
		//boolean[][] mines = new boolean[size[0]][size[1]];
		Cell[][] cells = new Cell[size[0]][size[1]];
		//int[][] numbers = new int[size[0]][size[1]];
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
				cells[i][j] = new Cell(i, j);
				
				contentPane.add(cells[i][j]);
				if(rand.nextBoolean()&&currentMines<numMines)
				{
					System.out.println("Placed mine at (" + i + "," + j + ")");
					currentMines++;
					cells[i][j].setMine(true);
					cells[i][j].setIcon(mineImage);
				}
			}
		}
		for( int i = 0; i < cells.length; i++ )
		{
			for( int j = 0; j < cells[i].length; j++ )
			{
				if(cells[i][j].isMine()) continue;
				
				// check all cells surrounding the one in question (if it's not a mine itself)
				if( i<(size[0]-1) && cells[i+1][j].isMine() ) cells[i][j].incrementValue(); //  below
				if( j<(size[0]-1) && cells[i][j+1].isMine() ) cells[i][j].incrementValue(); // right
				if( i<(size[0]-1) && j<(size[0]-1) && cells[i+1][j+1].isMine() ) cells[i][j].incrementValue(); // below & right
				if( i>0 && cells[i-1][j].isMine() ) cells[i][j].incrementValue(); // above
				if( i>0 && j<(size[0]-1) && cells[i-1][j+1].isMine() ) cells[i][j].incrementValue(); // above & right 
				if( j>0 && cells[i][j-1].isMine() ) cells[i][j].incrementValue(); // left
				if( i>0 && j>0 && cells[i-1][j-1].isMine() ) cells[i][j].incrementValue(); // above & left
				if( i<(size[0]-1) && j>0 && cells[i+1][j-1].isMine() ) cells[i][j].incrementValue(); // down & left
				
				cells[i][j].setText(Integer.toString(cells[i][j].getValue()));
			}
		}
		
		frame.pack();
		frame.setVisible(true);
	}
}
