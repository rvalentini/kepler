# kepler

Interactive website explaining the three Kepler laws of planetary motion using animations. 

## Overview

The website is written in ClojureScript, using the following dependencies: 

+ [Figwheel Main](https://github.com/bhauman/figwheel-main) for building and the awesome interactive development experience it offers 
+ [Reagent](https://github.com/reagent-project/reagent) as React wrapper for component modularization and state handling
+ [Quil](http://quil.info/?example=fireworks) A Processing based library for interactive drawings and animations


## Development

To get an interactive development environment run:

    lein fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL.

To create a production build run:

	lein clean
	lein fig:min

## License

Copyright © 2021 Riccardo Valentini
Distributed under the MIT License.
