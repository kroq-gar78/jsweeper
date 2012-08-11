package org.kroqgar78.jsweeper;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Jsweeper
{
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
		JButton[][] buttons = new JButton[size[0]][size[1]]; // position is (dist from top, dist from left)
		boolean[][] mines = new boolean[size[0]][size[1]];
		int[][] numbers = new int[size[0]][size[1]];
		for( int i = 0; i < buttons.length; i++ )
		{
			for( int j = 0; j < buttons[i].length; j++ )
			{
				buttons[i][j] = new JButton();
				contentPane.add(buttons[i][j]);
				mines[i][j] = false;
				if(rand.nextBoolean()&&currentMines<numMines)
				{
					System.out.println("Placed mine at (" + i + "," + j + ")");
					currentMines++;
					mines[i][j]=true;
					numbers[i][j]=-1;
					buttons[i][j].setIcon(mineImage);
				}
			}
		}
		for( int i = 0; i < numbers.length; i++ )
		{
			for( int j = 0; j < numbers[i].length; j++ )
			{
				if(mines[i][j]) continue;
				
				// check all cells surrounding the one in question (if it's not a mine itself)
				if( i<(size[0]-1) && mines[i+1][j] ) numbers[i][j]++; //  below
				if( j<(size[0]-1) && mines[i][j+1]) numbers[i][j]++; // right
				if( i<(size[0]-1) && j<(size[0]-1) && mines[i+1][j+1]) numbers[i][j]++; // below & right
				if( i>0 && mines[i-1][j]) numbers[i][j]++; // above
				if( i>0 && j<(size[0]-1) && mines[i-1][j+1]) numbers[i][j]++; // above & right 
				if( j>0 && mines[i][j-1]) numbers[i][j]++; // left
				if( i>0 && j>0 && mines[i-1][j-1]) numbers[i][j]++; // above & left
				if( i<(size[0]-1) && j>0 && mines[i+1][j-1]) numbers[i][j]++; // down & left
				
				buttons[i][j].setText(Integer.toString(numbers[i][j]));
			}
		}
		
		frame.pack();
		frame.setVisible(true);
	}
}
