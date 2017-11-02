package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import tictactoe.Point;
import tictactoe.PointsAndScores;

public class Board {

	List<Point> availablePoints;
	Scanner scan = new Scanner(System.in);
	int[][] board = new int[3][3]; 

	List<PointsAndScores> rootsChildrenScore = new ArrayList<>();
	
	//Evaluate points on board
	public int evaluateBoard() {
		int score = 0;

		//Evaluate all rows
		for (int i = 0; i < 3; ++i) {
			int X = 0;
			int O = 0;
			for (int j = 0; j < 3; ++j) {
				if (board[i][j] == 1) {
					X++;
				} else if (board[i][j] == 2) {
					O++;
				}
			} 
			score+=changeInScore(X, O); 
		}

		//Evaluate all columns
		for (int j = 0; j < 3; ++j) {
			int X = 0;
			int O = 0;
			for (int i = 0; i < 3; ++i) {
				if (board[i][j] == 1) {
					X++;
				} else if (board[i][j] == 2) {
					O++;
				}
			}
			score+=changeInScore(X, O);
		}

		//Evaluate diagonal (0,0)->(1,1)->(2,2)
		int X = 0;
		int O = 0;
		for (int i = 0, j = 0; i < 3; ++i, ++j) {
			if (board[i][j] == 1) {
				X++;
			} else if (board[i][j] == 2) {
				O++;
			}
		}
		score+=changeInScore(X, O);

		//Evaluate Diagonal (2,0)->(1,1)->(0,2)
		X = 0;
		O = 0;
		for (int i = 2, j = 0; i > -1; --i, ++j) {
			if (board[i][j] == 1) {
				X++;
			} else if (board[i][j] == 2) {
				O++;
			}
		}
		score+=changeInScore(X, O);

		return score;
	}
	//Find the score for each evaluation
	private int changeInScore(int X, int O){
		int change;
		if (X == 3) {
			change = 100;
		} else if (X == 2 && O == 0) {
			change = 10;
		} else if (X == 1 && O == 0) {
			change = 1;
		} else if (O == 3) {
			change = -100;
		} else if (O == 2 && X == 0) {
			change = -10;
		} else if (O == 1 && X == 0) {
			change = -1;
		} else {
			change = 0;
		} 
		return change;
	}

	//Set this to some value if you want to have some specified depth limit for search
	int uptoDepth = -1;

	public int alphaBetaMinimax(int alpha, int beta, int depth, int turn){

		if(beta<=alpha){ 
			//Pruning at depth
			if(turn == 1){ 
				return Integer.MAX_VALUE;
			}
			else{ 
				return Integer.MIN_VALUE; 
			}
		}

		//Leaf or end game state (win, loose or draw)
		if(depth == uptoDepth || isGameOver()){
			return evaluateBoard();
		}

		List<Point> pointsAvailable = getAvailableStates();

		if(pointsAvailable.isEmpty()) {
			return 0;
		}

		//No depth, no children
		if(depth==0) {
			rootsChildrenScore.clear(); 
		}

		int maxValue = Integer.MIN_VALUE;
		int minValue = Integer.MAX_VALUE;

		for(int i = 0; i < pointsAvailable.size(); ++i){
			Point point = pointsAvailable.get(i);

			int currentScore = 0;

			//AI Player turn
			if(turn == 1){
				placeAMove(point, 1); 
				currentScore = alphaBetaMinimax(alpha, beta, depth+1, 2);
				maxValue = Math.max(maxValue, currentScore); 

				//Set alpha
				alpha = Math.max(currentScore, alpha);

				if(depth == 0){
					rootsChildrenScore.add(new PointsAndScores(currentScore, point));
				}
			}
			//User turn
			else if(turn == 2){
				placeAMove(point, 2);
				currentScore = alphaBetaMinimax(alpha, beta, depth+1, 1); 
				minValue = Math.min(minValue, currentScore);

				//Set beta
				beta = Math.min(currentScore, beta);
			}
			//Reset board
			board[point.x][point.y] = 0; 

			//If a pruning has been done, don't evaluate the rest of the sibling states
			if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) {
				break;
			}
		}
		//Return maxValue if AI Player
		if(turn == 1){
			return maxValue;
		}
		//Return minValue if User
		else{
			return minValue;
		}
	}  
	
	//Game over if one has won, or if board is full
	public boolean isGameOver() {
		return (hasXWon() || hasOWon() || getAvailableStates().isEmpty());
	}
	
	//Check for AI Player winning state
	public boolean hasXWon() {
		//Diagonal win for X
		if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
			return true;
		}
		//Row or Column win for O
		for (int i = 0; i < 3; ++i) {
			if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
					|| (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
				return true;
			}
		}
		return false;
	}

	//Check for User winning state
	public boolean hasOWon() {
		//Diagonal win for O
		if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
			return true;
		}
		//Row or Column win for O
		for (int i = 0; i < 3; ++i) {
			if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
					|| (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
				return true;
			}
		}

		return false;
	}

	//Check where on board there is empty points
	public List<Point> getAvailableStates() {
		availablePoints = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (board[i][j] == 0) {
					availablePoints.add(new Point(i, j));
				}
			}
		}
		return availablePoints;
	}

	public void placeAMove(Point point, int player) {
		board[point.x][point.y] = player;   //player = 1 for X, 2 for O
	}

	//Returns the best move from the rootsChildrenScore list
	public Point returnBestMove() {
		int MAX = -100000;
		int best = -1;

		for (int i = 0; i < rootsChildrenScore.size(); ++i) {
			if (MAX < rootsChildrenScore.get(i).score) {
				MAX = rootsChildrenScore.get(i).score;
				best = i;
			}
		}

		return rootsChildrenScore.get(best).point;
	}

	//Takes input from the user
	public void takeInput() {
		System.out.println("\nYour move: (row, col)");
		int x = scan.nextInt();
		int y = scan.nextInt();
		if ((x < 0 || x > 2) || (y < 0 || y > 2)){
			System.out.println("Input out of range(0-2). Try again!");
			takeInput();
		}
		else{
			if(board[x][y] == 0){
				Point point = new Point(x, y);
				placeAMove(point, 2);
			}

			else{
				int xory = board[x][y];
				if (xory == 1){
					System.out.println("Point already taken by AI Player(X). Try Again!");
				}
				else if (xory == 2){
					System.out.println("Point already taken by User(O). Try Again!");
				}
				takeInput();
			}
		}
	}

	//Displays the board
	public void displayBoard() {

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if(board[i][j] == 0){
					System.out.print("- ");
				}
				else if(board[i][j] == 1){
					System.out.print("X ");
				}
				else{
					System.out.print("O ");
				}
			}
			System.out.println();

		}
	} 

	//Resets the board
	public void resetBoard() {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				board[i][j] = 0;
			}
		}
	} 
}
