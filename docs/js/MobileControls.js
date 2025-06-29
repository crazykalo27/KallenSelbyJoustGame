/**
 * Mobile Controls - Handles touch input for mobile devices
 */
class MobileControls {
    constructor(gameEngine) {
        this.gameEngine = gameEngine;
        this.joystickActive = false;
        this.joystickCenter = { x: 0, y: 0 };
        this.joystickRadius = 50; // Half of joystick base width
        this.handleRadius = 20;   // Half of handle width
        this.deadZone = 15;       // Minimum distance before registering input
        
        this.currentTouch = null;
        this.upButtonPressed = false;
        
        this.initializeControls();
    }

    initializeControls() {
        const joystick = document.getElementById('mobileJoystick');
        const joystickHandle = document.getElementById('joystickHandle');
        const upButton = document.getElementById('mobileUpButton');
        const restartButton = document.getElementById('mobileRestartButton');
        
        if (!joystick || !joystickHandle || !upButton || !restartButton) {
            console.log('Mobile controls not found - likely on desktop');
            return;
        }

        // Set initial joystick center position
        this.updateJoystickCenter();
        
        // Window resize handler to update joystick center
        window.addEventListener('resize', () => {
            this.updateJoystickCenter();
        });

        // Joystick touch events
        joystick.addEventListener('touchstart', (e) => this.handleJoystickStart(e), { passive: false });
        joystick.addEventListener('touchmove', (e) => this.handleJoystickMove(e), { passive: false });
        joystick.addEventListener('touchend', (e) => this.handleJoystickEnd(e), { passive: false });
        joystick.addEventListener('touchcancel', (e) => this.handleJoystickEnd(e), { passive: false });

        // Up button touch events
        upButton.addEventListener('touchstart', (e) => this.handleUpButtonStart(e), { passive: false });
        upButton.addEventListener('touchend', (e) => this.handleUpButtonEnd(e), { passive: false });
        upButton.addEventListener('touchcancel', (e) => this.handleUpButtonEnd(e), { passive: false });

        // Restart button touch events
        restartButton.addEventListener('touchstart', (e) => this.handleRestartButtonStart(e), { passive: false });
        restartButton.addEventListener('touchend', (e) => this.handleRestartButtonEnd(e), { passive: false });
        restartButton.addEventListener('touchcancel', (e) => this.handleRestartButtonEnd(e), { passive: false });

        // Mouse events for testing on desktop (optional)
        joystick.addEventListener('mousedown', (e) => this.handleJoystickMouseStart(e));
        document.addEventListener('mousemove', (e) => this.handleJoystickMouseMove(e));
        document.addEventListener('mouseup', (e) => this.handleJoystickMouseEnd(e));
        
        upButton.addEventListener('mousedown', (e) => this.handleUpButtonMouseStart(e));
        upButton.addEventListener('mouseup', (e) => this.handleUpButtonMouseEnd(e));
        
        restartButton.addEventListener('mousedown', (e) => this.handleRestartButtonMouseStart(e));
        restartButton.addEventListener('mouseup', (e) => this.handleRestartButtonMouseEnd(e));
    }

    updateJoystickCenter() {
        const joystickBase = document.querySelector('.joystick-base');
        if (joystickBase) {
            const rect = joystickBase.getBoundingClientRect();
            this.joystickCenter.x = rect.left + rect.width / 2;
            this.joystickCenter.y = rect.top + rect.height / 2;
        }
    }

    // Joystick Touch Events
    handleJoystickStart(e) {
        e.preventDefault();
        this.joystickActive = true;
        this.currentTouch = e.touches[0];
        this.updateJoystickCenter();
    }

    handleJoystickMove(e) {
        if (!this.joystickActive || !this.currentTouch) return;
        
        e.preventDefault();
        const touch = e.touches[0];
        this.updateJoystickPosition(touch.clientX, touch.clientY);
    }

    handleJoystickEnd(e) {
        e.preventDefault();
        this.joystickActive = false;
        this.currentTouch = null;
        this.resetJoystick();
    }

    // Mouse Events for Desktop Testing
    handleJoystickMouseStart(e) {
        this.joystickActive = true;
        this.updateJoystickCenter();
        this.updateJoystickPosition(e.clientX, e.clientY);
    }

