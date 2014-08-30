Clojuroids
==========

A [ClojureScript](http://github.com/clojure/clojurescript) version of the classic [Asteroids] (http://en.wikipedia.org/wiki/Asteroids_(video_game)) video game.

## Current status

* Several asteroids are created and move around the playing field
 * They break into smaller asteroids when hit 
 * They respawn after the last one is hit
* A ship is created and can be controlled
* The ship can fire shots
* Hit detection
  * between shots and roids
  * between ship and roids
* Ship respawns after being hit 
* Effects
  * flames
  * explosions
* Requires HTML 5
* Works best with Chrome

Been spending time on getting the core of the game to the point where I can start adding functionality without breaking anything. I think I am there.

## Todo
* Enemy ships
* Scoring
* Spawn new roids and increase the level when the field is cleared
* Bonuses
* Shields
* Sound 

## Libraries/APIs Used
* ClojureScript
* core.async
* Transducers
* compojure

## Installation

Download from [clojuroids](https://github.com/tedfoye/clojuroids)

You will need [leiningen](https://github.com/technomancy/leiningen) to compile and run the game.

    $ lein cljsbuild once dev
    $ lein ring server
    
## Instructions

Click on the play area to bring focus to the canvas. 

Move the ship using:
* J - rotate left
* L - rotate right
* I - forward thrust
* K - reverse thrust

Use 'F' to fire.

## License

Copyright © 2014 Ted Foye

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
