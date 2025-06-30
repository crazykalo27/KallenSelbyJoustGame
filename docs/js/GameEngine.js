/**
 * Main game engine - handles game logic, collision detection, and rendering
 */
class GameEngine {
    static POINTS_FOR_ENEMY_KILL = 500;
    static POINTS_FOR_EGG = 500;
    static POINTS_FOR_LEVEL_COMPLETION = 1000;
    static NUM_LEVELS = 12;
    static DELAY = 16; // ~60 FPS

    constructor(canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');
        this.levelLoader = new LevelLoader();

        // Game state
        this.gameObjects = [];
        this.enemies = [];
        this.platforms = [];
        this.eggs = [];
        this.hero = null;
        this.levelNum = 0;
        this.points = 0;
        this.lives = 4;
        this.gameOver = false;
        this.tutorial = true;
        this.pointsThisLevelAttempt = 0;
        this.pointsLoss = 0;

        // Input handling
        this.keys = {
            left: false,
            right: false,
            up: false
        };

        this.setupEventListeners();
        this.gameLoop = null;
    }

    setupEventListeners() {
        // Keyboard input
        document.addEventListener('keydown', (e) => this.handleKeyDown(e));
        document.addEventListener('keyup', (e) => this.handleKeyUp(e));

        // Prevent arrow keys from scrolling the page
        document.addEventListener('keydown', (e) => {
            if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.code)) {
                e.preventDefault();
            }
        });
    }

    handleKeyDown(e) {
        switch (e.code) {
            case 'ArrowLeft':
                this.keys.left = true;
                if (this.hero) this.hero.setLeftKeyHeld(true);
                break;
            case 'ArrowRight':
                this.keys.right = true;
                if (this.hero) this.hero.setRightKeyHeld(true);
                break;
            case 'ArrowUp':
                this.keys.up = true;
                if (this.hero) this.hero.setUpKeyHeld(true);
                break;
            case 'KeyN':
                this.newGame(); // Always restart from beginning
                break;
            case 'KeyU':
                if (this.levelNum < GameEngine.NUM_LEVELS - 1) {
                    this.loadLevel(this.levelNum + 1);
                }
                break;
            case 'KeyD':
                if (this.levelNum > 0) {
                    this.loadLevel(this.levelNum - 1);
                }
                break;
        }
    }

    handleKeyUp(e) {
        switch (e.code) {
            case 'ArrowLeft':
                this.keys.left = false;
                if (this.hero) this.hero.setLeftKeyHeld(false);
                break;
            case 'ArrowRight':
                this.keys.right = false;
                if (this.hero) this.hero.setRightKeyHeld(false);
                break;
            case 'ArrowUp':
                this.keys.up = false;
                if (this.hero) this.hero.setUpKeyHeld(false);
                break;
        }
    }

    async newGame() {
        console.log(`=== STARTING NEW GAME ===`);
        this.levelNum = 0;
        this.points = 0;
        this.pointsLoss = 0;
        this.lives = 4;
        this.gameOver = false;
        this.tutorial = true;
        
        console.log(`New game state: Level ${this.levelNum}, Lives ${this.lives}, Tutorial ${this.tutorial}`);
        await this.loadLevel(0);
        this.updateUI();
        console.log(`=== NEW GAME COMPLETE ===`);
    }

    async loadLevel(levelNumber) {
        console.log(`=== LOADING LEVEL ${levelNumber} ===`);
        this.levelNum = levelNumber;
        
        // Reset points for this level attempt
        this.pointsThisLevelAttempt = 0;
        
        // Set tutorial mode for level 0
        this.tutorial = (levelNumber === 0);
        this.levelLoader.setTutorial(this.tutorial);
        
        // Clear existing objects
        this.gameObjects = [];
        this.enemies = [];
        this.platforms = [];
        this.eggs = [];
        console.log(`Arrays cleared - about to load level data`);
        
        // Load level data
        const levelData = await this.levelLoader.loadLevel(levelNumber + "level");
        console.log(`Level data received:`, levelData);
        
        if (levelData.objects.length > 0) {
            this.gameObjects = levelData.objects;
            this.enemies = levelData.enemies;
            this.platforms = levelData.platforms;
            this.hero = levelData.hero;
            
            // Provide platforms to RandomMoveEnemy instances for ground detection
            for (const enemy of this.enemies) {
                if (enemy instanceof RandomMoveEnemy) {
                    enemy.setPlatforms(this.platforms);
                }
            }
            
            // Debug: Log level loading info
            console.log(`Level ${levelNumber} loaded:`);
            console.log(`  Total objects: ${this.gameObjects.length}`);
            console.log(`  Platforms: ${this.platforms.length}`);
            console.log(`  Enemies: ${this.enemies.length}`);
            console.log(`  Eggs: ${this.eggs.length}`);
            if (this.hero) {
                console.log(`  Hero at: (${this.hero.getXCent()}, ${this.hero.getYCent()})`);
                console.log(`  Hero previous pos: (${this.hero.getPreviousXPos()}, ${this.hero.getPreviousYPos()})`);
            }
        } else {
            console.log(`ERROR: Level ${levelNumber} failed to load - no objects found!`);
        }
        
        this.updateUI();
        console.log(`=== LEVEL ${levelNumber} LOADING COMPLETE ===`);
    }

    start() {
        if (this.gameLoop) {
            clearInterval(this.gameLoop);
        }
        
        this.gameLoop = setInterval(() => {
            this.update();
            this.render();
        }, GameEngine.DELAY);
    }

    stop() {
        if (this.gameLoop) {
            clearInterval(this.gameLoop);
            this.gameLoop = null;
        }
    }

    update() {
        if (this.gameOver) return;

        // Provide platforms to RandomMoveEnemy instances for ground detection
        for (const enemy of this.enemies) {
            if (enemy instanceof RandomMoveEnemy) {
                enemy.setPlatforms(this.platforms);
            }
        }

        // Update all game objects
        const objectsToRemove = [];
        
        for (const obj of [...this.gameObjects]) {
            try {
                obj.update();
            } catch (error) {
                if (error.message.includes("DeadException")) {
                    this.removeDeadEnemy(obj);
                    objectsToRemove.push(obj);
                }
            }
        }

        // Remove dead enemies
        for (const obj of objectsToRemove) {
            this.removeGameObject(obj);
        }

        // Handle collisions
        this.handleCollisions();

        // Check win condition - but only if we're not on the tutorial level AND the level has actually loaded
        if (this.gameObjects.length > 0 && this.enemies.length === 0 && this.eggs.length === 0) {
            console.log(`WIN CONDITION TRIGGERED: Level ${this.levelNum}, Enemies: ${this.enemies.length}, Eggs: ${this.eggs.length}`);
            
            // Award level completion bonus
            this.points += GameEngine.POINTS_FOR_LEVEL_COMPLETION;
            this.pointsThisLevelAttempt += GameEngine.POINTS_FOR_LEVEL_COMPLETION;
            
            if (this.levelNum < GameEngine.NUM_LEVELS - 1) {
                console.log(`Advancing to level ${this.levelNum + 1}`);
                this.tutorial = false;
                this.loadLevel(this.levelNum + 1);
            } else {
                console.log(`Game completed! Max level reached.`);
            }
            this.updateUI();
        }

        // Update eggs and check for respawn
        for (const egg of [...this.eggs]) {
            if (egg.isReadyToRespawn()) {
                this.replaceEgg(egg);
            }
        }
    }

    removeDeadEnemy(enemy) {
        if (!(enemy instanceof Enemy)) return;

        // Create egg at enemy position
        const egg = new Egg(
            enemy.getXCent(),
            enemy.getYCent(),
            enemy,
            "Egg"
        );
        
        this.gameObjects.push(egg);
        this.eggs.push(egg);
        
        // Only award points for original enemies, not respawned ones
        if (!enemy.isRespawnedEnemy()) {
            this.points += GameEngine.POINTS_FOR_ENEMY_KILL;
            this.pointsThisLevelAttempt += GameEngine.POINTS_FOR_ENEMY_KILL;
        }
        
        this.updateUI();
    }

    replaceEgg(egg) {
        // Remove egg
        this.removeGameObject(egg);
        this.eggs = this.eggs.filter(e => e !== egg);
        
        // Create new enemy from egg and mark as respawned
        const newEnemy = egg.getContainedEnemy().getCopy();
        newEnemy.setRespawned(true); // Mark as respawned so no points awarded
        newEnemy.setXCent(egg.getXCent());
        newEnemy.setYCent(egg.getYCent() - newEnemy.getHeight() / 2);
        
        // If it's a RandomMoveEnemy (Koopa), provide platforms for ground detection
        if (newEnemy instanceof RandomMoveEnemy) {
            newEnemy.setPlatforms(this.platforms);
        }
        
        this.gameObjects.push(newEnemy);
        this.enemies.push(newEnemy);
    }

    removeGameObject(obj) {
        this.gameObjects = this.gameObjects.filter(o => o !== obj);
        this.enemies = this.enemies.filter(e => e !== obj);
    }

    handleCollisions() {
        if (!this.hero) return;

        // Hero-Platform collisions
        const heroCollidingPlatforms = [];
        
        // Debug: Log platforms array state
        if (this.platforms.length === 0) {
            console.warn(`No platforms in array during collision check`);
            return;
        }
        
        for (let i = 0; i < this.platforms.length; i++) {
            const platform = this.platforms[i];
            
            // Validate platform object
            if (!platform) {
                console.error(`NULL platform at index ${i}`);
                continue;
            }
            
            if (!(platform instanceof Platform)) {
                console.error(`ERROR: Non-Platform object at index ${i}:`, typeof platform, platform.constructor?.name, platform);
                continue;
            }
            
            if (this.hero.overlaps(platform)) {
                // Platform effect handling - exactly matching Java order
                if (platform.isLava()) {
                    console.log(`Hero hit lava platform - respawning`);
                    this.respawn();
                    return;
                }
                if (platform.isIce()) {
                    this.hero.addXVelocity(5 * Math.sign(this.hero.getXVelocity()));
                }
                if (platform.isSlime()) {
                    this.hero.setXVelocity(0);
                }
                if (platform.isCool()) {
                    platform.setName(0);
                    platform.SetCool(false);
                    this.lives++;
                    this.updateUI();
                }
                heroCollidingPlatforms.push(platform);
            }

            // Enemy-Platform collisions - only for valid platforms
            for (const enemy of this.enemies) {
                if (enemy.overlaps(platform)) {
                    enemy.collidewith(platform);
                }
            }

            // Egg-Platform collisions - only for valid platforms
            for (const egg of this.eggs) {
                if (egg.overlaps(platform)) {
                    if (platform.isLava()) {
                        this.removeGameObject(egg);
                        this.eggs = this.eggs.filter(e => e !== egg);
                    } else {
                        egg.collidewith(platform);
                    }
                }
            }
        }

        // Handle hero-platform collision - exactly matching Java collision logic
        if (heroCollidingPlatforms.length > 0) {
            // Find closest platform using same logic as Java
            let closestPlatform = heroCollidingPlatforms[0];
            let shortestDistance = Number.MAX_VALUE;
            
            for (const platform of heroCollidingPlatforms) {
                const distance = platform.getDistance(this.hero.getXCent(), this.hero.getYCent());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestPlatform = platform;
                }
            }
            
            // Apply collision with closest platform
            this.hero.collidewith(closestPlatform);
        }

        // Hero-Enemy collisions (Joust mechanics)
        const bounceStrength = 25;
        for (const enemy of this.enemies) {
            if (this.hero.overlaps(enemy)) {
                const joustResult = this.hero.joust(enemy);
                if (joustResult === 2) {
                    enemy.markForRemoval();
                } else if (joustResult === 0) {
                    this.respawn();
                    return;
                } else {
                    const direction = Math.sign(enemy.getXCent() - this.hero.getXCent());
                    this.hero.setXVelocity(-direction * bounceStrength);
                }
            }
        }

        // Hero-Egg collisions
        for (const egg of [...this.eggs]) {
            if (egg.overlaps(this.hero)) {
                this.removeGameObject(egg);
                this.eggs = this.eggs.filter(e => e !== egg);
                
                // Only award points for eggs from original enemies, not respawned ones
                if (!egg.isFromRespawnedEnemy()) {
                    this.points += GameEngine.POINTS_FOR_EGG;
                    this.pointsThisLevelAttempt += GameEngine.POINTS_FOR_EGG;
                }
                
                this.updateUI();
            }
        }

        // Enemy-to-Enemy collisions (prevent passing through each other)
        for (let i = 0; i < this.enemies.length; i++) {
            for (let j = i + 1; j < this.enemies.length; j++) {
                const enemy1 = this.enemies[i];
                const enemy2 = this.enemies[j];
                
                if (enemy1.overlaps(enemy2)) {
                    this.resolveEnemyCollision(enemy1, enemy2);
                }
            }
        }
    }

    resolveEnemyCollision(enemy1, enemy2) {
        // Calculate overlap and separation
        const rect1 = enemy1.getBoundingBox();
        const rect2 = enemy2.getBoundingBox();
        
        // Calculate intersection
        const intersectionLeft = Math.max(rect1.x, rect2.x);
        const intersectionRight = Math.min(rect1.x + rect1.width, rect2.x + rect2.width);
        const intersectionTop = Math.max(rect1.y, rect2.y);
        const intersectionBottom = Math.min(rect1.y + rect1.height, rect2.y + rect2.height);
        
        const overlapWidth = intersectionRight - intersectionLeft;
        const overlapHeight = intersectionBottom - intersectionTop;
        
        // Determine primary collision direction (smaller overlap indicates collision direction)
        if (overlapWidth < overlapHeight) {
            // Horizontal collision - separate horizontally
            const separation = overlapWidth / 2;
            const direction = Math.sign(enemy2.getXCent() - enemy1.getXCent());
            
            // Move enemies apart
            enemy1.move(-direction * separation, 0);
            enemy2.move(direction * separation, 0);
            
            // Stop horizontal movement for both enemies
            enemy1.setXVelocity(0);
            enemy2.setXVelocity(0);
            
            // Update positions to prevent stuck states
            enemy1.updatePreviousPosition();
            enemy2.updatePreviousPosition();
        } else {
            // Vertical collision - separate vertically  
            const separation = overlapHeight / 2;
            const direction = Math.sign(enemy2.getYCent() - enemy1.getYCent());
            
            // Move enemies apart
            enemy1.move(0, -direction * separation);
            enemy2.move(0, direction * separation);
            
            // Stop vertical movement for both enemies
            enemy1.setYVelocity(0);
            enemy2.setYVelocity(0);
            
            // Update positions to prevent stuck states
            enemy1.updatePreviousPosition();
            enemy2.updatePreviousPosition();
        }
    }

    respawn() {
        // Lose points equal to what was gained on this level attempt
        this.points -= this.pointsThisLevelAttempt;
        this.lives--;
        
        if (this.lives <= 0) {
            this.tutorial = false;
            this.gameOver = true;
        } else {
            this.loadLevel(this.levelNum); // Respawn on current level (this will reset pointsThisLevelAttempt)
        }
        
        this.updateUI();
    }

    render() {
        // Clear canvas
        this.ctx.fillStyle = 'black';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        if (this.tutorial) {
            this.renderTutorial();
        }

        if (this.gameOver) {
            this.renderGameOver();
            return;
        }

        // Render all game objects
        for (const obj of this.gameObjects) {
            obj.drawOn(this.ctx);
        }
    }

    renderTutorial() {
        this.ctx.fillStyle = 'white';
        this.ctx.font = '60px Arial';
        this.ctx.fillText('Welcome to Joust!', 150, 150);

        this.ctx.font = '25px Arial';
        this.ctx.fillText('Use Arrow Keys to fly', 500, 430);
        this.ctx.fillText('up to this health box!', 500, 455);

        this.ctx.fillText('During Collisions, if you are higher', 75, 200);
        this.ctx.fillText('than the enemy it will die. Don\'t hit', 75, 225);
        this.ctx.fillText('them while they are higher!', 75, 250);

        this.ctx.fillText('Kill all enemies to move', 75, 525);
        this.ctx.fillText('to the next level!', 75, 550);

        this.ctx.fillText('Press \'N\' to restart!', 75, 600);

        this.ctx.fillText('Watch out for blocks', 450, 675);
        this.ctx.fillText('with special properties!', 450, 700);
    }

    renderGameOver() {
        this.ctx.fillStyle = 'white';
        this.ctx.font = '80px Arial';
        this.ctx.fillText('GAME OVER', 150, 250);

        this.ctx.font = '30px Arial';
        this.ctx.fillText('Press \'N\' to quick restart!', 150, 400);

        // Try to draw game over images (optional)
        for (let i = 0; i < 4; i++) {
            const img = new Image();
            img.src = `images/gameover${i}.png`;
            img.onload = () => {
                this.ctx.drawImage(img, 25 + (215 * i), 450, 190, 190);
            };
        }
    }

    updateUI() {
        document.getElementById('score').textContent = `Points: ${this.points}`;
        document.getElementById('lives').textContent = `Lives: ${this.lives}`;
        document.getElementById('level').textContent = `Level: ${this.levelNum}`;
    }
} 