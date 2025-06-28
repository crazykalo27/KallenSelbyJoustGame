/**
 * Level loader - handles loading levels from text files
 */
class LevelLoader {
    static AIR_STRING = ".";
    static PLATFORM_STRING = "o";
    static HERO_STRING = "h";
    static GHOST_STRING = "b";
    static KOOPA_STRING = "e";
    static TRACKER_STRING = "t";
    static LAVA_STRING = "l";
    static ICE_STRING = "i";
    static SLIME_STRING = "s";
    static REGEN_STRING = "c";
    static COORDINATE_SCALE = 50;

    static HERO_FILE = "DigDug";
    static GHOST_FILE = "PacMan";
    static KOOPA_FILE = "BlueKoopa";
    static TRACKER_ENEMY_FILE = "WaddleD";

    constructor() {
        this.tutorial = true;
        this.enemies = [];
        this.platforms = [];
        this.hero = null;
    }

    setTutorial(tutorial) {
        this.tutorial = tutorial;
    }

    async loadLevel(levelName) {
        console.log(`LevelLoader: Attempting to load ${levelName}.txt`);
        try {
            const response = await fetch(`levels/${levelName}.txt`);
            console.log(`LevelLoader: Fetch response status: ${response.status}`);
            if (!response.ok) {
                throw new Error(`Failed to load level: ${levelName}`);
            }
            
            const levelData = await response.text();
            console.log(`LevelLoader: Level data loaded, length: ${levelData.length}`);
            console.log(`LevelLoader: First few lines:`, levelData.split('\n').slice(0, 3));
            
            const result = this.parseLevel(levelData);
            console.log(`LevelLoader: Parse result:`, result);
            return result;
        } catch (error) {
            console.error("Error loading level:", error);
            return { objects: [], enemies: [], platforms: [], hero: null };
        }
    }

    parseLevel(levelData) {
        // Remove carriage returns and split into lines - matches Java Scanner behavior
        const lines = levelData.replace(/\r/g, '').trim().split('\n');
        const objects = [];
        this.enemies = [];
        this.platforms = [];
        this.hero = null;
        
        let trackerPlaceholder = null;
        let trackerIndex = -1;
        
        console.log(`Parsing level with ${lines.length} lines`);

        for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            for (let j = 0; j < line.length; j++) {
                const char = line[j];
                
                // Skip empty characters and whitespace - matches Java logic
                if (char === '' || char === ' ') {
                    continue;
                }
                
                const x = j * LevelLoader.COORDINATE_SCALE + LevelLoader.COORDINATE_SCALE / 2;
                const y = i * LevelLoader.COORDINATE_SCALE + LevelLoader.COORDINATE_SCALE / 2;
                
                // Debug: Log coordinate calculation for hero and key platforms
                if (char === 'h' || char === 'b') {
                    console.log(`Char '${char}' at grid (${i}, ${j}) -> world position (${x}, ${y})`);
                }

                const gameObject = this.createGameObject(char, x, y);
                if (gameObject) {
                    objects.push(gameObject);
                    
                    // Track special objects
                    if (gameObject instanceof Hero) {
                        this.hero = gameObject;
                        console.log(`Hero created and assigned`);
                    } else if (gameObject instanceof Enemy) {
                        this.enemies.push(gameObject);
                        console.log(`Enemy added: ${gameObject.constructor.name}`);
                        
                        // Handle tracker placeholder
                        if (char === LevelLoader.TRACKER_STRING) {
                            trackerPlaceholder = gameObject;
                            trackerIndex = this.enemies.length - 1;
                        }
                    } else if (gameObject instanceof Platform) {
                        this.platforms.push(gameObject);
                    } else {
                        console.warn(`Unknown object type created:`, gameObject);
                    }
                }
            }
        }

        // Replace tracker placeholder with actual tracker that follows hero
        if (trackerPlaceholder && this.hero && trackerIndex >= 0) {
            const tracker = new Tracker(
                trackerPlaceholder.getXCent(),
                trackerPlaceholder.getYCent(),
                1.2, // Reduced to 1/3 of original (3.5/3 ≈ 1.2)
                this.hero,
                LevelLoader.TRACKER_ENEMY_FILE
            );
            
            // Replace in all arrays
            objects[objects.indexOf(trackerPlaceholder)] = tracker;
            this.enemies[trackerIndex] = tracker;
        }

        return {
            objects: objects,
            enemies: this.enemies,
            platforms: this.platforms,
            hero: this.hero
        };
    }

    createGameObject(char, x, y) {
        switch (char) {
            case LevelLoader.AIR_STRING:
                return null;
                
            case LevelLoader.PLATFORM_STRING:
                return new Platform(x, y, 0);
                
            case LevelLoader.HERO_STRING:
                return new Hero(x, y, 0.5, LevelLoader.HERO_FILE); // Further reduced for better control
                
            case LevelLoader.GHOST_STRING:
                const ghostSpeed = this.tutorial ? 0 : 1.8; // Reduced to 1/3 of original (5.5/3 ≈ 1.8)
                return new LeftRightEnemy(x, y, ghostSpeed, LevelLoader.GHOST_FILE);
                
            case LevelLoader.KOOPA_STRING:
                return new RandomMoveEnemy(x, y, 1.0, LevelLoader.KOOPA_FILE); // Reduced to 1/3 of original (3.0/3 = 1.0)
                
            case LevelLoader.TRACKER_STRING:
                // Create placeholder - will be replaced with actual tracker later
                return new RandomMoveEnemy(x, y, 1.0, LevelLoader.KOOPA_FILE);
                
            case LevelLoader.LAVA_STRING:
                return new Platform(x, y, 1);
                
            case LevelLoader.ICE_STRING:
                return new Platform(x, y, 2);
                
            case LevelLoader.SLIME_STRING:
                return new Platform(x, y, 3);
                
            case LevelLoader.REGEN_STRING:
                return new Platform(x, y, 4);
                
            default:
                console.warn(`Unknown character in level: '${char}' (char code: ${char.charCodeAt(0)})`);
                return null;
        }
    }

    getEnemies() {
        return this.enemies;
    }

    getPlatforms() {
        return this.platforms;
    }

    getHero() {
        return this.hero;
    }
} 