package student_player;

public interface Utility
{
	/**
	 * 
	 * @param state
	 * 				the state of the board after the current player chooses some move
	 * @param turnPlayer
	 * 				the player of the current turn
	 * @return the utility value of the state for the current player
	 */
	int utilityOpponentNext(pentago_twist.PentagoBoardState state, int turnPlayer);
	
	/**
	 * 
	 * @param state
	 * 				the state of the board before the current player chooses any move
	 * @param turnPlayer
	 * 				the player of the current turn
	 * @return the utility value of the state for the current player
	 */
	int utilityThisNext(pentago_twist.PentagoBoardState state, int turnPlayer);
}
