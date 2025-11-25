package main;

import java.io.File;
import java.io.PrintWriter;

public class MazeGenerator {
    
    // ==========================================================
    // 🌀 Maze Generation Using Recursive Backtracking (DFS)
    // ==========================================================
    public void generateMazeToFile(String filePath, int width, int height) {
        // ✅ Ensure the maze dimensions are odd (important for proper path carving)
        if (width % 2 == 0) width--;
        if (height % 2 == 0) height--;

        // Create a 2D array to represent the maze
        // 1 = wall, 0 = path
        int[][] maze = new int[height][width];
        java.util.Random rand = new java.util.Random();

        // 🔹 Step 1: Fill the maze completely with walls (1s)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = 1;
            }
        }

        // 🔹 Step 2: Start carving paths recursively using DFS from (1,1)
        carvePath(1, 1, maze, rand);

        // 🔹 Step 3: Make sure start and end positions are open (path = 0)
        maze[1][1] = 0; // Start point
        maze[height - 2][width - 2] = 0; // End point

        // 🔹 Step 4: Create a larger "expanded" maze
        // Each path cell (0) and wall (1) becomes a 2x2 block for smoother look
        int expandedHeight = height * 2;
        int expandedWidth = width * 2;
        int[][] expanded = new int[expandedHeight][expandedWidth];

        // Convert original maze into expanded version
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x] == 1) {
                    // Wall becomes a 2x2 block of walls
                    expanded[y * 2][x * 2] = 1;
                    expanded[y * 2][x * 2 + 1] = 1;
                    expanded[y * 2 + 1][x * 2] = 1;
                    expanded[y * 2 + 1][x * 2 + 1] = 1;
                } else {
                    // Path becomes a 2x2 block of paths
                    expanded[y * 2][x * 2] = 0;
                    expanded[y * 2][x * 2 + 1] = 0;
                    expanded[y * 2 + 1][x * 2] = 0;
                    expanded[y * 2 + 1][x * 2 + 1] = 0;
                }
            }
        }

        // 🔹 Step 5: Ensure the directory "maps" exists (create if missing)
        File dir = new File("maps");
        if (!dir.exists()) dir.mkdirs();

        // 🔹 Step 6: Write the expanded maze data to a text file
        try (PrintWriter out = new PrintWriter(filePath)) {
            for (int y = 0; y < expandedHeight; y++) {
                for (int x = 0; x < expandedWidth; x++) {
                    // Print each cell (0 or 1)
                    out.print(expanded[y][x]);
                    // Add a space between numbers (except last column)
                    if (x < expandedWidth - 1) out.print(" ");
                }
                // Move to the next line after each row
                out.println();
            }
            System.out.println("✅ Maze with 2x2 paths generated to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 🧩 Recursive DFS Path Carving Function
    // ==========================================================
    private void carvePath(int x, int y, int[][] maze, java.util.Random rand) {
        // List of directions (0=up, 1=right, 2=down, 3=left)
        int[] dirs = {0, 1, 2, 3};
        // Randomize direction order to create a unique maze
        shuffleArray(dirs, rand);

        // Try moving in each random direction
        for (int dir : dirs) {
            int dx = 0, dy = 0;
            switch (dir) {
                case 0 -> dy = -1; // move up
                case 1 -> dx = 1;  // move right
                case 2 -> dy = 1;  // move down
                case 3 -> dx = -1; // move left
            }

            // Calculate the target cell 2 steps away
            int nx = x + dx * 2;
            int ny = y + dy * 2;

            // ✅ Check boundaries: ensure we don't carve outside maze
            if (ny > 0 && ny < maze.length - 1 && nx > 0 && nx < maze[0].length - 1) {
                // If target cell is still a wall, carve a path to it
                if (maze[ny][nx] == 1) {
                    // Remove wall between current and next cell
                    maze[y + dy][x + dx] = 0;
                    // Open the next cell
                    maze[ny][nx] = 0;
                    // Recursively carve deeper
                    carvePath(nx, ny, maze, rand);
                }
            }
        }
    }

    // ==========================================================
    // 🔀 Helper: Shuffle array of directions (randomize movement)
    // ==========================================================
    private void shuffleArray(int[] array, java.util.Random rand) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            // Swap elements
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
