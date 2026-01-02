# CATcher Game - Developer Video Presentation Script

## Developer Team:
- **KAREN**: Explains overall architecture and game design
- **DIANA**: Explains game mechanics and player interaction  
- **CABAHUG**: Explains code implementation and technical details

---

**[INTRO - Title Card]**
## "CATcher Game: A Complete Java Game Development Walkthrough"
**Presented by: Cabahug, Diana, and Karen**

## PART 1: INTRODUCTION TO CATCHER

**[SCREEN SHOWS: Game Gameplay Demo]**

**KAREN:** "Hello everyone, and welcome to our CATcher Game Development Presentation. I'm Karen, one of the developers of this project. Today, my team and I - Diana and Cabahug - will walk you through every aspect of our game, from the architecture to the code to the mechanics."

**[SCREEN SHOWS: Game title and main menu]**

**DIANA:** "CATcher is a 2D top-down arcade-style game built entirely in Java. The concept is simple: you control a character at the bottom of the screen, and your goal is to catch falling food items while avoiding bombs. The longer you survive, the higher your score and the harder the game becomes."

**[SCREEN SHOWS: Gameplay footage - catching food, avoiding bombs]**

**CABAHUG:** "From a technical perspective, this game demonstrates fundamental game development concepts: the game loop, collision detection, state management, input handling, and audio integration. Everything you see is implemented in pure Java using Swing for graphics and javax.sound.sampled for audio."

---

## PART 2: PROJECT STRUCTURE AND FILE BREAKDOWN

**[SCREEN SHOWS: File directory structure diagram]**

**KAREN:** "Let's start by understanding the file structure. Our project is organized into three main directories: `src/` for source code, `bin/` for compiled classes, and `res/` for resources like images and sounds."

**[SCREEN SHOWS: src/ folder expanded]**

**DIANA:** "In the source code, we have three main packages: `main/`, `entity/`, and `tile/`. The `main/` package contains all the core game logic and managers. The `entity/` package contains our game objects like Player, FallingEntity, and our sound system."

**[SCREEN SHOWS: Detailed file list with descriptions]**

**CABAHUG:** "Here are the critical files and what they do:

**Core Game Loop:**
- `Main.java` - Entry point, creates the game window
- `GamePanel.java` - Main game container, houses the game loop and all components
- `GameStateManager.java` - Tracks game states (MENU, GAME, PAUSE, GAMEOVER)

**Player and Entities:**
- `Player.java` - Player character with position, lives, and score
- `FallingEntity.java` - Falling game objects (bombs and food)
- `GameplayManager.java` - Handles spawning, collisions, and game logic

**Input and UI:**
- `InputHandler.java` - Processes keyboard and mouse input
- `KeyHandler.java` - Tracks keyboard state
- `MouseHandler.java` - Tracks mouse position and clicks
- `UIRenderer.java` - Renders all menus, buttons, and HUD
- `ImageButton.java` - Interactive button with hover effects and sounds

**Audio System:**
- `SoundHandler.java` - Low-level audio management
- `sounds.java` - Game-specific sound organization"

---

## PART 3: THE GAME LOOP - THE HEARTBEAT OF THE GAME

**[SCREEN SHOWS: Game loop diagram with arrows]**

**KAREN:** "The game loop is the most critical component. It runs 60 times per second, and each iteration performs three main steps: input processing, game logic update, and rendering."

**[SCREEN SHOWS: Pseudocode]**

**DIANA:** "Here's what happens during each frame:

1. **Input Processing**: Listen for mouse movement and clicks, update player position
2. **Update Phase**: Move all falling entities down, check for collisions, update score/lives
3. **Render Phase**: Draw the background, player, falling entities, and HUD
4. **Sound**: Play any audio that was triggered during update phase

All of this happens 60 times per second, about every 16.67 milliseconds."

**[SCREEN SHOWS: Code with highlights]**

**CABAHUG:** "Here's the actual code for the game loop in `GamePanel.java`:

```java
private void run() {
    // Get the current time in nanoseconds
    long lastTime = System.nanoTime();
    // Calculate the target frame time: 1 second / 60 FPS = 16.67 ms
    final long FRAME_TIME = 1_000_000_000 / 60;  // 60 FPS target
    
    // Main game loop - runs continuously while game is running
    while (running) {
        // Get current time to calculate elapsed time since last frame
        long currentTime = System.nanoTime();
        // Calculate how much time has passed since the last frame
        long deltaTime = currentTime - lastTime;
        
        // Only update game if enough time has passed (to maintain 60 FPS)
        if (deltaTime >= FRAME_TIME) {
            updateGame();      // Step 1: Update logic (move entities, check collisions)
            render();          // Step 2: Render graphics (draw everything)
            lastTime = currentTime;  // Reset timer for next frame
        }
    }
}
```

