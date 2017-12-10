import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final String[] teams;


//    4
//    Atlanta       83 71  8  0 1 6 1
//    Philadelphia  80 79  3  1 0 0 2
//    New_York      78 78  6  6 0 0 0
//    Montreal      77 82  3  1 2 0 0

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);

        int num = in.readInt();
        teams = new String[num];

    }
    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        return null;
    }

    // number of wins for given team
    public int wins(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");

        return 0;
    }

    // number of losses for given team
    public int losses(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");

        return 0;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");

        return 0;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (null == team1 || team1.isEmpty()) throw new IllegalArgumentException("Empty team1");
        if (null == team2 || team2.isEmpty()) throw new IllegalArgumentException("Empty team2");

        return 0;
    }

    // is given team eliminated?
    public boolean isEliminated(String team)  {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");

        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");

        return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
