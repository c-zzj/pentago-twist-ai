package student_player;

import boardgame.Board;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

class ImprovedEval implements Utility
{
	private int onePieceSide = 100,
	onePieceMid = 300,
	twoPiecesSide = 800,
	twoPiecesMid = 900,
	twoPiecesDiagonal = 900,
	twoPiecesSideDiagonal = 900,
	twoPiecesAdjacentQuadrants = 700,
	twoPiecesMidAdjacentQuadrants = 1500,
	midBlockOpponentsThreePieces = 2000,
	sideBlockOpponentsThreePieces = 2000,
	DiagonalBlockOpponentsThreePieces = 500,
	threePiecesSide = 6000,
	threePiecesMid = 4000,
	threePiecesDiagonal = 4000,
	threePiecesAdjacentQuadrants = 2000,
	fourPiecesHorizontal = 11000,
	fourPiecesVertical = 10000,
	fourPiecesDiagonal = 0,
	fourPiecesDiagonalTwoMid = 13000,
	fourPiecessideDiagonal = 8000,
	fourPiecessideDiagonalMissingMid = 12000
	;
	
	// 192 = 2^6 * 3 ( 0 <= emptySpots <=2, more than 2 will be considered as 2)
	// 192: 0-63: no emptySpots; 64-127: 1 emptySpot; 128-191: 2 emptySpot
	 int[] sideHorizontalTable = new int[192];
	 int[] sideVerticalTable = new int[192];
	 int[] midHorizontalTable = new int[192];
	 int[] midVerticalTable = new int[192];
	 int[] diagonalTable = new int[192];
	 int[] sideDiagonalTable = new int[32];
	
	public ImprovedEval() {buildTables();}
	
	public int utilityOpponentNext(PentagoBoardState s, int turnPlayer) {
		int winner = s.getWinner();
		if (winner == Board.DRAW || winner == 1 - turnPlayer) return -100000;
		else if (winner == turnPlayer) return 100000;
		
		Piece[][] board = s.getBoard();
		int tmp = getNetScore(board, 1-turnPlayer, turnPlayer);
		return getNetScore(board, turnPlayer, turnPlayer) - tmp;
	}
	
	public int utilityThisNext(PentagoBoardState s, int turnPlayer)
	{
		int winner = s.getWinner();
		if (winner == Board.DRAW || winner == 1 - turnPlayer) return -100000;
		else if (winner == turnPlayer) return 100000;
		
		Piece[][] board = s.getBoard();
		
		return getNetScore(board, turnPlayer, turnPlayer) / getNetScore(board, 1-turnPlayer, turnPlayer);
	}
	
	int getNetScore(Piece[][] board, int playerToCount, int turnPlayer)
	{
		Piece player;
		if(playerToCount==PentagoBoardState.BLACK)
			player = Piece.BLACK;
		else
			player = Piece.WHITE;
		int result = 1;
	
		int[] linesResult = new int[18];
		///////////// side horizontal
		int encodedLine = 0;
		int emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[0][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[0] = sideHorizontalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[2][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[1] = sideHorizontalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[3][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[2] = sideHorizontalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[5][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[3] = sideHorizontalTable[encodedLine + 64*emptySpots];
		
		///////////// side vertical
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][0];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[4] = sideVerticalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][2];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[5] = sideVerticalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][3];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[6] = sideVerticalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][5];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[7] = sideVerticalTable[encodedLine + 64*emptySpots];
		
		///////////// mid horizontal
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[1][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[8] = midHorizontalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[4][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[9] = midHorizontalTable[encodedLine + 64*emptySpots];
		
		///////////// mid vertical
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][1];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[10] = midVerticalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][4];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[11] = midVerticalTable[encodedLine + 64*emptySpots];
		
		
		///////////// diagonal
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[12] = diagonalTable[encodedLine + 64*emptySpots];
		
		encodedLine = 0;
		emptySpots = 0;
		for(int i=0;i<6;i++)
		{
			Piece tmp = board[i][5-i];
			if(tmp==player) encodedLine += Math.pow(2, i);
			if(tmp==Piece.EMPTY) emptySpots++;
		}
		if(emptySpots>2) emptySpots=2;
		linesResult[13] = diagonalTable[encodedLine + 64*emptySpots];
		
		///////////// side diagonal
		encodedLine = 0;
		for(int i=0;i<5;i++)
		{
			Piece tmp = board[i][1+i];
			if(tmp==player) encodedLine += Math.pow(2, i);
		}
		linesResult[14] = sideDiagonalTable[encodedLine];
		
		encodedLine = 0;
		for(int i=0;i<5;i++)
		{
			Piece tmp = board[1+i][i];
			if(tmp==player) encodedLine += Math.pow(2, i);
		}
		linesResult[15] = sideDiagonalTable[encodedLine];
		
		encodedLine = 0;
		for(int i=0;i<5;i++)
		{
			Piece tmp = board[i][4-i];
			if(tmp==player) encodedLine += Math.pow(2, i);
		}
		linesResult[16] = sideDiagonalTable[encodedLine];
		
		encodedLine = 0;
		for(int i=0;i<5;i++)
		{
			Piece tmp = board[i+1][5-i];
			if(tmp==player) encodedLine += Math.pow(2, i);
		}
		linesResult[17] = sideDiagonalTable[encodedLine];
		
		for(int i=0;i<18;i++)
		{
			int tmp = linesResult[i];
			if(turnPlayer!=playerToCount && tmp>=8000) return 100000;
			result += tmp;
		}
		
		return result;
	}
	
