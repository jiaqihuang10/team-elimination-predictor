/* TeamElimination.java

   To interactively provide test inputs, run the program with
	java TeamElimination

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with
	java TeamElimination file.txt
   where file.txt is replaced by the name of the text file.

   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:

	<number of teams in division1>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	<number of teams in division2>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...

   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.

*/

import edu.princeton.cs.algs4.*;

import java.util.*;
import java.io.File;

//Do not change the name of the TeamElimination class
public class TeamElimination{
	// We use an ArrayList to keep track of te eliminated teams.
	public ArrayList<String> eliminated = new ArrayList<String>();
	private int team_num;
		//to save team names and their indices
	private	String [] teams;
	private int[][] eachToPlay; // 2-D array to save the games to play vs each other team
	private	int[] wins; //to save the number of games won
	private	int[] toPlay; //to save the number of games to play
	private int totalGameLeft = 0;
	ArrayList<Map.Entry<Integer,Integer>> gameVertices = new ArrayList<Map.Entry<Integer,Integer>>(); // each pair of teams other than the team given to test elimination
	// the most games the given team can win is less than the games another team's won,
	//then fast elimination, whitout using the FordFulkerson
	boolean fast_eliminated = false;


	/* TeamElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/
	public TeamElimination (Scanner s){
		readData(s);

		for(int i = 0; i < team_num; i++) {
			createGameVertices(i);
			//System.out.println("# of game vertices: " + gameVertices.size());
			int totalVertices = 2+gameVertices.size()+team_num-1;

			FlowNetwork G = createFlowNetwork(i, totalVertices);
			if (fast_eliminated == true) {
				eliminated.add(teams[i]);
				reset();
				continue;
			}

			FordFulkerson f = new FordFulkerson(G,0,totalVertices-1);

			double maxFlow = f.value();
			//System.out.println("max flow: " + maxFlow + ", games left: " + totalGameLeft);
			if (maxFlow < totalGameLeft) {
				eliminated.add(teams[i]);
			}
			reset();
		}
	}

	// create the flownetwork for the team given
	private FlowNetwork createFlowNetwork(int team, int totalV) {
		//System.out.println ("total vertices : " + totalV);
		FlowNetwork f = new FlowNetwork(totalV);

		int count = 0; // as index of vertices (from 0 - 11);

		//create edges from source to game vertices. then add them to the flowNetwork
		//meanwhile create edges from game vertices to team vertices
		for (int i = 0; i < gameVertices.size(); i++) {
			// FlowEdge(int v, int w, double capacity
			int team1 = gameVertices.get(i).getKey();
			int team2 = gameVertices.get(i).getValue();
			int pairs = gameVertices.size();
			//source to game
			FlowEdge e1 = new FlowEdge(0, ++count, eachToPlay[team1][team2]);
			f.addEdge(e1);
			totalGameLeft += eachToPlay[team1][team2];
			//System.out.println("vertex: " + count);
			//game to team
			FlowEdge e2 = new FlowEdge(count,team1<team ? (team1+pairs+1) : (team1+pairs),Integer.MAX_VALUE);
			FlowEdge e3 = new FlowEdge(count,team2<team ? (team2+pairs+1) : (team2+pairs),Integer.MAX_VALUE);
			f.addEdge(e2);
			f.addEdge(e3);
		}
		//add edges from team to sink
		for (int i = 0; i < team_num; i++) {
			if (i != team) {
				int cap = wins[team] + toPlay[team] - wins[i];
				if (cap < 0) {
					fast_eliminated = true;
					return f;
				}
				FlowEdge e = new FlowEdge(++count,totalV-1, cap);
				f.addEdge(e);
			}

		}

		//System.out.println(f);

		return f;
	}

	//create the game vertices for the team given ( each pair of teams other than the team given - Choose 2 from(team_num-1))
	void createGameVertices(int team) {

		int count = 0;
		for (int i = 0; i < team_num; i++) {
			if (i == team) {
				;
			} else {
				for (int j = i+1; j<team_num; j++) {
					if (j == team) { //vs the team given, not needed in this flownetwork
						;
					} else {
						Map.Entry<Integer,Integer> pair = new AbstractMap.SimpleEntry<>(i,j);
						gameVertices.add(pair);
						//System.out.println(teams[i] + " vs " + teams[j] + " to play: " + eachToPlay[pair.getKey()][pair.getValue()]);
					}

			}
			}

		}
	}

	private void readData(Scanner s) {
		team_num = s.nextInt(); 		//total team number
		//System.out.println("team numbers: " + team_num);
		teams = new String[team_num];
		wins = new int[team_num];
		toPlay = new int[team_num];
		eachToPlay = new int[team_num][team_num];

		for (int i = 0; i< team_num; i++ ) {
			teams[i] = s.next();
			wins[i] = s.nextInt();
			toPlay[i] = s.nextInt();
			for (int j = 0; j < team_num; j++) {
				eachToPlay[i][j] = s.nextInt();
			}
			if (s.hasNextLine()) {
				s.nextLine(); //move to the front of nextline
			}
		}

	}

	//clear all the data for the flowNetwork done, and ready for next
	private void reset() {
		totalGameLeft = 0;
		gameVertices.clear();
		fast_eliminated = false;
	}

	private void print_team_data() {
		for (int i = 0; i < team_num; i++) {
			System.out.print(teams[i] + " wins: " + wins[i] + " to plays: " + toPlay[i]);
			for (int j = 0; j < team_num; j++ ) {
				System.out.print(" " + eachToPlay[i][j]);
			}
			System.out.println();

		}
	}


	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");

		}

		int graphNum = 0;
		double totalTimeSeconds = 0;

		//Read until EOF is encountered (or an error occurs)
		while(true) {
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			//System.out.printf("Reading Division %d data\n", graphNum);
			long startTime = System.currentTimeMillis();
			TeamElimination be = new TeamElimination(s);
			//be.print_team_data();
			System.out.println();
			if (be.eliminated.size() == 0)
				System.out.println("No teams have been eliminated.");
			else
				System.out.println("Teams eliminated: " + be.eliminated);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			System.out.printf("Processed. Time(seconds): %.2f\n", totalTimeSeconds);
		}
	}

}
