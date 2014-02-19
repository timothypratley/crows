# Crows vs Ravens

Crows vs Ravens is a Massively Multiplayer Browser Based game where you control the behaviors of entities in a persistent world.


## Usage

`lein run`

To reload code while working, execute repl/scratch.clj


## License

Copyright © 2013 Timothy Pratley

Distributed under the Eclipse Public License, the same as Clojure.


## Design

Stream a heirachical view of a world over wamp topics.
Upon initial connection the current state is sent with a version number.


## TODO

* Refresh page when version mismatch
* Send speed and interpolate between poses
* Design the heirachicy rules
* Model loading
* Height(x,y) calculation
