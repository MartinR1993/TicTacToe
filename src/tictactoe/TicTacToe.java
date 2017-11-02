package tictactoe;

import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

	public static boolean isRunning = true;

	public static void main(String[] args) { 
		while(isRunning){
			Board b = new Board();
			Random rand = new Random();

			System.out.println("The Board: ");
			b.displayBoard();

			System.out.println("\nWho starts: \n"
					+ "1. AI Player (X) \n"
					+ "2. User (O) ");
			int choice = b.scan.nextInt();
			if (choice == 1) {
				Point p = new Point(rand.nextInt(3), rand.nextInt(3));
				b.placeAMove(p, 1);
				System.out.println("\nOpponent's move: (" + p.x + ", " + p.y + ")");
				b.displayBoard();
			}

			while (!b.isGameOver()) {
				b.takeInput();
				b.displayBoard();
				if (b.isGameOver()){
					break;
				}

				System.out.println("\nEvery point's score: ");
				b.alphaBetaMinimax(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1);
				for (int i = 0; i < b.rootsChildrenScore.size(); i++) {
					System.out.println("Point (" + b.rootsChildrenScore.get(i).point.x + "," + b.rootsChildrenScore.get(i).point.y + ") Score: " + b.rootsChildrenScore.get(i).score);
					b.placeAMove(b.returnBestMove(), 1);
				}

				System.out.println("\nOpponent's move: (" + b.returnBestMove().x + "," + b.returnBestMove().y + ")");
				b.displayBoard();
			}
			if (b.hasXWon()) {
				System.out.println("You lost!");
			} else if (b.hasOWon()) {
				System.out.println("You win!");
			} else {
				System.out.println("It's a draw!");
			}
			newGame();
		}
	}
	
	public static void newGame(){
		System.out.println("\nPlay again? \n"
				+ "1. Yes\n"
				+ "2. No");
		Scanner scan = new Scanner(System.in);
		int answer = scan.nextInt();
		if(answer == 1){
			System.out.println("New game started!\n");
		}
		else if(answer == 2){
			System.out.println("Game finished!");
			isRunning = false;
			scan.close();
		}
		else{
			System.out.println("Input out of range(0-1). Try Again!");
			newGame();
		}
	}
}

