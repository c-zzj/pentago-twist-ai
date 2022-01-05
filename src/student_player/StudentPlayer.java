package student_player;

import boardgame.Move;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoMove;
import java.util.ArrayList;


/**
 * 
 * @author Christian Zhao, Student ID 260894002
 *
 */
public class StudentPlayer extends PentagoPlayer {
    private ABPruning pruning = null;
    private MCTS monteCarlo = null;
    private Utility util = null;
	
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260894002");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    
    public Move chooseMove(PentagoBoardState boardState) {
        int turnNumber = boardState.getTurnNumber();
        if(turnNumber==0)
        	return firstMove(boardState);
        if(turnNumber==1)
        	return goForMid(boardState);
        
        // use alpha-beta pruning for second player
        if(boardState.getTurnPlayer()==1) return blackPlayer(boardState);
        
        // use mcts for second player
        if(turnNumber<14) return whitePlayer(boardState);
        
        return endGame(boardState); // end game uses alpha-beta pruning
    }
    
    private Move firstMove(PentagoBoardState boardState)
    {
    	// initialize players
    	util = new ImprovedEval();
    	pruning = new ABPruning(util, boardState.getTurnPlayer());
        monteCarlo = new MCTS(1.4,true);
        
        return goForMid(boardState);
    }
    
    /**
     *  simply chooses an empty middle spot of a quadrant
     * @param boardState
     * @return
     */
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
    	else if(topRight==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(1,4,0,0, boardState.getTurnPlayer());
    	}
    	else if(bottomLeft==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(4,1,0,0, boardState.getTurnPlayer());
    	}
    	else if(bottomRight==Piece.EMPTY)
    	{
    		midMove = new PentagoMove(4,4,0,0, boardState.getTurnPlayer());
    	}
    	return midMove;
    }
    
    
    private Move blackPlayer(PentagoBoardState boardState)
    {
    	try {
        	Move myMove = pruning.chooseMove(boardState, 3, 1950);
        	return myMove;
        	}
        catch(Exception e)
        {
        	return boardState.getRandomMove();
        }
    }
    
    private Move whitePlayer(PentagoBoardState boardState)
    {
    	try {
        	Move myMove = monteCarlo.chooseMove(boardState,20);
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
    	Move myMove = pruning.chooseMove(boardState, 5, 1950);
    	return myMove;
    	}
    	catch(Exception e)
    	{
    		return boardState.getRandomMove();
    	}
    }
}