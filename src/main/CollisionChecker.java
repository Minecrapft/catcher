package main;

import entity.Entity;
import entity.Player;


public class CollisionChecker {
	
	GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	

	}
	
	public boolean checkTileCollisionX(Entity entity, int velocityX) {
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityTopRow = entityTopWorldY / gp.tileSize;
		int entityBottomRow = entityBottomWorldY / gp.tileSize;
		int tileNum1 = 0, tileNum2 = 0;
		
		if (velocityX > 0) {
			// Moving right
			int entityRightCol = (entityRightWorldX + velocityX) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
		} else if (velocityX < 0) {
			// Moving left
			int entityLeftCol = (entityLeftWorldX + velocityX) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
		}
		
		// Check for gate/portal
		checkForGate(entity, tileNum1, tileNum2);
		
		return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
	}
	
	public boolean checkTileCollisionY(Entity entity, int velocityY) {
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX / gp.tileSize;
		int entityRightCol = entityRightWorldX / gp.tileSize;
		int tileNum1 = 0, tileNum2 = 0;
		
		if (velocityY > 0) {
			// Moving down
			int entityBottomRow = (entityBottomWorldY + velocityY) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
		} else if (velocityY < 0) {
			// Moving up
			int entityTopRow = (entityTopWorldY + velocityY) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
		}
		
		// Check for gate/portal
		checkForGate(entity, tileNum1, tileNum2);
		
		return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
	}
	
	private void checkForGate(Entity entity, int tileNum1, int tileNum2) {
		// Check if player is inside the gate or portal
		if (tileNum1 == 2 || tileNum2 == 2) {
			// Gate: generate new maze and reload
			gp.tileM.generateMazeToFile("maps/map.txt", gp.maxWorldCol/2, gp.maxWorldRow/2);
			gp.tileM.loadMap("maps/map.txt");

			// If the entity is the player, reset its world position to the starting tile
			if (entity instanceof Player) {
				// start position is tile (2,2) => world coord = tileSize * 2
				entity.worldX = gp.tileSize * 2;
				entity.worldY = gp.tileSize * 2;
				entity.playerPosX = entity.worldX / gp.tileSize;
				entity.playerPosY = entity.worldY / gp.tileSize;
			}

			// place objects far from the player's (new) position
			gp.tileM.placeObjectsFarFromPlayer(1, entity.playerPosX, entity.playerPosY);
			entity.score++;
			System.out.println("Score: " + entity.score);
		}
	}
	
	public void checkTile(Entity entity) {
		
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize;
		int entityRightCol = entityRightWorldX/gp.tileSize;
		int entityTopRow = entityTopWorldY/gp.tileSize;
		int entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		int tileNum1 = 0, tileNum2 = 0;
		
		switch(entity.direction) {
		case "up":
			entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				
			}
			
			break;
			
		case "down":
			entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				
			}
			break;
			
		case "left":
			entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				
			}
			break;
			
		case "right":
			entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				entity.collisionOn = true;
				
			}
			break;
		
		}
		
		checkForGate(entity, tileNum1, tileNum2);

	}

}
