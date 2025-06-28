/**
 * Main game initialization
 */
document.addEventListener('DOMContentLoaded', async () => {
    const canvas = document.getElementById('gameCanvas');
    
    if (!canvas) {
        console.error('Game canvas not found!');
        return;
    }

    // Initialize game engine
    const game = new GameEngine(canvas);
    
    // Start new game
    await game.newGame();
    
    // Start game loop
    game.start();
    
    console.log('Joust game initialized successfully!');
}); 