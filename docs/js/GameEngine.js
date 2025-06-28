/**
 * Main game engine - handles game logic, collision detection, and rendering
 */
class GameEngine {
    static POINTS_FOR_ENEMY_KILL = 750;
    static POINTS_FOR_EGG = 500;
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
                this.newGame();
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
        this.levelNum = 0;
        this.points = 0;
        this.pointsLoss = 0;
        this.lives = 4;
        this.gameOver = false;
        this.tutorial = true;
        
        await this.loadLevel(0);
        this.updateUI();
    }

    async loadLevel(levelNumber) {
        this.levelNum = levelNumber;
        
        // Set tutorial mode for level 0
        this.tutorial = (levelNumber === 0);
        this.levelLoader.setTutorial(this.tutorial);
        
        // Clear existing objects
        this.gameObjects = [];
        this.enemies = [];
        this.platforms = [];
        this.eggs = [];
        
        // Load level data
        const levelData = await this.levelLoader.loadLevel(levelNumber + "level");
        
        if (levelData.objects.length > 0) {
            this.gameObjects = levelData.objects;
            this.enemies = levelData.enemies;
            this.platforms = levelData.platforms;
            this.hero = levelData.hero;
        }
        
        this.updateUI();
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

        // Check win condition
        if (this.enemies.length === 0 && this.eggs.length === 0) {
            if (this.levelNum < GameEngine.NUM_LEVELS - 1) {
                this.tutorial = false;
                this.pointsLoss = 0;
                this.loadLevel(this.levelNum + 1);
            }
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
        
        // Award points
        this.points += GameEngine.POINTS_FOR_ENEMY_KILL;
        this.pointsLoss += GameEngine.POINTS_FOR_ENEMY_KILL;
        
        this.updateUI();
    }

    replaceEgg(egg) {
        // Remove egg
        this.removeGameObject(egg);
        this.eggs = this.eggs.filter(e => e !== egg);
        
        // Create new enemy from egg
        const newEnemy = egg.getContainedEnemy().getCopy();
        newEnemy.setXCent(egg.getXCent());
        newEnemy.setYCent(egg.getYCent() - newEnemy.getHeight() / 2);
        
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
        for (const platform of this.platforms) {
            if (this.hero.overlaps(platform)) {
                if (platform.isLava()) {
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
                    platform.setCool(false);
                    this.lives++;
                    this.updateUI();
                }
                heroCollidingPlatforms.push(platform);
            }

            // Enemy-Platform collisions
            for (const enemy of this.enemies) {
                if (enemy.overlaps(platform)) {
                    enemy.collidewith(platform);
                }
            }

            // Egg-Platform collisions
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

        // Handle hero-platform collision
        if (heroCollidingPlatforms.length > 0) {
            let closestPlatform = heroCollidingPlatforms[0];
            let shortestDistance = Infinity;
            
            for (const platform of heroCollidingPlatforms) {
                const distance = platform.getDistance(this.hero.getXCent(), this.hero.getYCent());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestPlatform = platform;
                }
            }
            
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
                this.pointsLoss += GameEngine.POINTS_FOR_EGG;
                this.points += GameEngine.POINTS_FOR_EGG;
                this.updateUI();
            }
        }
    }

    respawn() {
        // Lose points equal to enemies on screen
        this.points -= this.pointsLoss;
        this.lives--;
        
        if (this.lives <= 0) {
            this.tutorial = false;
            this.gameOver = true;
        } else {
            this.pointsLoss = 0;
            this.loadLevel(this.levelNum);
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

        this.ctx.fillText('Press \'N\' to quick restart!', 75, 600);

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