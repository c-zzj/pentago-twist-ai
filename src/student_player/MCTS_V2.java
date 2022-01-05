package student_player;
import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Christian
 *
 * MCTS_V2 is the version that reuses the tree from last move - other than this, is the same as MCTS.java
 */


public class MCTS_V2
{
	double UCTconstant;
	Random rand = new Random(System.nanoTime());
	long maxTime = 1950; // 
	int maxPlays = 10000000; //
	int numPlayed;
	Utility util;
	boolean useUtility = true;
	double utilityWeight = 0.2;
	
	// record the search tree of last move, and the last move chosen
	Tree searchTree = null;
	Node lastNodeChosen = null;
	
	public MCTS_V2(double UCTconstant, boolean useUtil)
	{
		this.UCTconstant = UCTconstant;
		util = new ImprovedEval();
		useUtility = useUtil;
	}
	
	public MCTS_V2(boolean useUtil)
	{
		this.UCTconstant = 1.4;
		util = new ImprovedEval();
		useUtility = useUtil;
	}
	
	public PentagoMove chooseMove(PentagoBoardState state, int playsEachVisit)
	{
		numPlayed = 0;
		if(searchTree == null)
			searchTree = new Tree(state);
		else
		{
			// find the current node in previously built tree
			boolean notFound = true;
			MyBoard cur = new MyBoard(state.getBoard());
			for(Node c : lastNodeChosen.children)
			{
				MyBoard possibleCur = new MyBoard(c.state.getBoard());
				if (cur.equals(possibleCur))
				{
					searchTree.root = c;
					c.parent = null;
					notFound = false;
					break;
				}
			}
			if(notFound) // not likely to happen, but just to check
			{
				searchTree = new Tree(state);
			}
		}
		
		Node root = searchTree.root;
		root.expandWithPartialMerge();
		
		long startTime = System.currentTimeMillis();
		long curTime = System.currentTimeMillis();
		
		for(Node c : root.children)
		{
			if (c.state.getWinner() == state.getTurnPlayer())
				return c.lastMove;
		}
		
		while(curTime - startTime < maxTime && numPlayed <= maxPlays)
		{
			// select stage - go to the leave and expand if possible
			Node leave = searchTree.getLeave();
			
			Node toPlay;
			if(leave.state.getWinner()==Board.NOBODY && leave.numPlays!=0 && leave.definiteWinner == Board.NOBODY)
			{
				leave.expandWithPartialMerge();
				
				ArrayList<Node>children = leave.children;
				toPlay = children.get(rand.nextInt(children.size()));
			}
			else {
				toPlay = leave;
			}
			
			// fast play stage - randomly select a child and play, and update the values
			searchTree.sampleAndUpdate(playsEachVisit, toPlay);
			
			//if(toPlay.state.getWinner()==state.getTurnPlayer())
				//System.out.println(toPlay.parent.numPlays+" plays, "+toPlay.numWins+ " wins");
			
			numPlayed++;
			curTime = System.currentTimeMillis();
		}
		double tmp1 = UCTconstant;
		double tmp2 = utilityWeight;
		if(searchTree.getThisUCT(searchTree.thisSelectChild(searchTree.root)) < 0.55)
			UCTconstant = 0.8;
		else
			UCTconstant = 0;
		utilityWeight = 0;
		Node bestMove = searchTree.thisSelectChild(searchTree.root);
		UCTconstant = tmp1;
		utilityWeight = tmp2;
		
		lastNodeChosen = bestMove;
		
		return bestMove.lastMove;
	}

	

	
	private class Tree
	{
		Node root;
		
		Tree(PentagoBoardState state)
		{
			root = new Node(state, null, null);
		}
		
		Node selectChild(Node cur)
		{
			if(cur.state.getTurnPlayer() != root.state.getTurnPlayer())
				return opponentSelectChild(cur);
			return thisSelectChild(cur);
		}
		
		Node thisSelectChild(Node cur)
		{
			Node selected = null;
			double lowestValue = Double.NEGATIVE_INFINITY;
			for(Node child : cur.children)
			{
				double childsUCT = getThisUCT(child);
				if(childsUCT > lowestValue)
				{
					selected = child;
					lowestValue = childsUCT;
				}
			}
			return selected;
		}
		
		Node opponentSelectChild(Node cur)
		{
			Node selected = null;
			double lowestValue = Double.NEGATIVE_INFINITY;
			for(Node child : cur.children)
			{
				double childsUCT = getOpponentUCT(child);
				if(childsUCT > lowestValue)
				{
					selected = child;
					lowestValue = childsUCT;
				}
			}
			return selected;
		}
		
		
		Node getLeave()
		{
			Node cur = root;
			while(cur.children.size()>0)
			{
				cur = selectChild(cur);
			}
			return cur;
		}
		
