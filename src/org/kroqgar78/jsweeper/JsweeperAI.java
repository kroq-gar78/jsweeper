package org.kroqgar78.jsweeper;

import java.util.Random;

public class JsweeperAI
{
	public JsweeperAI(Jsweeper inst)
	{
		this.inst = inst;
		
		rand = new Random(System.currentTimeMillis());
	}
	
	public void doStep()
	{
		// check if any cells are clicked at all; else, click a random one
		if(!areAnyCellsClicked())
		{
			inst.getCells()[rand.nextInt(inst.getSize()[0])][rand.nextInt(inst.getSize()[1])].clickCell();
		}
		
	}
	
	public boolean areAnyCellsClicked()
	{
		for( int i = 0; i < inst.getSize()[0]; i++ )
		{
			for( int j = 0; j < inst.getSize()[1]; j++ )
			{
				if( inst.getCells()[i][j].clicked ) return true;
			}
		}
		
		return false;
	}
	
	private Jsweeper inst;
	private Random rand;
	
	public static void main(String[] args)
	{
		Jsweeper inst = new Jsweeper(16, 16, 40);
		JsweeperAI ai = new JsweeperAI(inst);
		ai.doStep();
	}
}
