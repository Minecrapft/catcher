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

        // === MAP BOUNDARIES ===
        int maxCol = gp.maxWorldCol - 1;
        int maxRow = gp.maxWorldRow - 1;

        if (velocityX > 0) {
            // moving right
            int entityRightCol = (entityRightWorldX + velocityX) / gp.tileSize;

            // Stop player at right edge
            if (entityRightCol > maxCol) return true;

            tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

        } else if (velocityX < 0) {
            // moving left
            int entityLeftCol = (entityLeftWorldX + velocityX) / gp.tileSize;

            // Stop player at left edge
            if (entityLeftCol < 0) return true;

            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
        }

        // Check for gate
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

        // === MAP BOUNDARIES ===
        int maxCol = gp.maxWorldCol - 1;
        int maxRow = gp.maxWorldRow - 1;

        if (velocityY > 0) {
            // moving down
            int entityBottomRow = (entityBottomWorldY + velocityY) / gp.tileSize;

            // Stop player at bottom edge
            if (entityBottomRow > maxRow) return true;

            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

        } else if (velocityY < 0) {
            // moving up
            int entityTopRow = (entityTopWorldY + velocityY) / gp.tileSize;

            // Stop player at top edge
            if (entityTopRow < 0) return true;

            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
        }

        // Check for gate
        checkForGate(entity, tileNum1, tileNum2);

        return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
    }

    private void checkForGate(Entity entity, int tileNum1, int tileNum2) {
        // Check if touching gate tile
        if (tileNum1 == 2 || tileNum2 == 2) {

            gp.tileM.generateMazeToFile("maps/map.txt", gp.maxWorldCol / 2, gp.maxWorldRow / 2);
            gp.tileM.loadMap("maps/map.txt");

            if (entity instanceof Player) {
                // Reset player to start
                entity.worldX = gp.tileSize * 2;
                entity.worldY = gp.tileSize * 2;
                entity.playerPosX = entity.worldX / gp.tileSize;
                entity.playerPosY = entity.worldY / gp.tileSize;
            }

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

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1 = 0, tileNum2 = 0;

        switch (entity.direction) {
        case "up":
            entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
            if (entityTopRow < 0) { // Top map boundary
                entity.collisionOn = true;
                return;
            }
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
            break;

        case "down":
            entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
            if (entityBottomRow > gp.maxWorldRow - 1) { // Bottom boundary
                entity.collisionOn = true;
                return;
            }
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
            break;

        case "left":
            entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
            if (entityLeftCol < 0) { // Left boundary
                entity.collisionOn = true;
                return;
            }
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
            break;

        case "right":
            entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
            if (entityRightCol > gp.maxWorldCol - 1) { // Right boundary
                entity.collisionOn = true;
                return;
            }
            tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
            break;
        }

        // Check gate
        checkForGate(entity, tileNum1, tileNum2);
    }
}
