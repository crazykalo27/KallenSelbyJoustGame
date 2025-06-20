# Joust Game Deployment Tasks

This document outlines the streamlined build tasks for deploying the Joust Game to GitHub Pages. Due to some encoding issues with the build.gradle file, please manually add these tasks to your build files.

## Required Tasks

Add the following tasks to your root `build.gradle` file:

```groovy
// Clean docs directory while preserving git files
task cleanDocs {
    group = "deployment"
    description = "Cleans the docs directory properly while preserving git files"
    
    doLast {
        println "Cleaning docs directory..."
        delete fileTree('docs') {
            exclude '.git/**'
            exclude '.gitignore'
        }
        println "Docs directory cleaned."
    }
}

// Main deployment task
task docsDeploy(dependsOn: ['cleanDocs']) {
    group = "deployment"
    description = "Deploy HTML build to docs directory with proper asset handling"
    
    doLast {
        println "Starting deployment..."
        
        // Ensure required directories exist
        mkdir "docs"
        mkdir "docs/assets"
        mkdir "docs/assets/com/badlogic/gdx/utils"
        
        // Copy compiled HTML/JS
        if (file("html/build/dist").exists()) {
            copy {
                from 'html/build/dist'
                into 'docs'
            }
            println "Copied compiled GWT output"
        } else {
            println "Warning: html/build/dist not found. Run ./gradlew html:dist first."
        }
        
        // Copy assets preserving our master assets.txt
        copy {
            from 'assets'
            into 'docs/assets'
            exclude 'assets.txt' 
        }
        
        // Copy master assets.txt
        copy {
            from 'html/src/main/webapp/assets/assets.txt'
            into 'docs/assets'
        }
        
        // Copy fonts to LibGDX expected paths
        copy {
            from 'html/src/main/webapp/assets/fonts/font.fnt'
            into 'docs/assets/com/badlogic/gdx/utils'
            rename 'font.fnt', 'lsans-15.fnt'
        }
        copy {
            from 'html/src/main/webapp/assets/fonts/font.png'
            into 'docs/assets/com/badlogic/gdx/utils'
            rename 'font.png', 'lsans.png'
        }
        
        println "Docs deployment completed!"
    }
}

// Full deployment task (clean, compile, deploy)
task fullDeploy(dependsOn: ['clean', ':html:dist', 'docsDeploy']) {
    group = "deployment"
    description = "Full clean, compile and deploy process for HTML/docs"
}

// Ensure tasks run in the correct order
tasks.named('docsDeploy').configure {
    mustRunAfter(':html:dist') 
}
```

## HTML Module Build Tasks

Make sure your `html/build.gradle` has the following tasks:

```groovy
// Copy raw assets preserving our master assets.txt
task copyAssets(type: Copy) {
    into 'src/main/webapp/assets'
    from('../assets/images') {
        into 'images'
    }
    from('../assets/levels') {
        into 'levels'
    }
    
    // Copy font files
    from('../html/src/main/webapp/assets/fonts') {
        into 'fonts'
    }
    
    // Copy font files to LibGDX expected path
    from('../html/src/main/webapp/assets/fonts/font.fnt') {
        into 'com/badlogic/gdx/utils'
        rename 'font.fnt', 'lsans-15.fnt'
    }
    from('../html/src/main/webapp/assets/fonts/font.png') {
        into 'com/badlogic/gdx/utils'
        rename 'font.png', 'lsans.png'
    }
}

// Tasks should depend on copyAssets
tasks.named('compileJava')      { dependsOn copyAssets }
tasks.named('compileGwt')       { dependsOn copyAssets }
tasks.named('processResources') { dependsOn copyAssets }

// Clean task 
tasks.clean.doFirst {
    // Safely delete war directory
    if (file('war').exists()) {
        project.delete(fileTree('war').include('**/*'))
    }
}

// Assemble production-ready HTML5 site
task dist(type: Copy) {
    group       = 'build'
    description = 'Compile GWT + bundle with webapp into build/dist'
    dependsOn clean, compileGwt

    doFirst {
        delete "${buildDir}/dist"
    }

    // Compiled HTML+JS
    from 'build/gwt/out/html'
    into 'build/dist'

    // Static files + assets
    from('src/main/webapp') {
        include 'index.html'
        include 'assets/**'
    }
    
    // Copy font files to both locations
    doLast {
        copy {
            from 'src/main/webapp/assets/fonts/font.fnt'
            into 'build/dist/assets/com/badlogic/gdx/utils'
            rename 'font.fnt', 'lsans-15.fnt'
        }
        
        copy {
            from 'src/main/webapp/assets/fonts/font.png'
            into 'build/dist/assets/com/badlogic/gdx/utils'
            rename 'font.png', 'lsans.png'
        }
    }
}
```

## Deployment Process

1. Clean everything:
   ```
   ./gradlew clean
   ```

2. Build the HTML version:
   ```
   ./gradlew html:dist
   ```

3. Deploy to docs:
   ```
   ./gradlew docsDeploy
   ```

4. Or do everything in one command:
   ```
   ./gradlew fullDeploy
   ``` 