The key is checking if enough time has passed since the last frame. This ensures the game runs at exactly 60 FPS regardless of the computer's speed."

---

## PART 4: ENTITY SYSTEM - FOOD VS BOMBS

**[SCREEN SHOWS: Animation of falling entities]**

**KAREN:** "Entities are the core of our gameplay. Each entity is a falling object that starts at the top of the screen with a random X position and falls downward due to gravity. Every entity is classified as either food or a bomb."

**[SCREEN SHOWS: Side-by-side comparison of food and bomb]**

**DIANA:** "Let me explain the mechanics:

**FOOD ITEMS:**
- Give you points when caught (10-50 points depending on type)
- Penalty of 20 points if they fall off the bottom without being caught
- Cannot damage you - they're safe to approach but have a cost if missed

**BOMBS:**
- ALWAYS damages you
- 100 points lost AND 1 life lost
- The key mechanic: catching doesn't save you from bombs
- Special explosion sound plays when hit"

**[SCREEN SHOWS: Code snippet]**

**KAREN:** "In `FallingEntity.java`, we track what type of entity it is:

```java
public class FallingEntity {
    // Tracks what type of entity this is (0=bomb, 1-5=different food types)
    private int imageType;  // 0 = bomb, 1-5 = food types
    
    // Check if this entity is a bomb (imageType 0)
    public boolean isBomb() {
        // Returns true only if imageType is 0 (bomb)
        return imageType == 0;
    }
    
    // Get the point value for catching this entity
    public int getPoints() {
        // Switch statement checks the imageType and returns appropriate points
        switch(imageType) {
            case 1: return 10;  // Food type 1 (Apple) worth 10 points
            case 2: return 20;  // Food type 2 (Cherry) worth 20 points
            case 3: return 15;  // Food type 3 (Banana) worth 15 points
            default: return 0;  // Bomb (type 0) returns 0 points
        }
    }
}
```

And in `GameplayManager.java`, we handle each type differently during collision:

```java
// Check if the caught entity is a bomb
if (entity.isBomb()) {
    // BOMBS ALWAYS DAMAGE YOU!
    gamePanel.player.loseLife();               // Remove 1 life (triggers GAMEOVER if 0 left)
    gamePanel.player.losePoints(100);          // Subtract 100 points from score
    gamePanel.gameSound.playBombHitSound();    // Play explosion sound effect
} else {
    // Entity is food - safe to catch, gives points
    gamePanel.player.score += entity.getPoints();  // Add points to player's score
    gamePanel.gameSound.playCoinCollectSound();    // Play collection sound effect
}
```"

---

## PART 5: COLLISION DETECTION - THE PHYSICS SYSTEM

**[SCREEN SHOWS: Diagrams of collision boxes overlapping]**

**KAREN:** "Collision detection is how the game determines if things are touching. We need to check three types of collisions every frame:

1. Did the player's catching net touch an entity?
2. Did a bomb naturally fall down and hit the player?
3. Did an entity fall off the bottom of the screen?"

**[SCREEN SHOWS: Visual representation of collision boxes and rectangles]**

**DIANA:** "We use Java's `Rectangle` class for collision detection. Each game object has a collision rectangle, and we use the `intersects()` method to check if two rectangles overlap. If they do, we have a collision."

**[SCREEN SHOWS: Code with step-by-step walkthrough]**

**KAREN:** "Here's how collision detection works in `GameplayManager.updateEntities()`:

```java
// Create an Iterator to safely loop through and remove entities
Iterator<FallingEntity> iterator = gamePanel.fallingEntities.iterator();
// Loop through all falling entities
while (iterator.hasNext()) {
    // Get the next entity from the list
    FallingEntity entity = iterator.next();
    // Move entity down the screen based on elapsed time
    entity.update(gameTime);  // Move entity down by speed * deltaTime
    
    // COLLISION TYPE 1: Check if player's catching net touched this entity
    if (gamePanel.player.isCatching && entity.checkCatch(gamePanel.player)) {
        // Net is active AND entity's collision box overlaps with net
        if (entity.isBomb()) {
            // Caught a bomb - bombs damage even when caught!
            gamePanel.player.losePoints(100);               // Lose 100 points
            gamePanel.player.loseLife();                    // Lose 1 life
            gamePanel.gameSound.playBombHitSound();         // Play explosion sound
        } else {
            // Caught food - safe and gives points
            gamePanel.player.score += entity.getPoints();   // Add points to score
            gamePanel.gameSound.playCoinCollectSound();     // Play collect sound
        }
        // Remove entity from game since it's been caught
        iterator.remove();  // Remove entity from game
    }
    // COLLISION TYPE 2: Check if bomb naturally fell on player (not caught)
    else if (entity.isBomb() && entity.checkCollision(gamePanel.player)) {
        // Bomb's collision box overlapped with player's collision box
        gamePanel.player.losePoints(100);           // Lose 100 points
        gamePanel.player.loseLife();                // Lose 1 life
        gamePanel.gameSound.playBombHitSound();     // Play explosion sound
        // Remove the bomb entity from game
        iterator.remove();
    }
    // COLLISION TYPE 3: Check if entity fell off the bottom of screen
    else if (entity.fellOffScreen()) {
        // Entity's Y position is now below screen height
        if (!entity.isBomb()) {
            // Only penalize if it's FOOD that fell off (not bombs)
            gamePanel.player.losePoints(20);                // Lose 20 points for missing food
            gamePanel.gameSound.playFoodCollectSound();     // Play sound for falling off
        }
        // Remove entity from game since it's off screen
        iterator.remove();
    }
}
```

