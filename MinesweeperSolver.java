package mine;
import java.util.Random;
import java.util.Arrays;
class MinesweeperSolver {
	MinesweeperGame game;
	int[][] currentBoard;
	boolean[][] flag;
	int numBombsLeft;
	int height;
	int width;
	int totalNumBombs;
	MinesweeperSolver(MinesweeperGame game){
		this.game = game;
		height = game.getHeight();
		width = game.getWidth();
		totalNumBombs = game.getNumberOfBombs();
		numBombsLeft = game.getNumberOfBombs();
		flag = new boolean[height][width];
		currentBoard = game.getCurrentBoard();
	}

	void doStuff(){
		int oldHash = 0;
		goRandom();
		currentBoard = game.getCurrentBoard();
		int newHash = Arrays.deepHashCode(currentBoard);
		printCurrentBoard();
		while(!game.getHasLost() && !game.hasWon()){
			for (int y = 0; y < height; y++){
				for (int x = 0; x < width; x++){
					allSurroundingBombsFound(x, y);
					onlyBombSpotsLeft(x, y);
				}
			}
			currentBoard = game.getCurrentBoard();
			oldHash = newHash;
			newHash = Arrays.deepHashCode(currentBoard);
			if (oldHash == newHash) {
				System.out.println((double)numBombsLeft);
				System.out.println((double)totalNumBombs);
				System.out.println((double)numBombsLeft/(double)totalNumBombs);
				if ((double)numBombsLeft/(double)totalNumBombs < .4){
					System.out.println("Doing Calculations");
					int[][] poss = letsUseProbability();
					int high = poss[0][0];
					int highX = 0;
					int highY = 0;
					for (int y = 0; y < height; y++){
						for (int x = 0; x < width; x++){
							if (poss[y][x] > high){
								high = poss[y][x];
								highX = x;
								highY = y;
							}
						}
					}
					game.reveal(highX, highY);
				} else {
					goRandom();
				}
			}
			printCurrentBoard();
		}
		if (game.getHasLost()) System.out.println("LOST");
		if (game.hasWon()) System.out.println("WON");
 	}


	void goRandom(){
		Random rand = new Random();
		int valueX = rand.nextInt(width);
		int valueY = rand.nextInt(height);
		while(currentBoard[valueY][valueX] != 9 || isFlagged(valueX, valueY)){
			valueX = rand.nextInt(width);
			valueY = rand.nextInt(height);
		}
		System.out.println("Random @("+ valueX + ","+valueY+")");
		game.reveal(valueX, valueY);
	}

	void printCurrentBoard(){
		currentBoard = game.getCurrentBoard();
		for (int a = 0; a < height; a++){
			for (int b = 0; b < width; b++){
				if (flag[a][b]) System.out.print("F");
				else {
					if (currentBoard[a][b] != 9 && currentBoard[a][b] != -1) System.out.print(currentBoard[a][b]);
					else if (currentBoard[a][b] != 9) System.out.print("B");
					else System.out.print("+");
				}
			}
			System.out.println();
		}
		System.out.println("--------------------");
	}

	boolean isFlagged(int x, int y){
		return flag[y][x];
	}

	void flagSpot(int x, int y){
		if (!flag[y][x]) numBombsLeft--;
		flag[y][x] = true;
	}

	void allSurroundingBombsFound(int x, int y){
		int val = currentBoard[y][x];
		if (val > 0 && val < 9) {
			if (y != 0 && flag[y-1][x]) val--; //top
			if (x != 0 && flag[y][x-1]) val--; //left
			if (y != 0 && x != 0 && flag[y-1][x-1]) val--; //top left
			if (y != height-1 && flag[y+1][x]) val--; //bottom
			if (x != width-1 && flag[y][x+1]) val--; //right
			if (x != width-1 && y != height-1 && flag[y+1][x+1]) val--;	//bottom right
			if (x != 0 && y != height-1 && flag[y+1][x-1]) val--; //bottom left
			if (y != 0 && x != width-1 && flag[y-1][x+1]) val--;//top right
		}
		if (val == 0 && currentBoard[y][x] != 0){ //have zero bombs left to be found around it AND is not a zero block
			if (y != 0 && !flag[y-1][x]) game.reveal(x, y-1); //top
			if (x != 0 && !flag[y][x-1]) game.reveal(x-1, y); //left
			if (y != 0 && x != 0 && !flag[y-1][x-1]) game.reveal(x-1, y-1); //top left
			if (y != height-1 && !flag[y+1][x]) game.reveal(x, y+1); //bottom
			if (x != width-1 && !flag[y][x+1]) game.reveal(x+1, y); //right
			if (x != width-1 && y != height-1 && !flag[y+1][x+1]) game.reveal(x+1, y+1);	//bottom right
			if (x != 0 && y != height-1 && !flag[y+1][x-1]) game.reveal(x-1, y+1); //bottom left
			if (y != 0 && x != width-1 && !flag[y-1][x+1]) game.reveal(x+1, y-1);//top right
		}
	}