		void sampleAndUpdate(int timesToPlay, Node toPlay)
		{
			int wins = 0;
			int rootPlayer = root.state.getTurnPlayer();
			int result = toPlay.state.getWinner();
			PentagoBoardState state = (PentagoBoardState) toPlay.state.clone();
			
			
			// if previous visiting determined a definite winner, then winner gets all immediately
			if (toPlay.definiteWinner != Board.NOBODY)
			{
				if(toPlay.definiteWinner!=rootPlayer)
				{
					update(toPlay,5*timesToPlay, 0);
					return;
				}
				update(toPlay, timesToPlay,timesToPlay);
				return;
			}
			
			
			
			// if already terminated, winner gets all
			if (result!= Board.NOBODY)
			{
				toPlay.definiteWinner = result;
				if(result!=rootPlayer)
				{
					update(toPlay,5*timesToPlay,0);
					return;
				}
				update(toPlay, timesToPlay,timesToPlay);
				return;
			}
			
			
			
			// if first move terminates game, winner gets all
			for(PentagoMove move : state.getAllLegalMoves())
			{
				PentagoBoardState tmp = (PentagoBoardState) toPlay.state.clone();
				tmp.processMove(move);
				result = tmp.getWinner();
				// if first random move terminates game, winner gets all
				if(result == toPlay.state.getTurnPlayer())
				{
					toPlay.definiteWinner = result;
					if(result!=rootPlayer)
					{
						update(toPlay,5*timesToPlay,0);
						return;
					}
					update(toPlay, timesToPlay, timesToPlay);
					return;
				}
			}
			
			// start simulation
			for(int i=0; i<timesToPlay; i++)
			{
				result = toPlay.state.getWinner();
				state = (PentagoBoardState) toPlay.state.clone();
				int j=0;
				while(result == Board.NOBODY)
				{
					state.processMove(MyTools.fastRandomMove(state, rand));
					result = state.getWinner();
					/*
					if(j==0 && result == toPlay.state.getTurnPlayer())
					{
						if(result!=rootPlayer)
						{
							update(toPlay,timesToPlay,0);
							return;
						}
						update(toPlay, timesToPlay, timesToPlay);
						return;
					}
					j=1;*/
				}
				if(result==root.state.getTurnPlayer()) wins++;
			}
			// backPropagate
			update(toPlay, timesToPlay, wins);
		}
		
		void update(Node leave, int numPlays, int wins)
		{
			//int leavePlayer = leave.state.getTurnPlayer();
			//int rootPlayer = root.state.getTurnPlayer();
			
			Node cur = leave;
			while(cur!=null)
			{
				cur.numPlays += numPlays;
				//if(cur.state.getTurnPlayer() != rootPlayer)
					cur.numWins += wins;
				
				cur = cur.parent;
			}
		}
		
		double getThisUCT(Node n)
		{
			//if(n.numPlays==0) return UCTconstant * Math.sqrt( Math.log1p(n.parent.numPlays)/0.01 );
			if(n.numPlays==0) return 1;
			double UCT = ((double) n.numWins / (double)n.numPlays) 
					+ UCTconstant * Math.sqrt( Math.log1p(n.parent.numPlays)/(double)n.numPlays );
			
			if(useUtility) return UCT+n.utility;
			return UCT;
		}
		
		double getOpponentUCT(Node n)
		{
			//if(n.numPlays==0) return UCTconstant * Math.sqrt( Math.log1p(n.parent.numPlays)/0.01 );
			if(n.numPlays==0) return 1;
			double UCT = ((double) (n.numPlays-n.numWins) / (double)n.numPlays) 
					+ UCTconstant * Math.sqrt( Math.log1p(n.parent.numPlays)/(double)n.numPlays );
			
			if(useUtility) return UCT+n.utility;
			return UCT;
		}
	}
	
	
	private class Node
	{
		PentagoBoardState state;
		PentagoMove lastMove;
		Node parent;
		int numPlays = 0;
		int numWins = 0; // numWins are stored with respect to the player at root
		ArrayList<Node> children = new ArrayList();
		double utility;
		int definiteWinner = Board.NOBODY;
		
		Node(PentagoBoardState stateAfter, Node p, PentagoMove lastMove)
		{
			this.parent = p;
			this.state = stateAfter;
			this.lastMove = lastMove;
			// util divided by 100000 to normalize to the range (0,1]
			utility = util.utilityThisNext(stateAfter, 1-stateAfter.getTurnPlayer())*utilityWeight/100000.;
		}
		
		void expandWithCompleteMerge()
		{
			Collection<MoveIntPair> moves = MyTools.mergeNextStatesComplete(state);
			children = new ArrayList<Node>(moves.size());
			
			for(MoveIntPair pair : moves)
			{
				children.add(new Node(pair.stateAfter, this, pair.move));
			}
		}
		
		void expandWithPartialMerge()
		{
			Collection<MoveIntPair> moves = MyTools.mergeNextStatesPartial(state);
			children = new ArrayList<Node>(moves.size());
			
			for(MoveIntPair pair : moves)
			{
				children.add(new Node(pair.stateAfter, this, pair.move));
			}
		}
	}
}

