package cs572_lab2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aima.core.util.datastructure.XYLocation;

/**
 * A state of the Tic-tac-toe game is characterized by a board containing
 * symbols X and O, the next player to move, and an utility information.
 * 
 * @author Ruediger Lunde
 * 
 */
public class ReversiState implements Cloneable {
	public static final String O = "O";
	public static final String X = "X";
	public static final String EMPTY = "-";
	//
	private String[] board = new String[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, O, X, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, X, O, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
			EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY};

	private String playerToMove = X;
	private double utility = -1; // 1: win for X, 0: win for O, 0.5: draw

	public String getPlayerToMove() {
		return playerToMove;
	}
	
	public boolean isEmpty(int col, int row) {
		return board[getAbsPosition(col, row)] == EMPTY;
	}

	public String getValue(int col, int row) {
		return board[getAbsPosition(col, row)];
	}

	public double getUtility() {
		return utility;
	}

	public void mark(XYLocation action) {
		mark(action.getXCoOrdinate(), action.getYCoOrdinate());
	}

	public void mark(int col, int row) {
		if (utility == -1 && getValue(col, row) == EMPTY) {
			board[getAbsPosition(col, row)] = playerToMove;
			flipPieces(col,row);
			analyzeUtility();
			playerToMove = (playerToMove == X ? O : X);
		}
	}

	public List<XYLocation> getFeasiblePositions()
	{
		List<XYLocation> unMarkedPositions = getUnMarkedPositions();
		List<XYLocation> feasiblePositions = new ArrayList<>();
		
		for(int i=0;i<unMarkedPositions.size();i++)
		{
			XYLocation node = unMarkedPositions.get(i);
			int col = node.getXCoOrdinate();
			int row = node.getYCoOrdinate();
			if(hasNeighbours(node) && getNumberOfPossibleFlips(col, row)!=0)
				feasiblePositions.add(node);				
		}
		
		return feasiblePositions;
	}
	