	//when there are 3 spots lefts & 3 bombs left for a given time (flag all)
	void onlyBombSpotsLeft(int x, int y){
		int val = currentBoard[y][x]; // number of bombs surrounding
		if (val > 0 && val < 9) {
			//count how many bombs are left
			if (y != 0 && flag[y-1][x]) val--; //top
			if (x != 0 && flag[y][x-1]) val--; //left
			if (y != 0 && x != 0 && flag[y-1][x-1]) val--; //top left
			if (y != height-1 && flag[y+1][x]) val--; //bottom
			if (x != width-1 && flag[y][x+1]) val--; //right
			if (x != width-1 && y != height-1 && flag[y+1][x+1]) val--;	//bottom right
			if (x != 0 && y != height-1 && flag[y+1][x-1]) val--; //bottom left
			if (y != 0 && x != width-1 && flag[y-1][x+1]) val--;//top right


			//count how many open spaces
			int openSpaces = 0;
			if (y != 0 && currentBoard[y-1][x] == 9 && !flag[y-1][x]) openSpaces++; //top
			if (x != 0 && currentBoard[y][x-1] == 9 && !flag[y][x-1]) openSpaces++; //left
			if (y != 0 && x != 0 && currentBoard[y-1][x-1] == 9 && !flag[y-1][x-1]) openSpaces++; //top left
			if (y != height-1 && currentBoard[y+1][x] == 9 && !flag[y+1][x]) openSpaces++; //bottom
			if (x != width-1 && currentBoard[y][x+1] == 9 && !flag[y][x+1]) openSpaces++; //right
			if (x != width-1 && y != height-1 && currentBoard[y+1][x+1] == 9 && !flag[y+1][x+1]) openSpaces++;	//bottom right
			if (x != 0 && y != height-1 && currentBoard[y+1][x-1] == 9 && !flag[y+1][x-1]) openSpaces++; //bottom left
			if (y != 0 && x != width-1 && currentBoard[y-1][x+1] == 9 && !flag[y-1][x+1]) openSpaces++;//top right
			//System.out.println("x: " + x + " y: " + y);
			//System.out.println(openSpaces + "  " + val);
			if (openSpaces == val) {
				if (y != 0 && currentBoard[y-1][x] == 9) flagSpot(x,y-1); //top
				if (x != 0 && currentBoard[y][x-1] == 9) flagSpot(x-1,y); //left
				if (y != 0 && x != 0 && currentBoard[y-1][x-1] == 9) flagSpot(x-1,y-1); //top left
				if (y != height-1 && currentBoard[y+1][x] == 9) flagSpot(x,y+1); //bottom
				if (x != width-1 && currentBoard[y][x+1] == 9) flagSpot(x+1,y); //right
				if (x != width-1 && y != height-1 && currentBoard[y+1][x+1] == 9) flagSpot(x+1,y+1);	//bottom right
				if (x != 0 && y != height-1 && currentBoard[y+1][x-1] == 9) flagSpot(x-1,y+1); //bottom left
				if (y != 0 && x != width-1 && currentBoard[y-1][x+1] == 9) flagSpot(x+1,y-1);//top right
			}
		}
	}
	//PROBABILITY FUNCTIONS
	//as before currentBoard (0-8 #, 9 is unknown)
	//flag array says if bomb
	//NOW
	//in board (-1: bomb, 0-8 #, 9 is unknown)
	int[][] letsUseProbability(){
		int[][] possibilities = new int[height][width];
		int[][] board = new int[height][width];		
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				board[y][x] = currentBoard[y][x];
				if (isFlagged(x, y)) board[y][x] = -1;
			}
		}
		helper(board, 0, 0, numBombsLeft, possibilities);
		return possibilities;
	}

	void helper(int[][] board, int x, int y, int numLeft, int[][] possibilities){
		//System.out.println("("+x+","+y+")");
		if (numLeft == 0){
			if (validBoard(board)) {
				for (int yy = 0; yy < height; yy++){
					for (int xx = 0; xx < width; xx++){ //not a bomb (according to calc && not known yet)
						if (board[yy][xx] != -1 && currentBoard[yy][xx] == 9) possibilities[yy][xx]++; 
					}
				}
			} else return;
		} else {
			//if (!possibleBoard(board)) return;
			int newX = x+1;
			int newY = y;
			if (newX == width) {
				newX = 0;
				newY = y+1;
			}
			if (newY == height) return;
			if (currentBoard[y][x] == 9) {
				board[y][x] = -1;
				helper(board, newX, newY, numLeft-1, possibilities);
				board[y][x] = 9;
				helper(board, newX, newY, numLeft, possibilities);
			} else {
				helper(board, newX, newY, numLeft, possibilities);
			}

		}
	}

	boolean possibleBoard(int[][] board){ //does this speed up the code?
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				int val = board[y][x];
				if (val == -1 || val == 9) continue;
				if (y != 0 && board[y-1][x] == -1) val--; //top
				if (x != 0 && board[y][x-1] == -1) val--; //left
				if (y != 0 && x != 0 && board[y-1][x-1] == -1) val--; //top left
				if (y != height-1 && board[y+1][x] == -1) val--; //bottom
				if (x != width-1 && board[y][x+1] == -1) val--; //right
				if (x != width-1 && y != height-1 && board[y+1][x+1] == -1) val--;	//bottom right
				if (x != 0 && y != height-1 && board[y+1][x-1] == -1) val--; //bottom left
				if (y != 0 && x != width-1 && board[y-1][x+1] == -1) val--;//top right
				if (val < 0) return false;
			}
		}
		return true;
	}

	boolean validBoard(int[][] board){ //everything checks out
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				int val = board[y][x];
				if (val == -1 || val == 9) continue;
				if (y != 0 && board[y-1][x] == -1) val--; //top
				if (x != 0 && board[y][x-1] == -1) val--; //left
				if (y != 0 && x != 0 && board[y-1][x-1] == -1) val--; //top left
				if (y != height-1 && board[y+1][x] == -1) val--; //bottom
				if (x != width-1 && board[y][x+1] == -1) val--; //right
				if (x != width-1 && y != height-1 && board[y+1][x+1] == -1) val--;	//bottom right
				if (x != 0 && y != height-1 && board[y+1][x-1] == -1) val--; //bottom left
				if (y != 0 && x != width-1 && board[y-1][x+1] == -1) val--;//top right
				if (val != 0) return false;
			}
		}
		return true;
	}
}