    handleJoystickMouseMove(e) {
        if (!this.joystickActive) return;
        this.updateJoystickPosition(e.clientX, e.clientY);
    }

    handleJoystickMouseEnd(e) {
        this.joystickActive = false;
        this.resetJoystick();
    }

    updateJoystickPosition(clientX, clientY) {
        const deltaX = clientX - this.joystickCenter.x;
        const deltaY = clientY - this.joystickCenter.y;
        const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Constrain handle within joystick base
        const maxDistance = this.joystickRadius - this.handleRadius;
        const constrainedDistance = Math.min(distance, maxDistance);
        
        let handleX = 0;
        let handleY = 0;
        
        if (distance > 0) {
            handleX = (deltaX / distance) * constrainedDistance;
            handleY = (deltaY / distance) * constrainedDistance;
        }
        
        // Update handle position
        const handle = document.getElementById('joystickHandle');
        if (handle) {
            handle.style.transform = `translate(${handleX}px, ${handleY}px)`;
        }
        
        // Process input only if outside dead zone
        if (distance > this.deadZone) {
            const normalizedX = deltaX / maxDistance;
            this.processJoystickInput(normalizedX);
        } else {
            this.processJoystickInput(0);
        }
    }

    processJoystickInput(normalizedX) {
        if (!this.gameEngine || !this.gameEngine.hero) return;
        
        const threshold = 0.3;
        
        if (normalizedX < -threshold) {
            // Moving left
            this.gameEngine.hero.setLeftKeyHeld(true);
            this.gameEngine.hero.setRightKeyHeld(false);
        } else if (normalizedX > threshold) {
            // Moving right
            this.gameEngine.hero.setLeftKeyHeld(false);
            this.gameEngine.hero.setRightKeyHeld(true);
        } else {
            // Neutral position
            this.gameEngine.hero.setLeftKeyHeld(false);
            this.gameEngine.hero.setRightKeyHeld(false);
        }
    }

    resetJoystick() {
        const handle = document.getElementById('joystickHandle');
        if (handle) {
            handle.style.transform = 'translate(0px, 0px)';
        }
        
        if (this.gameEngine && this.gameEngine.hero) {
            this.gameEngine.hero.setLeftKeyHeld(false);
            this.gameEngine.hero.setRightKeyHeld(false);
        }
    }

    // Up Button Touch Events
    handleUpButtonStart(e) {
        e.preventDefault();
        this.upButtonPressed = true;
        if (this.gameEngine && this.gameEngine.hero) {
            this.gameEngine.hero.setUpKeyHeld(true);
        }
    }

    handleUpButtonEnd(e) {
        e.preventDefault();
        this.upButtonPressed = false;
        if (this.gameEngine && this.gameEngine.hero) {
            this.gameEngine.hero.setUpKeyHeld(false);
        }
    }

    // Mouse Events for Up Button (Desktop Testing)
    handleUpButtonMouseStart(e) {
        e.preventDefault();
        this.upButtonPressed = true;
        if (this.gameEngine && this.gameEngine.hero) {
            this.gameEngine.hero.setUpKeyHeld(true);
        }
    }

    handleUpButtonMouseEnd(e) {
        e.preventDefault();
        this.upButtonPressed = false;
        if (this.gameEngine && this.gameEngine.hero) {
            this.gameEngine.hero.setUpKeyHeld(false);
        }
    }

    // Restart Button Touch Events
    handleRestartButtonStart(e) {
        e.preventDefault();
        // Visual feedback
        e.target.style.transform = 'scale(0.9)';
    }

    handleRestartButtonEnd(e) {
        e.preventDefault();
        // Reset visual feedback
        e.target.style.transform = 'scale(1)';
        // Restart the game
        if (this.gameEngine) {
            this.gameEngine.newGame();
        }
    }

    // Mouse Events for Restart Button (Desktop Testing)
    handleRestartButtonMouseStart(e) {
        e.preventDefault();
        // Visual feedback
        e.target.style.transform = 'scale(0.9)';
    }

    handleRestartButtonMouseEnd(e) {
        e.preventDefault();
        // Reset visual feedback
        e.target.style.transform = 'scale(1)';
        // Restart the game
        if (this.gameEngine) {
            this.gameEngine.newGame();
        }
    }
} 