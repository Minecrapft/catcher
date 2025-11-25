package tile;

import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.MazeGenerator;
import entity.Player;
import entity.Entity;
public class TileManager extends MazeGenerator{

    GamePanel gp;
    Player player;
    Entity entity;
    
    
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
    
        
	    // ✅ Generate a new random maze each time the game starts
	    // Use cell division so odd/even map sizes are handled more robustly.
        
	    //int genWidth = (gp.maxWorldCol + 1) / 2;
	    //int genHeight = (gp.maxWorldRow + 1) / 2;
	    //generateMazeToFile("maps/map.txt", genWidth, genHeight);
	        
	    //loadMap("maps/map.txt");
		//placeObjectsFarFromPlayer(1, 48, 48);
		
    }

    public void getTileImage() {
        try {
        	
        	
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall.png"));
            tile[1].collision = false;

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    // ==========================================================
    // Load map from file
    // ==========================================================
  
      public void loadMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int col = 0;
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                // If file ended early, fill remaining rows with walls (1)
                if (line == null) {
                    for (col = 0; col < gp.maxWorldCol; col++) {
                        mapTileNum[col][row] = 1; // wall
                    }
                    row++;
                    continue;
                }

                String[] numbers = line.split(" ");
                // Parse available columns
                for (col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                }
                // If the generated line is shorter than expected, pad the rest with walls
                for (; col < gp.maxWorldCol; col++) {
                    mapTileNum[col][row] = 1; // wall
                }
                row++;
            }

            System.out.println("✅ Maze loaded successfully from: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
      
   // ==========================================================
   // 🎯 Place Random Objects Far from Player
   // ==========================================================
   public void placeObjectsFarFromPlayer(/* takes in object count */ int objectCount, 
		   								/* takes in player column*/ int playerCol, 
		   								/* takes in player row   */ int playerRow) {
       java.util.Random rand = new java.util.Random();
       int placed = 0;

       int minDistance = Math.min(gp.maxScreenCol, gp.maxScreenRow) / 2; 
       // Minimum distance from player (half the maze size)

       while (placed < objectCount) {
           int col = rand.nextInt(gp.maxWorldCol);
           int row = rand.nextInt(gp.maxWorldRow);

           // Only place object on path (grass)
           if (mapTileNum[col][row] == 0) {
               // Compute distance from player using Manhattan distance
               int distance = Math.abs(col - playerCol) + Math.abs(row - playerRow);

               if (distance > minDistance) {
                   mapTileNum[col][row] = 2; // 2 = the gate
                   placed++;
               }
           }
       }

       System.out.println("🎁 Placed " + objectCount + " objects far from player!");
   }


      
    // ==========================================================
    // Draw the map
    // ==========================================================
    public void draw(Graphics2D g2) {
    	
        int worldCol = 0;
        int worldRow = 0;
        int x = 0;
        int y = 0;
        
        
        while (worldCol < gp.maxScreenCol && worldRow < gp.maxScreenRow) {
        	
        	
            int tileNum = mapTileNum[worldCol][worldRow];
            	
            g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            worldCol++;


            if (worldCol == gp.maxScreenCol) {
	            	worldCol = 0;
	            	x = 0;
	            	worldRow++;
	            	y += gp.tileSize;

            }
        }
        
    }
}
