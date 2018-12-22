package mine;
import java.util.Random;
class MinesweeperGame {
	int[][] board; //0-8 is number of bombs & -1 is bomb
	boolean[][] uncovered; //true if user knows; false if user doesnt
	int numBombs;
	int height;
	int width;
	boolean hasLost = false;
	MinesweeperGame(int width, int height, int numBombs) {
		board = new int[height][width];
		uncovered = new boolean[height][width];
		this.numBombs = numBombs;
		this.width = width;
		this.height = height;
		while(numBombs > 0) {
			Random rand = new Random();
			int valueX = rand.nextInt(width);
			int valueY = rand.nextInt(height);
			if (board[valueY][valueX] != -1){
				board[valueY][valueX] = -1;
				numBombs--;
			}
			for (int a = 0; a < height; a++){
				for (int b = 0; b < width; b++){ //[a][b]
					if (board[a][b] == -1) continue;
					int count = 0;
					if (a != 0 && board[a-1][b] == -1) count++; //top
					if (b != 0 && board[a][b-1] == -1) count++; //left
					if (a != 0 && b != 0 && board[a-1][b-1] == -1) count++; //top left
					if (a != height-1 && board[a+1][b] == -1) count++; //bottom
					if (b != width-1 && board[a][b+1] == -1) count++; //right
					if (b != width-1 && a != height-1 && board[a+1][b+1] == -1) count++;	//bottom right
					if (b != 0 && a != height-1 && board[a+1][b-1] == -1) count++; //bottom left
					if (a != 0 && b != width-1 && board[a-1][b+1] == -1) count++;//top right
					board[a][b] = count;
				}
			}
		}
	}

	boolean hasWon(){
		for (int a = 0; a < height; a++){
			for (int b = 0; b < width; b++){
				if (board[a][b] != -1 && !uncovered[a][b]) return false;
			}
		}
		return true;
	}

	int getHeight(){
		return height;
	}

	int getWidth(){
		return width;
	}


	boolean getHasLost(){
		return hasLost;
	}

	int getNumberOfBombs(){
		return numBombs;
	}

	void printCurrentBoard(){
		for (int a = 0; a < height; a++){
			for (int b = 0; b < width; b++){
				if (uncovered[a][b]){
					if (board[a][b] != -1) System.out.print(board[a][b]);
					else System.out.print("B");
				} 
				else System.out.print("+");
			}
			System.out.println();
		}
	}

	void printCompleteBoard(){
		for (int a = 0; a < height; a++){
			for (int b = 0; b < width; b++){
				if (board[a][b] != -1) System.out.print(board[a][b]);
				else System.out.print("B");
			}
			System.out.println();
		}
	}

	//a more hefty function
	//9 means not known yet
	int[][] getCurrentBoard(){
		int[][] ret = new int[height][width];
		for (int a = 0; a < height; a++){
			for (int b = 0; b < width; b++){
				if (uncovered[a][b]) ret[a][b] = board[a][b];
				else ret[a][b] = 9;
			}
		}
		return ret;
	}

	int reveal(int x, int y){
		if (hasLost) return -1;
		if (uncovered[y][x] == false && board[y][x] == 0) {
			uncovered[y][x] = true;
			if (y != 0) reveal(x, y-1); //top
			if (x != 0) reveal(x-1, y); //left
			if (y != 0 && x != 0) reveal(x-1, y-1); //top left
			if (y != height-1) reveal(x, y+1); //bottom
			if (x != width-1) reveal(x+1, y); //right
			if (x != width-1 && y != height-1) reveal(x+1, y+1);	//bottom right
			if (x != 0 && y != height-1) reveal(x-1, y+1); //bottom left
			if (y != 0 && x != width-1) reveal(x+1, y-1);//top right
		}
		uncovered[y][x] = true;
		if (board[y][x] == -1) hasLost = true;
		return board[y][x];
	}

	public static void main(String[] args){
		if (args.length == 3) {
			MinesweeperGame mine = new MinesweeperGame(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			mine.printCompleteBoard();
			System.out.println("----------------");
			MinesweeperSolver solve = new MinesweeperSolver(mine);
			solve.doStuff();
		}

	}
}