package student_player;

import boardgame.Move;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoBoardState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 *  Implementation of standard alpha-beta pruning, with transposition table memorizing visited states
 *  
 * @author Christian
 *
 */

public class ABPruning
{
	private int maxTurn;
	private Utility util;
	private int maxPlayer;
	private int minPlayer;
	private HashMap<MyBoard, Integer> transpositionTable;
	long startTime=0;
	long timeLimit=1950;
	
	public ABPruning(Utility util, int player)
	{
		this.util = util;
		this.maxPlayer = player;
		this.minPlayer = 1 - player;
		this.transpositionTable = new HashMap(10000);
	}
	
	public PentagoMove chooseMove(PentagoBoardState state, int maxSteps, long timeLimit)
	{
		startTime = System.currentTimeMillis();
		this.timeLimit = timeLimit;
		if(state.getTurnPlayer()==maxPlayer)
			return chooseMaxMove(state,maxSteps);
		else return chooseMinMove(state,maxSteps);
	}
	
	PentagoMove chooseMaxMove(PentagoBoardState state, int maxSteps)
	{
		this.maxTurn = maxSteps;
		ArrayList<PentagoMove> legalMoves = state.getAllLegalMoves();
		Collection<PentagoMove> moves = MyTools.mergeMovesComplete(state, legalMoves);
		int v = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		PentagoMove bestMove = null;
		for (PentagoMove move : moves)
		{
			PentagoBoardState stateAfterMove = (PentagoBoardState) state.clone();
			stateAfterMove.processMove(move);
			int tmp = minValue(stateAfterMove, alpha, beta, 1);
			if (tmp > v)
			{
				v = tmp;
				bestMove = move;
			}
			if (v >= beta) return move;
			if (v > alpha) alpha = v;
		}
		return bestMove;
	}
	
	PentagoMove chooseMinMove(PentagoBoardState state, int maxSteps)
	{
		this.maxTurn = maxSteps;
		ArrayList<PentagoMove> legalMoves = state.getAllLegalMoves();
		Collection<PentagoMove> moves = MyTools.mergeMovesComplete(state, legalMoves);
		int v = Integer.MAX_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		PentagoMove bestMove = null;
		for (PentagoMove move : moves)
		{
			PentagoBoardState stateAfterMove = (PentagoBoardState) state.clone();
			stateAfterMove.processMove(move);
			int tmp = minValue(stateAfterMove, alpha, beta, 1);
			if (tmp < v)
			{
				v = tmp;
				bestMove = move;
			}
			if (v <= alpha) return move;
			if (v < beta) beta = v;
		}
		return bestMove;
	}
	
	private int maxValue(PentagoBoardState state, int alpha, int beta, int curTurn)
	{
		if (state.gameOver() || curTurn==maxTurn || System.currentTimeMillis()-startTime > timeLimit) 
			return util.utilityOpponentNext(state, maxPlayer);
		
		
		int v = Integer.MIN_VALUE;;
		ArrayList<PentagoMove> legalMoves = state.getAllLegalMoves();
		Collection<PentagoMove> moves = MyTools.mergeMovesPartial(state, legalMoves);
		
		int numMoves = moves.size();
		MoveIntPair[] moveIntPairs = new MoveIntPair[numMoves];
		int i=0;
		for(PentagoMove curMove : moves)
		{
			PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
			stateAfter.processMove(curMove);
			
			// associate each move with the utility of the state after applying it
			
			moveIntPairs[i] = new MoveIntPair(curMove, util.utilityOpponentNext(stateAfter, maxPlayer), stateAfter);
			i++;
		}
		
		Arrays.sort(moveIntPairs); // sort according to utility, hope will reach best move earlier
		
		for(i=0;i<moveIntPairs.length;i++)
		{
			PentagoBoardState b = moveIntPairs[i].stateAfter;
			Integer tmp = transpositionTable.get(new MyBoard(b.getBoard()));
			if(tmp==null)
			{
				tmp = minValue(b, alpha, beta, curTurn + 1);
				transpositionTable.put(new MyBoard(b.getBoard()), tmp);
			}
			if (tmp > v) v = tmp;
			if (v >= beta) return v;
			if (v > alpha) alpha = v;
		}
		return v;
	}
	
	private int minValue(PentagoBoardState state, int alpha, int beta, int curTurn)
	{
		if (state.gameOver() || curTurn==maxTurn || System.currentTimeMillis()-startTime > timeLimit) 
			return util.utilityThisNext(state, maxPlayer);
		
		int v = Integer.MAX_VALUE;;
		ArrayList<PentagoMove> legalMoves = state.getAllLegalMoves();
		Collection<PentagoMove> moves = MyTools.mergeMovesPartial(state, legalMoves);
		
		int numMoves = moves.size();
		MoveIntPair[] moveIntPairs = new MoveIntPair[numMoves];
		int i=0;
		for(PentagoMove curMove : moves)
		{
			PentagoBoardState stateAfter = (PentagoBoardState) state.clone();
			stateAfter.processMove(curMove);
			
			// associate each move with the utility of the state after applying it
			moveIntPairs[i] = new MoveIntPair(curMove, util.utilityOpponentNext(stateAfter, maxPlayer),stateAfter);
			i++;
		}
		Arrays.sort(moveIntPairs); // sort according to utility, hope will reach best move earlier
		
		for(i=moveIntPairs.length-1;i>=0;i--)
		{
			PentagoBoardState b = moveIntPairs[i].stateAfter;
			Integer tmp = transpositionTable.get(new MyBoard(b.getBoard()));
			if(tmp==null)
			{
				tmp = maxValue(b, alpha, beta, curTurn + 1);
				transpositionTable.put(new MyBoard(b.getBoard()), tmp);
			}
			if (tmp < v) v = tmp;
			if (v <= alpha) return v;
			if (v < beta) beta = v;
		}
		return v;
	}
}
