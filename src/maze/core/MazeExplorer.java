package maze.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import core.Direction;
import core.Pos;

public class MazeExplorer {
	private Maze m;
	private Pos location;
	private TreeSet<Pos> treasureFound;
	private MazeExplorer goal;
	
	public MazeExplorer(Maze m, Pos location) {
		this.m = m;
		this.location = location;
		treasureFound = new TreeSet<>();
	}
	
	public Pos getLocation() {return location;}

	public Set<Pos> getAllTreasureFromMaze() {
		return m.getTreasures();
	}

	public Set<Pos> getAllTreasureFound() {
		return treasureFound;
	}

	public int getNumTreasuresFound() {
		return treasureFound.size();
	}

	public MazeExplorer getGoal() {
		if (goal == null) {
			goal = m.getGoal();
		}
		return goal;
	}

	public ArrayList<MazeExplorer> getSuccessors() {
		ArrayList<MazeExplorer> result = new ArrayList<MazeExplorer>();
		for(int i = 0; i < Direction.values().length; i++) {
			Pos value = Direction.values()[i].successor(getLocation());
			if(m.within(value) && !m.blocked(value,getLocation())){
				MazeExplorer create = new MazeExplorer(getM(), value);
				result.add(create);
			}
		}
		return result;
	}
	// TODO: It should add as a successor every adjacent, unblocked neighbor square.
	// I added a comment for demonstration purposes.
	/* Build Maze Explorers and add to result */
	/* Add Treasure found to the sucussors found */
	
	public void addTreasures(Collection<Pos> treasures) {
		treasureFound.addAll(treasures);
	}
	
	public String toString() {
		StringBuilder treasures = new StringBuilder();
		for (Pos t: treasureFound) {
			treasures.append(";");
			treasures.append(t.toString());
		}
		return "@" + location.toString() + treasures;
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MazeExplorer that) {
			return this.location.equals(that.location) && this.treasureFound.equals(that.treasureFound);
		} else {
			return false;
		}
	}

	public boolean achievesGoal() {
		return this.equals(getGoal());
	}

	public Maze getM() {
		return m;
	}
}
