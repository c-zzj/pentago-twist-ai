package student_player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import boardgame.Board;

import java.util.HashMap;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import java.util.HashSet;
import java.util.Collection;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }
    
    // Helper for getting a random move. Faster than the provided one. Used in MCTS.java
    public static PentagoMove fastRandomMove(PentagoBoardState state, Random rand)
    {
    	int player = state.getTurnPlayer();
    	ArrayList<int[]> emptySpots = new ArrayList();
    	Piece[][] board = state.getBoard();
    	for(int i=0;i<6;i++)
    	{
    		for(int j=0;j<6;j++)
    		{
    			if(board[i][j] == Piece.EMPTY)
    				emptySpots.add(new int[]{i,j});
    		}
    	}
    	int[] spot = emptySpots.get(rand.nextInt(emptySpots.size()));
    	int op = rand.nextInt(8);
    	return new PentagoMove(spot[0],spot[1], op%4, op/4, player);
    }
    
    // only merge moves that result in the same state
    public static Collection<PentagoMove> mergeMovesPartial(PentagoBoardState state, ArrayList<PentagoMove> moves)
    {
    	HashMap<MyBoard, PentagoMove> uniqueMoves = new HashMap(256);
    	for(PentagoMove move : moves)
    	{
    		PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
    		stateAfter.processMove(move);
    		uniqueMoves.put(new MyBoard(stateAfter.getBoard()), move);
    	}
    	return uniqueMoves.values();
    }
    
    public static Collection<MoveIntPair> mergeNextStatesPartial(PentagoBoardState state)
    {
    	ArrayList<PentagoMove> moves = state.getAllLegalMoves();
    	HashMap<MyBoard, MoveIntPair> uniqueMoves = new HashMap(256);
    	for(PentagoMove move : moves)
    	{
    		PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
    		stateAfter.processMove(move);
    		MoveIntPair pair = new MoveIntPair(move, 0, stateAfter);
    		uniqueMoves.put(new MyBoard(stateAfter.getBoard()), pair);
    	}
    	return uniqueMoves.values();
    }
    
    // not only merge moves that result in the same state
    // but also merge moves that result in the states of the same symmetric group
    public static Collection<PentagoMove> mergeMovesComplete(PentagoBoardState state, ArrayList<PentagoMove> moves)
    {
    	//Piece[][] a = state.getBoard();
    	//System.out.println(Arrays.deepToString(a));
    	HashMap<HashSet<MyBoard>, PentagoMove> uniqueMoves = new HashMap();
    	for(PentagoMove move : moves)
    	{
    		PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
    		stateAfter.processMove(move);
    		HashSet<MyBoard> symmetries = getSymmetryGroup(stateAfter);
    		uniqueMoves.put(symmetries, move);
    	}
    	return uniqueMoves.values();
    }
    
    public static Collection<MoveIntPair> mergeNextStatesComplete(PentagoBoardState state)
    {
    	ArrayList<PentagoMove> moves = state.getAllLegalMoves();
    	HashMap<HashSet<MyBoard>, MoveIntPair> uniqueMoves = new HashMap();
    	for(PentagoMove move : moves)
    	{
    		PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
    		stateAfter.processMove(move);
    		HashSet<MyBoard> symmetries = getSymmetryGroup(stateAfter);
    		
    		MoveIntPair pair = new MoveIntPair(move, 0, stateAfter);
    		uniqueMoves.put(symmetries, pair);
    	}
    	return uniqueMoves.values();
    }
    
    private static HashSet<MyBoard> getSymmetryGroup(PentagoBoardState state)
    {
    	Piece[][] board = e(state.getBoard());
    	HashSet<MyBoard> symmetries = new HashSet();
    	Piece[][] verticalFlip = verticalFlip(board);
    	symmetries.add(new MyBoard(board)); // add e
    	symmetries.add(new MyBoard(verticalFlip)); // add vertical flip
    	symmetries.add(new MyBoard(horizontalFlip(board))); // add horizontal flip
    	symmetries.add(new MyBoard(ACFlip(board))); // add AC flip
    	symmetries.add(new MyBoard(BDFlip(board))); // add BD flip
    	symmetries.add(new MyBoard(BDFlip(verticalFlip))); // add 90 degrees rotate
    	symmetries.add(new MyBoard(horizontalFlip(verticalFlip))); // add 180 degrees rotate
    	symmetries.add(new MyBoard(ACFlip(verticalFlip))); // add 270 degrees rotate
    	return symmetries;
    }
    
    //The following are symmetric operations on a square
    private static Piece[][] e(Piece[][] board)
    {
    	Piece[][] afterFlip = new Piece[6][6];
    	for(int i=0; i<6; i++)
    	{
    		for(int j=0; j<6; j++)
    		{
    			afterFlip[i][j] = board[i][j];
    		}
    	}
    	return afterFlip;
    }
    private static Piece[][] horizontalFlip(Piece[][] board)
    {
    	Piece[][] afterFlip = new Piece[6][6];
    	for(int i=0; i<6; i++)
    	{
    		for(int j=0; j<6; j++)
    		{
    			afterFlip[i][j] = board[i][5-j];
    		}
    	}
    	return afterFlip;
    }
    private static Piece[][] verticalFlip(Piece[][] board)
    {
    	Piece[][] afterFlip = new Piece[6][6];
    	for(int i=0; i<6; i++)
    	{
    		for(int j=0; j<6; j++)
    		{
    			afterFlip[i][j] = board[5-i][j];
    		}
    	}
    	return afterFlip;
    }
    private static Piece[][] ACFlip(Piece[][] board) // flip around bottom left and top right diagonal
    {
    	Piece[][] afterFlip = new Piece[6][6];
    	for(int i=0; i<6; i++)
    	{
    		for(int j=0; j<6; j++)
    		{
    			afterFlip[i][j] = board[5-j][5-i];
    		}
    	}
    	return afterFlip;
    }
    private static Piece[][] BDFlip(Piece[][] board) // flip around top left and bottom right diagonal
    {
    	Piece[][] afterFlip = new Piece[6][6];
    	for(int i=0; i<6; i++)
    	{
    		for(int j=0; j<6; j++)
    		{
    			afterFlip[i][j] = board[j][i];
    		}
    	}
    	return afterFlip;
    }
    
}

