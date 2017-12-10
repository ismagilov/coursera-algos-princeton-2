import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseballElimination {
    private final int n;
    private final Map<String, Integer> idxByTeam = new LinkedHashMap<>();
    private final Map<Integer, String> teamByIdx = new LinkedHashMap<>();
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;

//    4
//    Atlanta       83 71  8  0 1 6 1
//    Philadelphia  80 79  3  1 0 0 2
//    New_York      78 78  6  6 0 0 0
//    Montreal      77 82  3  1 2 0 0

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);

        n = in.readInt();

        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];

        for (int i = 0; i < n; i++) {
            String teamName = in.readString();
            idxByTeam.put(teamName, i);
            teamByIdx.put(i, teamName);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();

            for (int j = 0; j < n; j++)
                games[i][j] = in.readInt();
        }

    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams
    public Iterable<String> teams() {
        return idxByTeam.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");
        if (!idxByTeam.containsKey(team)) throw new IllegalArgumentException("Missing team");

        return wins[idxByTeam.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");
        if (!idxByTeam.containsKey(team)) throw new IllegalArgumentException("Missing team");

        return losses[idxByTeam.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");
        if (!idxByTeam.containsKey(team)) throw new IllegalArgumentException("Missing team");

        return remaining[idxByTeam.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (null == team1 || team1.isEmpty()) throw new IllegalArgumentException("Empty team1");
        if (null == team2 || team2.isEmpty()) throw new IllegalArgumentException("Empty team2");

        if (!idxByTeam.containsKey(team1)) throw new IllegalArgumentException("Missing team1");
        if (!idxByTeam.containsKey(team2)) throw new IllegalArgumentException("Missing team2");

        return games[idxByTeam.get(team1)][idxByTeam.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team)  {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");
        if (!idxByTeam.containsKey(team)) throw new IllegalArgumentException("Missing team");

        if (!isTriviallyEliminated(team).isEmpty()) return true;

        return calculateElimination(team).isEliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (null == team || team.isEmpty()) throw new IllegalArgumentException("Empty team");
        if (!idxByTeam.containsKey(team)) throw new IllegalArgumentException("Missing team");

        ArrayList<String> trivElimBy = isTriviallyEliminated(team);
        if (!trivElimBy.isEmpty()) return trivElimBy;

        return calculateElimination(team).cert;
    }

    private Elimination calculateElimination(String team) {
        int teamIdx = idxByTeam.get(team);
        int matchesNum = n * (n - 1) / 2;
        int srcVertex = matchesNum + n;
        int targetVertex = srcVertex + 1;

        FlowNetwork flowNetwork = new FlowNetwork(matchesNum + n + 2);
        int curMatchVertex = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                flowNetwork.addEdge(new FlowEdge(curMatchVertex, matchesNum + i, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(curMatchVertex, matchesNum + j, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(srcVertex, curMatchVertex, games[i][j]));
                curMatchVertex++;
            }

            flowNetwork.addEdge(new FlowEdge(matchesNum + i, targetVertex, wins[teamIdx] + remaining[teamIdx] - wins[i]));
        }

        FordFulkerson ff = new FordFulkerson(flowNetwork, srcVertex, targetVertex);

        boolean isEliminated = false;
        for (FlowEdge e : flowNetwork.adj(srcVertex)) {
            if (e.flow() != e.capacity()) {
                isEliminated = true;
                break;
            }
        }

        ArrayList<String> cert = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (ff.inCut(matchesNum + i))
                cert.add(teamByIdx.get(i));
        }

        return new Elimination(isEliminated, !cert.isEmpty() ? cert : null);
    }

    private static class Elimination {
        private final boolean isEliminated;
        private final ArrayList<String> cert;

        public Elimination(boolean isEliminated, ArrayList<String> cert) {
            this.isEliminated = isEliminated;
            this.cert = cert;
        }
    }

    private ArrayList<String> isTriviallyEliminated(String team) {
        int teamIdx = idxByTeam.get(team);

        ArrayList<String> res = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (i == teamIdx) continue;

            if (wins[teamIdx] + remaining[teamIdx] < wins[i])
                res.add(teamByIdx.get(i));
        }

        return res;
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
