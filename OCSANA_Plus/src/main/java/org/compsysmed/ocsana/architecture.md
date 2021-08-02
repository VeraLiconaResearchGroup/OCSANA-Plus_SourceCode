# OCSANA software architecture
This file documents the architecture of the OCSANA software.
It may be of interest if you plan to read or modify the source code.

## Overall architecture
The architecture of OCSANA is divided into several categories of components.

### CyActivator
Cytoscape requires that apps provide a [`CyActivator`](internal/CyActivator.java), which sets up the menu options for the app and tells Cytoscape how to run it.

### Tasks
The work of OCSANA is done by a collection of Cytoscape Tasks, with code implemented in [`tasks`](tasks/).
Each run of the app creates an [`OCSANACoordinatorTask`](internal/tasks/OCSANACoordinatorTask.java), which gets configuration from the user.
It then creates an [`OCSANARunnerTask`](internal/tasks/OCSANARunnerTask.java) which accepts a little more user configuration and then runs the sequence of algorithm tasks in the correct order.
Each step of the OCSANA process is represented by its own Task which is called from the Runner.

The Coordinator and Runner classes take advantage of the Cytoscape `@Tunable` mechanism to get configuration from the user.
Due to idiosyncracies of the Cytoscape architecture, the other tasks *cannot* use `@Tunable`.
All configuration must be handled by the Coordinator, the Runner, and their member variables.

## Algorithms
Several steps of the OCSANA process are algorithmically non-trivial.
Rather than clutter the Task classes with the algorithm details, we create a separate category of Algorithm classes, with code implemented in [`algorithms`](algorithms).
Many of these algorithms have their own user-configurable parameters, which are again handled using the Cytoscape `@Tunable` mechanism.
See the implementation of the [MMCS algorithm](internal/algorithms/mhs/MMCSAlgorithm.java) for an example.

## UI
Most of the UI design is handled through the Cytoscape `@Tunable` mechanism, which spares us having to write a lot of Swing code by hand.
However, any UI components that we design are implemented in [`ui`](ui/).