**Important Note:** We use an `Iterator` instead of traditional ArrayList removal because removing items during iteration can cause bugs. Iterator is also more efficient - O(1) per removal instead of O(n)."

---

## PART 6: GAME STATE MANAGEMENT - MENU, PLAY, PAUSE, GAME OVER

**[SCREEN SHOWS: State transition diagram]**

**KAREN:** "The game has five distinct states, and players transition between them as they play. State management is crucial for controlling what happens at each moment."

**[SCREEN SHOWS: Flowchart with arrows showing state transitions]**

**DIANA:** "Let me walk through the states:

**MENU:** Initial state when the game starts. Player sees title screen and Start button.
- Transition: Click Start → GAME state

**GAME:** Active gameplay. Player is catching and avoiding.
- Transitions: Press ESC → PAUSE state | Lose all lives → GAMEOVER state

**PAUSE:** Game is paused. Can resume or return to menu.
- Transitions: Press ESC → GAME state | Click Menu → MENU state

**GAMEOVER:** Player lost all lives. Can retry or return to menu.
- Transitions: Click Retry → GAME state (new game) | Click Menu → MENU state

**HIGHSCORE:** Optional state to show high scores (for future expansion)"

**[SCREEN SHOWS: Code showing state enum and transitions]**

**KAREN:** "In `GameStateManager.java`, we define the states as an enum:

```java
// Enum defines all possible game states
public enum GameState {
    MENU,        // Main menu screen state
    GAME,        // Active gameplay state
    PAUSE,       // Paused during gameplay state
    GAMEOVER,    // Game over screen state
    HIGHSCORE    // High score display state (for future use)
}
```

Every frame in `GamePanel.render()`, we check the current state and render accordingly:

```java
public void render() {
    // Check which game state we're in and render accordingly
    switch (gameStateManager.getState()) {
        case MENU:
            // Render main menu with start button
            uiRenderer.drawMainMenu();
            break;
            
        case GAME:
            // Render active gameplay
            drawGameplay();                          // Draw game background and objects
            uiRenderer.drawHUD();                   // Draw hearts, score, level display
            break;
            
        case PAUSE:
            // Render pause menu overlay on top of game
            uiRenderer.drawPauseMenu();             // Draw with pause overlay
            break;
            
        case GAMEOVER:
            // Render game over screen with final score and buttons
            uiRenderer.drawGameOverScreen();        // Draw with game over buttons
            break;
    }
}
```

And in `InputHandler.java`, we handle state transitions:

```java
// Check if ESC key is currently pressed
if (keyHandler.isPressedKey(KeyEvent.VK_ESCAPE)) {
    // Get the current game state to decide what to do
    GameState current = gameStateManager.getState();
    // If we're actively playing, pause the game
    if (current == GameState.GAME) {
        gameStateManager.setState(GameState.PAUSE);  // Change state to PAUSE
    } 
    // If game is already paused, resume playing
    else if (current == GameState.PAUSE) {
        gameStateManager.setState(GameState.GAME);   // Change state back to GAME
    }
}
```"

---

## PART 7: INPUT SYSTEM - HOW PLAYERS CONTROL THE GAME

**[SCREEN SHOWS: Input flow diagram]**

**KAREN:** "Input handling is how the game receives and responds to player actions. We have two types of input: keyboard and mouse."

**[SCREEN SHOWS: Keyboard and mouse inputs side by side]**

**DIANA:** "Here's what inputs do what:

**MOUSE INPUT:**
- **Mouse Movement**: Player character automatically follows the mouse cursor horizontally
- **Mouse Click**: Activates the catching net (net swings upward)