	/**
	 *  build the utility arrays for each configuration of each winning line
	 */
	private void buildTables()
	{
		int[] possibleLine;
		for(int emptyStones=0; emptyStones<3;emptyStones++)
		{
			for(int i=0;i<64;i++)
			{
				// convert to binary
				possibleLine = new int[7];
				int tmp = i;
				possibleLine[0] = tmp/32;
				tmp %= 32;
				possibleLine[1] = tmp/16;
				tmp %= 16;
				possibleLine[2] = tmp/8;
				tmp %= 8;
				possibleLine[3] = tmp/4;
				tmp %= 4;
				possibleLine[4] = tmp/2;
				tmp %= 2;
				possibleLine[5] = tmp;
				possibleLine[6] = emptyStones;
				sideHorizontalTable[64*emptyStones+i] = processSideHorizontalVertical(possibleLine, true);
				sideVerticalTable[64*emptyStones+i] = processSideHorizontalVertical(possibleLine, false);
				midHorizontalTable[64*emptyStones+i] = processMidHorizontalVertical(possibleLine, true);
				midVerticalTable[64*emptyStones+i] = processMidHorizontalVertical(possibleLine, false);
				diagonalTable[64*emptyStones+i] = processDiagonal(possibleLine);
			}
		}
		for(int i=0;i<32;i++)
		{
			possibleLine = new int[5];
			int tmp = i;
			possibleLine[0] = tmp/16;
			tmp %= 16;
			possibleLine[1] = tmp/8;
			tmp %= 8;
			possibleLine[2] = tmp/4;
			tmp %= 4;
			possibleLine[3] = tmp/2;
			tmp %= 2;
			possibleLine[4] = tmp;
			sideDiagonalTable[i] = processSideDiagonal(possibleLine);
		}
		
	}
	
	private int processSideHorizontalVertical(int[] line, boolean isHorizontal)
	{
		// line[0-5] are stones, line[6] is number of empty spots
		// 1 means has stone, 0 means no
		if (line.length != 7) throw new IllegalArgumentException();
		int result = 0;
		int numPieces = 0;
		// check for 1 piece
		for(int i=0; i<6; i++)
		{
			if (line[i]==1 && i%3==1 && isHorizontal) result += onePieceSide;
			if (line[i]==1) numPieces++;
		}
		
		// check for two pieces
		if ( (line[0]==1&&line[1]==1) || line[1]==1&&line[2]==1)
			result += twoPiecesSide;
		if ( (line[3]==1&&line[4]==1) || line[4]==1&&line[5]==1)
			result += twoPiecesSide;
		if (line[1]==1&&line[4]==1)
			result += twoPiecesAdjacentQuadrants;
		
		// check for three pieces
		if(numPieces>=3)
		{
			if (line[0]==1 && line[1]==1 && line[2]==1)
				result += threePiecesSide;
			if (line[3]==1 && line[4]==1 && line[5]==1)
				result += threePiecesSide;
			if (line[1]==1&&line[3]==1) result += threePiecesAdjacentQuadrants;
		}
		
		// check for four pieces
		if(numPieces>=4 && ((line[1]==1||line[4]==1) && line[6]>1)
				|| (line[1]==1&&line[4]==1))
		{
			// if no empty spot, then it is impossible to win on here with 4 stones
			if (isHorizontal) result += fourPiecesHorizontal;
			else if (line[2]==1 && line[3]==1) result += fourPiecesVertical;
			
		}
		return result;
	}
	