//wrapper class of Piece[][] for hashing
class MyBoard
{
	Piece[][] board;
	MyBoard(Piece[][] board){this.board = board;}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MyBoard b = (MyBoard) o;
		return Arrays.deepEquals(b.board, this.board);
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.deepHashCode(this.board);
		//return Arrays.deepToString(this.board).hashCode();
	}
}

//a class that is a pair of move and an integer associated with that move
class MoveIntPair implements Comparable<MoveIntPair>
{
	PentagoMove move;
	int num;
	PentagoBoardState stateAfter;
	private static Random rand = new Random(System.nanoTime());
	
	MoveIntPair(PentagoMove move, int num, PentagoBoardState stateAfter)
	{
		this.move = move; this.num = num; this.stateAfter = stateAfter;
	}
	
	public static MoveIntPair[] kBestMoves(int k, MoveIntPair[] pairs, int maxNumNotBest)
	{
		PriorityQueue<MoveIntPair> queuedPairs = new PriorityQueue(pairs.length);
		ArrayList<MoveIntPair> remainder = new ArrayList();
		for (MoveIntPair pair: pairs)
		{
			queuedPairs.add(pair);
			remainder.add(pair);
		}
		
		MoveIntPair[] result = new MoveIntPair[k];
		for(int i=0; i<k-maxNumNotBest; i++)
		{
			result[i] = queuedPairs.poll();
			remainder.remove(result[i]);
		}
		for(int i=k-maxNumNotBest; i<k; i++)
		{
			int index = rand.nextInt(remainder.size());
			result[i] = remainder.get(index);
			remainder.remove(index);
		}
		return result;
	}
		
	@Override
	public int compareTo(MoveIntPair o)
	{
		// reversed value: turn ascending order to descending order when sorting
		if (o.num < num) return -1;
		if (o.num > num) return 1;
		return 0;
	}
}