**KEYBOARD INPUT:**
- **ESC Key**: Toggle pause (GAME ↔ PAUSE)
- **SPACE Key**: Menu selection (activate buttons in MENU and GAMEOVER states)

These inputs vary based on the current game state. Different states handle the same key differently."

**[SCREEN SHOWS: Code showing InputHandler logic]**

**CABAHUG:** "We have separate classes for tracking input:

**MouseHandler.java** tracks mouse position and clicks:
```java
public class MouseHandler extends MouseAdapter {
    // Store current mouse X position
    private int mouseX = 0;
    // Store current mouse Y position
    private int mouseY = 0;
    // Track if mouse button is currently pressed
    private boolean pressed = false;
    
    // Called when mouse is moved
    @Override
    public void mouseMoved(MouseEvent e) {
        // Update mouseX with new X coordinate from event
        mouseX = e.getX();
        // Update mouseY with new Y coordinate from event
        mouseY = e.getY();
    }
    
    // Called when mouse button is pressed down
    @Override
    public void mousePressed(MouseEvent e) {
        // Set pressed flag to true
        pressed = true;
        // Tell player to start catching (swing the net upward)
        gamePanel.player.startCatching();  // Net swings up
    }
}
```

**KeyHandler.java** tracks keyboard state:
```java
public class KeyHandler extends KeyAdapter {
    // Array to track which keys are currently pressed (256 possible key codes)
    private boolean[] pressed = new boolean[256];
    
    // Called when a key is pressed down
    @Override
    public void keyPressed(KeyEvent e) {
        // Get the key code and mark it as pressed in our array
        pressed[e.getKeyCode()] = true;
    }
    
    // Called when a key is released
    @Override
    public void keyReleased(KeyEvent e) {
        // Get the key code and mark it as not pressed
        pressed[e.getKeyCode()] = false;
    }
    
    // Check if a specific key is currently pressed
    public boolean isPressedKey(int keyCode) {
        // Return whether this key code is marked as pressed
        return pressed[keyCode];
    }
}
```

**InputHandler.java** combines both and implements game logic:
```java
public void handleGameInput() {
    // Automatically move player to follow mouse cursor horizontally
    gamePanel.player.setX(mouseHandler.getMouseX());
    
    // Check if ESC key is pressed to toggle pause
    if (keyHandler.isPressedKey(KeyEvent.VK_ESCAPE)) {
        // Get current game state
        if (gameStateManager.getState() == GameState.GAME) {
            // Currently playing - pause the game
            gameStateManager.setState(GameState.PAUSE);
            // Stop background music when paused
            gamePanel.gameSound.stopBackgroundMusic();
        } else if (gameStateManager.getState() == GameState.PAUSE) {
            // Currently paused - resume the game
            gameStateManager.setState(GameState.GAME);
            // Restart background music when resuming
            gamePanel.gameSound.playBackgroundMusic();
        }
    }
}
```"

---

## PART 8: AUDIO SYSTEM - SOUND EFFECTS AND MUSIC

**[SCREEN SHOWS: Audio system architecture diagram]**

**CABAHUG:** "Audio is a crucial part of the gaming experience. We created a two-layer audio system: a low-level sound engine and a game-specific sound manager."

**[SCREEN SHOWS: Sound playing at different game events]**

**DIANA:** "Our game has audio for almost every event:

**BACKGROUND MUSIC:**
- Plays continuously during gameplay
- Loops seamlessly

**SOUND EFFECTS:**
- **Catching Food**: Satisfying 'collect' sound
- **Bomb Hit**: Explosion effect sound
- **Button Hover**: Subtle UI feedback sound
- **Button Click**: Click confirmation sound
- **Losing Life**: Damage/hurt sound
- **Game Over**: Game over music/sound

All of these provide immediate audio feedback to the player, making the game feel more responsive and satisfying."

**[SCREEN SHOWS: Code and file structure]**

**CABAHUG:** "The audio system is implemented in two classes:

**SoundHandler.java** - The low-level engine:
```java
public class SoundHandler {
    // Map to store sound file paths (key = sound name, value = file path)
    private Map<String, String> soundFiles = new HashMap<>();
    
    // Register a sound with a name and file path
    public void loadSound(String soundName, String filePath) {
        // Store the file path associated with this sound name
        soundFiles.put(soundName, filePath);  // Store path, not the stream
    }
    
    // Play a sound once by name
    public void playSound(String soundName) {
        // Look up the file path for this sound name
        String filePath = soundFiles.get(soundName);
        // If sound not found, exit early
        if (filePath == null) return;
        
        try {
            // Create fresh AudioInputStream from the file
            // This is essential - we create a new stream each time we play
            AudioInputStream audioInputStream = 
                AudioSystem.getAudioInputStream(new File(filePath));
            
            // Get a new Clip object to play the sound
            Clip clip = AudioSystem.getClip();
            // Load the audio data into the clip
            clip.open(audioInputStream);
            // Start playing the sound immediately
            clip.start();
        } catch (Exception e) {
            // If something goes wrong, print the error
            e.printStackTrace();
        }
    }
    
    // Loop a sound continuously (for background music)
    public void loopSound(String soundName) {
        // Same as playSound but set clip to loop continuously
        // Creates fresh stream and sets Clip.LOOP_CONTINUOUSLY
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
```

**The key insight:** We store **file paths**, not cached audio streams. This is because AudioInputStream can't be reset. Every time we play a sound, we create a fresh stream from the file. This prevents errors and allows the same sound to play overlapping.

**sounds.java** - The game layer:
```java
public class sounds {
    // Reference to the sound handler that actually plays sounds
    private SoundHandler soundHandler;
    
    // Constructor - takes a SoundHandler instance
    public sounds(SoundHandler handler) {
        // Store the handler reference
        this.soundHandler = handler;
        // Load all game sounds
        loadAllSounds();
    }
    
    // Load all game sounds and associate them with file paths
    private void loadAllSounds() {
        // Load bomb hit sound effect
        soundHandler.loadSound("bomb_hit", "res/sounds/bomb.WAV");
        // Load food collection sound effect
        soundHandler.loadSound("food_collect", "res/sounds/eating(1).WAV");
        // Load button click sound effect
        soundHandler.loadSound("button_click", "res/sounds/click.WAV");
        // Load button hover sound effect
        soundHandler.loadSound("button_hover", "res/sounds/selecting2.WAV");
        // Load background music that will loop
        soundHandler.loadSound("background_music", "res/sounds/backgroundmusic.WAV");
    }
    
    // Play bomb explosion sound
    public void playBombHitSound() {
        // Tell sound handler to play the bomb_hit sound
        soundHandler.playSound("bomb_hit");
    }
    
    // Play food/coin collection sound
    public void playCoinCollectSound() {
        // Tell sound handler to play the food_collect sound
        soundHandler.playSound("food_collect");
    }
}
```

When game events happen, we call these methods. For example, in GameplayManager:
```java
// When game events happen, we call these methods. For example, in GameplayManager:
// Check if the caught entity is a bomb
if (entity.isBomb()) {
    // It's a bomb - always causes damage
    gamePanel.player.loseLife();
    // Call the bomb hit sound method
    gamePanel.gameSound.playBombHitSound();  // ← Sound plays immediately
}
```"

---

## PART 9: USER INTERFACE - MENUS, BUTTONS, AND HUD

**[SCREEN SHOWS: Menu screenshots and button interactions]**

**KAREN:** "The UI is responsible for displaying menus and the heads-up display (HUD) during gameplay. It's all managed by UIRenderer and ImageButton classes."

**[SCREEN SHOWS: Main Menu, Pause Menu, Game Over Screen side by side]**

**DIANA:** "The UI elements are:

**MAIN MENU:**
- Title text \"CATcher\"
- Start Game button

**GAMEPLAY HUD:**
- Hearts display (shows remaining lives)
- Score display (current points)
- Difficulty level indicator

**PAUSE MENU:**
- Semi-transparent overlay
- Resume button
- Return to Menu button

**GAME OVER SCREEN:**
- Final score display
- High score comparison
- Retry button (start new game)
- Menu button (return to main menu)"

**[SCREEN SHOWS: Button hover effect animation]**

**CABAHUG:** "In `UIRenderer.java`, all buttons are ImageButton objects:

```java
public class UIRenderer {
    // Create button objects for the start game button
    private ImageButton startButton;
    // Create button object for pause resume button
    private ImageButton pauseResumeButton;
    // Create button object for retry button
    private ImageButton retryButton;
    // Create button object for return to menu button
    private ImageButton menuButton;
    
    // Initialize all button images and positions
    private void loadButtonImages() {
        // Create start button at position (200, 300) with label and sound handler
        startButton = new ImageButton(200, 300, "Start Game", soundHandler);
        // Create pause resume button at position (200, 250)
        pauseResumeButton = new ImageButton(200, 250, "Resume", soundHandler);
        // Create retry button at position (200, 350)
        retryButton = new ImageButton(200, 350, "Retry", soundHandler);
        // Create menu button at position (200, 400)
        menuButton = new ImageButton(200, 400, "Menu", soundHandler);
    }
    
    // Render the main menu screen
    public void drawMainMenu() {
        // Draw background and title of menu
        // Update button hover state based on current mouse position
        startButton.update(mouseX, mouseY);
        // Draw the start button on screen
        startButton.draw(graphics);
        
        // Check if mouse was clicked and if it was over the start button
        if (mouseClicked && startButton.isMouseOver(...)) {
            // Player clicked start button - start the game
            gameStateManager.setState(GameState.GAME);
        }
    }
}
```

