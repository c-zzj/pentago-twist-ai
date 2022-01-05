package student_player;

import java.util.ArrayList;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

/**
 * A player class used to test against StudentPlayer.
 * @author Christian
 *
 */

public class AdversaryPlayer extends PentagoPlayer
{
	private ABPruning abPruning = null;
    private MCTS mcts;
    
	public AdversaryPlayer() {
        super("Adversary");
    }
	

	public Move chooseMove(PentagoBoardState boardState) {
		try {
        int turnNumber = boardState.getTurnNumber();
        if(turnNumber==0)
        	return firstMove(boardState);
        if(turnNumber==1)
        	return firstMove(boardState);
        else if(turnNumber<14) return midGame(boardState);
        return endGame(boardState);
		}
		catch(Exception e) {
			e.printStackTrace();
			return boardState.getRandomMove();
		}
    }
    
    private Move firstMove(PentagoBoardState boardState)
    {
    	abPruning = new ABPruning(new ImprovedEval(), boardState.getTurnPlayer());
    	mcts = new MCTS(1.4, true);
        return goForMid(boardState);
    }
    
    private Move goForMid(PentagoBoardState boardState)
    {
    	ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
    	Move midMove = moves.get(0);
    	Piece[][] board = boardState.getBoard();
    	Piece topLeft = board[1][1];
    	Piece topRight = board[1][4];
    	Piece bottomLeft = board[4][1];
    	Piece bottomRight = board[4][4];
    	if(topLeft==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(1,1,0,0, boardState.getTurnPlayer());
    	}
    	else if(bottomLeft==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(4,1,0,0, boardState.getTurnPlayer());
    	}
    	else if(topRight==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(1,4,0,0, boardState.getTurnPlayer());
    	}
    	else if(bottomRight==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(4,4,0,0, boardState.getTurnPlayer());
    	}
    	return midMove;
    }
    
    private Move midGame(PentagoBoardState boardState)
    {
    	try {
    	long a = System.nanoTime();
    	Move myMove;
    	int turn = boardState.getTurnNumber();
    	myMove = abPruning.chooseMove(boardState, 3, 1950);
    	//myMove = mcts.chooseMove(boardState, 20);
    	//myMove = forwardPruning.forwardPruningSearch(boardState, 5);
    	//a = System.nanoTime() - a;
    	//System.out.println((a/1000000) + " ms");
    	if(myMove==null) return boardState.getRandomMove();
    	return myMove;
    	}
    	catch(Exception e)
    	{
    		return boardState.getRandomMove();
    	}
    }
    
    private Move endGame(PentagoBoardState boardState)
    {
    	try {
        	Move myMove = abPruning.chooseMove(boardState, 5, 1950);
        	return myMove;}
        	catch(Exception e)
        	{
        		return boardState.getRandomMove();
        	}
    }
}
