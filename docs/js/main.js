/**
 * Main game initialization
 */
document.addEventListener('DOMContentLoaded', async () => {
    console.log(`=== GAME INITIALIZATION STARTED ===`);
    const canvas = document.getElementById('gameCanvas');
    
    if (!canvas) {
        console.error('Game canvas not found!');
        return;
    }

    console.log(`Canvas found, creating game engine...`);
    // Initialize game engine
    const game = new GameEngine(canvas);
    
    // Initialize mobile controls
    console.log(`Initializing mobile controls...`);
    const mobileControls = new MobileControls(game);
    
    console.log(`Game engine created, starting new game...`);
    // Start new game
    await game.newGame();
    
    console.log(`New game complete, starting game loop...`);
    // Start game loop
    game.start();
    
    console.log('Joust game initialized successfully!');
}); 