**ImageButton.java** - Enhanced with hover effects and sound:
```java
public class ImageButton {
    // X position of the button on screen
    private int x, y;
    // Store original position (used to calculate hover offset)
    private int originalX, originalY;
    // Track whether mouse is currently over this button
    private boolean isHovered = false;
    // Amount to move button up when hovered
    private static final int HOVER_OFFSET = 5;
    // Reference to sound handler for playing button sounds
    private SoundHandler soundHandler;
    
    // Update button state each frame (check hover, play sounds)
    public void update(int mouseX, int mouseY) {
        // Check if mouse is currently over this button
        isHovered = isMouseOver(mouseX, mouseY);
        
        // If newly hovered (wasn't hovered last frame), play hover sound
        if (isHovered && !wasHovered) {
            playHoverSound();  // Play sound when mouse first enters
        }
        // Remember hover state for next frame
        wasHovered = isHovered;
    }
    
    // Draw the button on the screen
    public void draw(Graphics2D g) {
        // If button is hovered, move it up 5 pixels
        int drawY = isHovered ? (y - HOVER_OFFSET) : y;
        
        // Draw button image at the calculated position
        g.drawImage(buttonImage, x, drawY, null);
        
        // Draw button text on top of image
        g.drawString(text, x + 20, drawY + 30);
    }
    
    // Play sound when mouse hovers over button
    private void playHoverSound() {
        // Check that sound handler exists before trying to play sound
        if (soundHandler != null) {
            // Tell sound handler to play the button_hover sound
            soundHandler.playSound("button_hover");
        }
    }
    
    // Play sound when button is clicked
    public void playClickSound() {
        // Check that sound handler exists before trying to play sound
        if (soundHandler != null) {
            // Tell sound handler to play the button_click sound
            soundHandler.playSound("button_click");
        }
    }
}
```

The buttons have visual feedback: they move up 5 pixels when you hover over them, and they play sound effects. This makes the UI feel responsive and engaging."

---

## PART 10: GAME MECHANICS - LIVES, SCORING, AND DIFFICULTY

**[SCREEN SHOWS: Score and lives tracking]**

**KAREN:** "The core game mechanics drive the player's progression and challenge. Let me break down how scoring, lives, and difficulty work together."

**[SCREEN SHOWS: Infographic showing point values and life mechanics]**

**DIANA:** "Here's the mechanics:

**LIVES SYSTEM:**
- Start with 3 lives (represented as hearts)
- Each bomb hit: lose 1 life
- When all 3 lives are gone: GAME OVER state

**SCORING SYSTEM:**
- Catch Food: +10 to +50 points (depending on type)
- Miss Food (falls off): -20 points
- Catch Bomb: -100 points (plus lose 1 life)
- Hit Bomb: -100 points (plus lose 1 life)
- Score never goes below 0

**DIFFICULTY SYSTEM:**
- Starts at level 1
- Increases over time as you survive
- Higher difficulty = faster falling entities
- Difficulty multiplier: 1.0 + (gameTime / 10000)
  - After 10 seconds: 1.1x speed
  - After 30 seconds: 1.3x speed
  - After 60 seconds: 1.6x speed"

**[SCREEN SHOWS: Code showing scoring logic]**

**CABAHUG:** "In `Player.java`, we track lives and score:

```java
public class Player {
    // Track the number of remaining lives (hearts)
    private int lives = 3;
    // Track the current score
    private int score = 0;
    
    // Called when player takes damage
    public void loseLife() {
        // Check if player has lives remaining
        if (lives > 0) {
            // Decrease lives by 1
            lives--;
            // If no lives left, game is over
            if (lives == 0) {
                // Change game state to GAMEOVER
                gamePanel.gameStateManager.setState(GameState.GAMEOVER);
            }
        }
    }
    
    // Called when points should be deducted from score
    public void losePoints(int points) {
        // Subtract points from score, but don't allow negative scores
        score = Math.max(0, score - points);  // Can't go below 0
    }
    
    // Called when points should be added to score
    public void addPoints(int points) {
        // Add points to the score
        score += points;
    }
    
    // Getter - return current score
    public int getScore() {
        return score;
    }
    
    // Getter - return remaining lives
    public int getLives() {
        return lives;
    }
}
```