	private int processMidHorizontalVertical(int[] line, boolean isHorizontal)
	{
		// line[0-5] are stones, line[6] is number of empty spots
		// 1 means has stone, 0 means no
		if (line.length != 7) throw new IllegalArgumentException();
		int result = 0;
		int numPieces = 0;
		// check for 1 piece
		for(int i=0; i<6; i++)
		{
			if(line[i]==1)numPieces++;
			if (line[i]==1&&isHorizontal)
			{
				if(i%3==1) result += onePieceMid;
				else result += onePieceSide;
			}
		}
					
		// check for two pieces
		if ( (line[0]==1&&line[1]==1) || line[1]==1&&line[2]==1)
			result += twoPiecesMid;
		if ( (line[3]==1&&line[4]==1) || line[4]==1&&line[5]==1)
			result += twoPiecesMid;
		if (line[1]==1&&line[4]==1)
			result += twoPiecesMidAdjacentQuadrants;
					
		// check for three pieces
		if(numPieces>=3)
		{
			if (line[0]==1 && line[1]==1 && line[2]==1)
				result += threePiecesMid;
			if (line[3]==1 && line[4]==1 && line[5]==1)
				result += threePiecesMid;
			if (line[1]==1&&line[3]==1) result += threePiecesAdjacentQuadrants;
		}
				
		// check for four pieces						
		if(numPieces>=4 && (line[1]==1&&line[4]==1))
		{
			// if no empty spot, then it is impossible to win on here with 4 stones
			// if no two mid then impossible to win
			result += fourPiecesDiagonalTwoMid;
			if (isHorizontal) result += fourPiecesHorizontal;
			else result += fourPiecesVertical;
		}
		return result;
	}
	
	private int processDiagonal(int[] line)
	{
		// line[0-5] are stones, line[6] is number of empty spots
		// 1 means has stone, 0 means no
		if (line.length != 7) throw new IllegalArgumentException();
		int result = 0;
		int numPieces = 0;
		for(int i=0; i<6; i++)
		{
			if(line[i]==1)numPieces++;
		}
					
		// check for two pieces
		if ( (line[0]==1&&line[1]==1) || line[1]==1&&line[2]==1)
			result += twoPiecesDiagonal;
		if ( (line[3]==1&&line[4]==1) || line[4]==1&&line[5]==1)
			result += twoPiecesDiagonal;
		if (line[1]==1&&line[4]==1)
			result += twoPiecesMidAdjacentQuadrants;
					
		// check for three pieces
		if(numPieces>=3)
		{
			if (line[0]==1 && line[1]==1 && line[2]==1)
				result += threePiecesDiagonal;
			if (line[3]==1 && line[4]==1 && line[5]==1)
				result += threePiecesDiagonal;
			if (line[1]==1&&line[3]==1) result += threePiecesAdjacentQuadrants;
		}
				
		// check for four pieces						
		if(numPieces>=4 && (line[1]==1&&line[4]==1))
		{
			// if no empty spot, then it is impossible to win on here with 4 stones
			// if no two mid then impossible to win
			result += fourPiecesDiagonalTwoMid;
		}
		return result;
	}
	
	private int processSideDiagonal(int[] line)
	{
		// line[0-4] are stones
		if (line.length != 5) throw new IllegalArgumentException();
		int result = 0;
		int numPieces = 0;
		for(int i=0; i<5; i++)
		{
			if(line[i]==1) numPieces++;
		}
		
		// check for two pieces
		if ( (line[0]==1&&line[1]==1))
			result += twoPiecesSideDiagonal;
		if ( (line[3]==1&&line[4]==1))
			result += twoPiecesSideDiagonal;
		
		// doesn't check for three pieces
		
		// check for four pieces
		if (numPieces>=4)
		{
			if (line[2]==1) result += fourPiecessideDiagonalMissingMid;
			else result += fourPiecessideDiagonal;
		}
		return result;
	}
}