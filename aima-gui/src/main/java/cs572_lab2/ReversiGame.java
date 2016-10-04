package cs572_lab2;

import java.util.List;

//import aima.core.environment.tictactoe.TicTacToeState;
//import aima.core.search.adversarial.Game;
//import aima.core.util.datastructure.XYLocation;

/**
 * Provides an implementation of the Tic-tac-toe game which can be used for
 * experiments with the Minimax algorithm.
 * 
 * @author Ruediger Lunde
 * 
 */
public class ReversiGame implements Game<ReversiState, XYLocation, String> {

	ReversiState initialState = new ReversiState();

	@Override
	public ReversiState getInitialState() {
		return initialState;
	}

	@Override
	public String[] getPlayers() {
		return new String[] { ReversiState.X, ReversiState.O };
	}

	@Override
	public String getPlayer(ReversiState state) {
		return state.getPlayerToMove();
	}

	@Override
	public List<XYLocation> getActions(ReversiState state) {
		return state.getFeasiblePositions();
	}

	@Override
	public ReversiState getResult(ReversiState state, XYLocation action) {
		ReversiState result = state.clone();
		result.mark(action);
		return result;
	}

	@Override
	public boolean isTerminal(ReversiState state) {
		return state.getUtility() != -1;
	}
	
	@Override
	public boolean needsCutOff(ReversiState state)
	{
		int depth = state.getNumberOfMarkedPositions();
		int rest_moves = 64-depth;
		
		if( rest_moves >= 5)//15 is good enough
			return true;
		else
			return false;		
	}
	
	@Override
	public double getUtility(ReversiState state, String player) {
		double result = state.getUtility();
		if (result != -1) {											
			if (player == ReversiState.O)
				result = 1 - result;			
		} else {
			throw new IllegalArgumentException("State is not terminal.");
		}
		
		return result;
	}
	
	@Override
	public int getEvaluation(ReversiState state, String player)
	{
		int result;
		
		int markedPositions = state.getNumberOfMarkedPositions();
		int num_black = state.getNumberOfBlackPieces();
		int num_white = markedPositions- num_black;
		
		if (player == ReversiState.X)
			result = num_black - num_white;
		else
			result = num_white - num_black;
		
		return result;
	}
}