And in `GameplayManager.java`, difficulty is calculated:

```java
// Calculate current difficulty based on how long player has been playing
private float calculateDifficulty() {
    // Difficulty increases with time
    // Formula: 1.0 + (gameTime / 10000)
    return 1.0f + (gameTime / 10000.0f);
}

// Spawn new falling entities
private void spawnEntities() {
    // Get current difficulty multiplier (increases over time)
    float difficulty = calculateDifficulty();
    // Calculate falling speed: base 2 pixels/frame multiplied by difficulty
    float fallSpeed = 2.0f * difficulty;  // Base 2 pixels/frame * multiplier
}
```

During collision detection, we apply the scoring:
```java
// Check if caught entity is a bomb or food
if (entity.isBomb()) {
    // Caught a bomb - deduct points and lose life
    gamePanel.player.losePoints(100);
    // Lose 1 life (triggers GAMEOVER if lives == 0)
    gamePanel.player.loseLife();  // Triggers GAMEOVER if lives == 0
} else {
    // Caught food - add points to score
    gamePanel.player.score += entity.getPoints();
}

// Check if entity fell off the bottom of screen
if (entity.fellOffScreen()) {
    // Only deduct points if it's food (not bombs)
    if (!entity.isBomb()) {
        // Entity was food and wasn't caught - deduct penalty
        gamePanel.player.losePoints(20);
    }
}
```"

---

## PART 11: PERFORMANCE AND OPTIMIZATION - MAKING IT SMOOTH

**[SCREEN SHOWS: Performance metrics and FPS counter]**

**KAREN:** "A game that runs smoothly is crucial for good gameplay. We had to optimize several aspects to maintain 60 FPS."

**[SCREEN SHOWS: Two side-by-side comparisons - slow vs fast code]**

**DIANA:** "Performance is about two things: efficiency and avoiding unnecessary work.

**KEY OPTIMIZATIONS:**
1. **Iterator Pattern for Entity Removal** - Don't use ArrayList.remove(i) in loops
2. **Difficulty Caching** - Calculate difficulty once per frame, not for every entity
3. **Proper Threading** - Game loop runs on separate thread from UI thread
4. **Collision Box Caching** - Don't create new rectangles every frame

**WHY THIS MATTERS:**
- ArrayList.remove(i) in a loop with 100 entities = 10,000+ operations per frame
- That's 600,000 operations per second just for removal!
- Using Iterator: 100 operations per frame = 6,000 per second
- That's a 100x performance improvement!"

**[SCREEN SHOWS: Benchmark comparison graph]**

**CABAHUG:** "Here's the critical optimization in `GameplayManager.updateEntities()`:

**WRONG (Slow O(n²)):**
```java
// WRONG (Slow O(n²)):**
// Loop through all entities using index
for (int i = 0; i < gamePanel.fallingEntities.size(); i++) {
    // Get entity at current index
    FallingEntity entity = gamePanel.fallingEntities.get(i);
    // ... collision logic ...
    // If this entity should be removed
    if (shouldRemove) {
        // Remove element at index i (SLOW - shifts all remaining elements!)
        gamePanel.fallingEntities.remove(i);  // ← SLOW! Shifts all elements
        // Decrement i to not skip the next element (now at position i)
        i--;  // Have to decrement to not skip
    }
}
```

**RIGHT (Fast O(n)):**
```java
Iterator<FallingEntity> iterator = gamePanel.fallingEntities.iterator();
while (iterator.hasNext()) {
    FallingEntity entity = iterator.next();
    // ... collision logic ...
    if (shouldRemove) {
        iterator.remove();  // ← FAST! No shifting needed
    }
}
```

With ArrayList, removing an element means shifting all elements after it down. With 100 entities and 100 removals per second, that's huge.

**Another optimization:**
```java
// WRONG (Recalculated 100+ times per frame)
private void spawnEntities() {
    float difficulty = 1.0f + (gameTime / 10000.0f);
    // Use difficulty...
}

// RIGHT (Calculated once per frame)
private float cachedDifficulty = 1.0f;

private void updateGame() {
    cachedDifficulty = 1.0f + (gameTime / 10000.0f);
    spawnEntities();  // Uses cached value
}
```"

---

## PART 12: COMPLETE GAME FLOW - ONE FULL FRAME EXPLAINED

**[SCREEN SHOWS: Animated diagram showing frame flow]**

**KAREN:** "Let me walk through exactly what happens during one single frame of the game at 60 FPS."

**[SCREEN SHOWS: Timeline diagram]**

**DIANA:** "Here's the sequence of events that happens every 16.67 milliseconds:

**STEP 1: INPUT PROCESSING (1-2ms)**
- MouseHandler detects mouse position
- KeyHandler detects button presses
- InputHandler updates player X position to match mouse
- InputHandler checks for ESC key to toggle pause

**STEP 2: GAME LOGIC UPDATE (3-5ms)**
- Update all falling entity positions (move down by speed)
- Increment game timer for difficulty calculation
- Spawn new entities if it's time
- Calculate current difficulty based on time

**STEP 3: COLLISION DETECTION (2-4ms)**
- Check if player catches any entity
  - If food: add points, play collect sound
  - If bomb: lose life, play explosion sound
- Check if any bomb naturally collides with player
  - Lose life and play sound
- Check if any entity fell off bottom
  - If food: lose points and play sound
  - If bomb: nothing (escaped)
- Remove dead entities using Iterator

**STEP 4: RENDERING (5-8ms)**
- Clear screen with background
- Draw game objects (player, falling entities)
- Draw HUD (hearts, score, difficulty level)
- Or draw menus if in MENU/PAUSE/GAMEOVER state

**STEP 5: AUDIO PLAYBACK (instant)**
- Play any sound effects queued during collision checks
- Continue looping background music

**Total: ~16.67ms - Ready for next frame!**"

**[SCREEN SHOWS: Code walkthrough]**

**CABAHUG:** "In `GamePanel.java`, this is the main frame loop:

```java
private void updateGame() {
    // STEP 1: Input
    inputHandler.handleInput();
    
    // STEP 2: Update game state
    gameplayManager.updateEntities();      // Move entities, check collisions
    
    // Game state is now current
}

private void render() {
    // STEP 4: Render
    Graphics2D g = (Graphics2D) getGraphics();
    
    switch (gameStateManager.getState()) {
        case GAME:
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw all game objects
            for (FallingEntity entity : gamePanel.fallingEntities) {
                entity.draw(g);
            }
            gamePanel.player.draw(g);
            
            // Draw HUD
            uiRenderer.drawHUD(g);
            break;
            
        case PAUSE:
            uiRenderer.drawPauseMenu(g);
            break;
            
        case GAMEOVER:
            uiRenderer.drawGameOverScreen(g);
            break;
    }
}

private void run() {
    long lastTime = System.nanoTime();
    final long FRAME_TIME = 1_000_000_000 / 60;  // ~16.67 ms
    
    while (running) {
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - lastTime;
        
        if (deltaTime >= FRAME_TIME) {
            updateGame();
            render();
            lastTime = currentTime;
        }
    }
}
```"

---

## PART 14: SUMMARY AND KEY TAKEAWAYS

**[SCREEN SHOWS: Review of all concepts covered]**

**KAREN:** "Let's recap the key concepts we've covered today:

**GAME ARCHITECTURE:**
- Game loop at 60 FPS
- State management system
- Manager classes for organization
- Input, Logic, Rendering separated

**GAMEPLAY MECHANICS:**
- Entity spawning with difficulty scaling
- Collision detection with three types
- Lives and scoring system
- Progressive difficulty over time

**TECHNICAL IMPLEMENTATION:**
- Iterator pattern for safe removal
- Enum-based state system
- Event-driven audio system
- Mouse and keyboard input handling

**PERFORMANCE:**
- 60 FPS smooth gameplay
- Optimized entity removal
- Proper threading
- Resource management"

**[SCREEN SHOWS: Final gameplay footage]**

**DIANA:** "From a player's perspective, CATcher delivers:
- Engaging arcade gameplay
- Immediate feedback (sounds and visuals)
- Progressive challenge
- Clear win/lose conditions
- Smooth, responsive controls"

**[SCREEN SHOWS: Code repository and files]**

**CABAHUG:** "From a developer's perspective, this project demonstrates:
- Professional Java game development practices
- Clean code architecture
- Performance optimization techniques
- Audio integration with javax.sound.sampled
- Swing-based graphics rendering
- Thread management and synchronization

The code is well-organized, documented, and uses industry-standard patterns. This is a solid foundation for building more complex games."

**[SCREEN SHOWS: Title card]**

## Conclusion

**CABAHUG, DIANA, and KAREN:** "Thank you for watching our CATcher Game Development Walkthrough! We've covered the complete architecture, all major components, game mechanics, code implementation, and optimization techniques. This project demonstrates real-world game development principles in Java. Feel free to extend this game with new features, and we hope this presentation helps you understand how games work under the hood!"

**[END CREDITS]**
- **Developer: Karen** - Architecture and System Design
- **Developer: Diana** - Game Mechanics and Gameplay Design  
- **Developer: Cabahug** - Code Implementation and Optimization

---

**THE END - CATcher Game Development Presentation Complete**
