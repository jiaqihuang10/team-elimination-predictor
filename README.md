# Team Elimination Predictor
Determine which teams in a sports league division have been eliminated from the playoffs.

To interactively provide test inputs, run the program with

	`java BaseballElimination`

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with

	`java BaseballElimination file.txt`

   where file.txt is replaced by the name of the text file.

   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:

   ```
	<number of teams in division1>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	<number of teams in division2>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
```
   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.

   ### sample run
   The output of a model solution on the division above is given in the listing below.
   ```
   Reading input values from division1.txt.
   Teams eliminated: [Detroit]
   ```

   FordFulkerson, FlowNetwork, and FlowEdge classes from the Algorithms (Sedgewick) used.