	public boolean hasNeighbours(XYLocation pos)
	{
		XYLocation NW = new XYLocation(pos.getXCoOrdinate()-1,pos.getYCoOrdinate()-1);
		XYLocation NE = new XYLocation(pos.getXCoOrdinate()+1,pos.getYCoOrdinate()-1);
		XYLocation SE = new XYLocation(pos.getXCoOrdinate()+1,pos.getYCoOrdinate()+1);
		XYLocation SW = new XYLocation(pos.getXCoOrdinate()-1,pos.getYCoOrdinate()+1);
		
		XYLocation[] neighbours = {pos.left(), pos.right(),pos.up(),pos.down(),NW,NE,SE,SW};
		
		for(int i=0;i<neighbours.length;i++)
		{
			if( withinTheBorad(neighbours[i]) )
			{
				int col = neighbours[i].getXCoOrdinate();
				int row = neighbours[i].getYCoOrdinate();
				if(!getValue(col,row).equals(EMPTY))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean withinTheBorad(XYLocation pos)
	{
		int col = pos.getXCoOrdinate();
		int row = pos.getYCoOrdinate();
		
		if( col>=0 && col <=7 && row >=0 && row<=7 )
			return true;
		else
			return false;
	}
	
	private int flipPieces(int col, int row)
	{
		ArrayList<ArrayList<XYLocation>> nodesToflip = new ArrayList<>(8);
		
		nodesToflip.add( detectNorth(col, row));
		nodesToflip.add( detectSouth(col, row));
		nodesToflip.add( detectWest(col, row));
		nodesToflip.add( detectEast(col, row));
		nodesToflip.add( detectNorthwest(col, row));
		nodesToflip.add( detectNortheast(col, row));
		nodesToflip.add( detectSouthwest(col, row));
		nodesToflip.add( detectSoutheast(col, row));
		
		int num=0; // number of flips
		for(int i=0;i<8;i++) // 8 directions
		{
			if(nodesToflip.get(i) != null)
			{
				num += nodesToflip.get(i).size();
				flipPiecesIn(nodesToflip.get(i));
			}
		}
		
		return num;
	}
	
	private int getNumberOfPossibleFlips(int col, int row)
	{
		ArrayList<ArrayList<XYLocation>> nodesToflip = new ArrayList<>(8);
		
		nodesToflip.add( detectNorth(col, row));
		nodesToflip.add( detectSouth(col, row));
		nodesToflip.add( detectWest(col, row));
		nodesToflip.add( detectEast(col, row));
		nodesToflip.add( detectNorthwest(col, row));
		nodesToflip.add( detectNortheast(col, row));
		nodesToflip.add( detectSouthwest(col, row));
		nodesToflip.add( detectSoutheast(col, row));
		
		int num=0; // number of flips
		for(int i=0;i<8;i++) // 8 directions
		{
			if(nodesToflip.get(i) != null)
			num += nodesToflip.get(i).size();
		}
		
		return num;
	}
	
	private ArrayList<XYLocation> detectNorth(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int iconst = col;
		int jstart = row-1;
		int jend   = 0;
		
		if (jstart -jend <= 0)
			return null;
		
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int j;
		for(j = jstart;j >=jend; j-- )
		{
			if(getValue(iconst,j).equals(EMPTY))
				break;
			
			if(getValue(iconst,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			list.add(new XYLocation(iconst, j));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 			
		}
		else{
			//flipPiecesIn(list);
			foundpos = new XYLocation(iconst,j);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectSouth(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int iconst = col;
		int jstart = row+1;
		int jend   = 7;
		
		if (jstart -jend >= 0)
			return null;
		
		
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int j;
		for(j = jstart;j <=jend; j++ )
		{
			if(getValue(iconst,j).equals(EMPTY))
				break;
			
			if(getValue(iconst,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(iconst, j));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(iconst,j);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectWest(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int jconst = row;
		int istart = col-1;
		int iend   = 0;
		
		if ( iend >= istart)
			return null;
		
		
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i;
		for(i = istart;i >= iend; i-- )
		{
			if(getValue(i,jconst).equals(EMPTY))
				break;
			
			if(getValue(i,jconst).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,jconst));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i,jconst);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectEast(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int jconst = row;
		int istart = col+1;
		int iend   = 7;
		
		if ( iend <= istart)
			return null;
		
		
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i;
		for(i = istart;i <= iend; i++ )
		{
			if(getValue(i,jconst).equals(EMPTY))
				break;
			
			if(getValue(i,jconst).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,jconst));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i,jconst);
			//flipPiecesIn(list);
			return list;  // the position of the piece on the other end
		}
		
	}
	
	private ArrayList<XYLocation> detectNorthwest(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int istart = col-1;
		int iend   = 0;
		int jstart = row-1;
		int jend   = 0;
		
		if ( iend >= istart)
			return null;
		
		if ( jend >= jstart)
			return null;
				
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i,j;
		for(i = istart,j=jstart;i >= iend && j>=jend; i--,j-- )
		{
			if(getValue(i,j).equals(EMPTY))
				break;
			
			if(getValue(i,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,j));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i,j);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectNortheast(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int istart = col+1;
		int iend   = 7;
		int jstart = row-1;
		int jend   = 0;
		
		if ( iend <= istart)
			return null;
		
		if ( jend >= jstart)
			return null;
				
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i,j;
		for(i = istart,j=jstart;i <= iend && j>=jend; i++,j-- )
		{
			if(getValue(i,j).equals(EMPTY))
				break;
			
			if(getValue(i,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,j));
		}
		
		XYLocation foundpos;
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i, j);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectSoutheast(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int istart = col+1;
		int iend   = 7;
		int jstart = row+1;
		int jend   = 7;
		
		if ( iend <= istart)
			return null;
		
		if ( jend <= jstart)
			return null;
				
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i,j;
		for(i = istart,j=jstart;i <= iend && j<=jend; i++,j++ )
		{
			if(getValue(i,j).equals(EMPTY))
				break;
			
			if(getValue(i,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,j));
		}
		
		XYLocation foundpos;
		
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i,j);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	private ArrayList<XYLocation> detectSouthwest(int col, int row)
	{
		// returns the nodes( as ArrayList) to be flipped
		
		int istart = col-1;
		int iend   = 0;
		int jstart = row+1;
		int jend   = 7;
		
		if ( iend >= istart)
			return null;
		
		if ( jend <= jstart)
			return null;
				
		ArrayList<XYLocation> list = new ArrayList<>(); 
		
		boolean found = false;
		int i,j;
		for(i = istart,j=jstart;i >= iend && j<=jend; i--,j++ )
		{
			if(getValue(i,j).equals(EMPTY))
				break;
			
			if(getValue(i,j).equals(playerToMove))
			{
				found = true;
				break;
			}
			
			// at this point, the node can only be of the other color
			list.add(new XYLocation(i,j));
		}
		
		XYLocation foundpos;
		
		if(found == false)
		{
			foundpos = null;
			return null; 
		}
		else{
			foundpos = new XYLocation(i,j);
			//flipPiecesIn(list);
			return list;  
		}
		
	}
	
	void flipPiecesIn( ArrayList<XYLocation> list)
	{
		for(int i=0;i<list.size();i++)
		{
			int col = list.get(i).getXCoOrdinate();
			int row = list.get(i).getYCoOrdinate();
			board[getAbsPosition(col, row)] = playerToMove;
		}
	}
	
	private void analyzeUtility() {

		if( getNumberOfMarkedPositions() < 64)
			utility = -1;  // not terminal needs heuristic function here?
		else if ( getNumberOfBlackPieces() > 32)
			utility = 1;   // black(X) win
		else if ( getNumberOfBlackPieces() == 32)
			utility = 0.5; // draw
		else
			utility = 0;   // while(O) win
	}

	public int getNumberOfBlackPieces()
	{		
		int retVal = 0;
		for (int col = 0; col < 8; col++) {
			for (int row = 0; row < 8; row++) {
				if ( getValue(col, row).equals("X") ) {
					retVal++;
				}
			}
		}
		return retVal;
	}
	

	public int getNumberOfMarkedPositions() {
		int retVal = 0;
		for (int col = 0; col < 8; col++) {
			for (int row = 0; row < 8; row++) {
				if (!(isEmpty(col, row))) {
					retVal++;
				}
			}
		}
		return retVal;
	}

	public List<XYLocation> getUnMarkedPositions() {
		List<XYLocation> result = new ArrayList<XYLocation>();
		for (int col = 0; col < 8; col++) {
			for (int row = 0; row < 8; row++) {
				if (isEmpty(col, row)) {
					result.add(new XYLocation(col, row));
				}
			}
		}
		return result;
	}

	@Override
	public ReversiState clone() {
		ReversiState copy = null;
		try {
			copy = (ReversiState) super.clone();
			copy.board = Arrays.copyOf(board, board.length);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace(); // should never happen...
		}
		return copy;
	}

	@Override
	public boolean equals(Object anObj) {
		if (anObj != null && anObj.getClass() == getClass()) {
			ReversiState anotherState = (ReversiState) anObj;
			for (int i = 0; i < 64; i++) {
				if (board[i] != anotherState.board[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// Need to ensure equal objects have equivalent hashcodes (Issue 77).
		return toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				strBuilder.append(getValue(col, row) + " ");
			}
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}

	//
	// PRIVATE METHODS
	//

	private int getAbsPosition(int col, int row) {
		return row * 8 + col;
	}
}
