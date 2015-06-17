package FloodSim;

import sim.util.*;

public class Activity {

    final public static int STAY_HOME = 0;
    final public static int FOOD_CENTER = 1; 
    final public static int HEALTH_CENTER = 2;  
    final public static int TERMINAL_CENTER = 3;
    final public static int SOCIAL_RELATIVES = 4;
    final public static int VISIT_SOCIAL = 5;
    
    public GroundControl bestActivityLocation(Victim vm, GroundControl position, int id, Flood f) {
    	
    	if (id == STAY_HOME) {
            return vm.getHome();
            //System.out.println("home" + newL.getCampID());
        }
    	 else if(id == FOOD_CENTER){
             return betstLoc (vm.getHome(), f.foodCenter,f);
         }
    	 else if (id == HEALTH_CENTER){
             return betstLoc (vm.getHome(), f.healthCenters,f);
         }
    	 else if (id == TERMINAL_CENTER){
             return betstLoc (vm.getHome(), f.terminalCenter,f);
         }
    	 else if (id == SOCIAL_RELATIVES) {
    		 int l=vm.getFamily().getRelativesLocation().numObjs;
    		 if(l == 0){
                 return vm.getHome();
             }
    		 else return ((GroundControl)(vm.getFamily().getRelativesLocation().objs[f.random.nextInt(l)]));
    	 }
    	 else {
             return vm.getHome();
         }
    }
    
    private GroundControl betstLoc (GroundControl fLoc, Bag fieldBag, Flood f){
    	Bag newLoc = new Bag();
    	
    	double bestScoreSoFar = Double.POSITIVE_INFINITY;
        for (int i = 0; i < fieldBag.numObjs; i++) {
            GroundControl potLoc = ((GroundControl) fieldBag.objs[i]);

            double fScore = fLoc.distanceTo(potLoc);
            if (fScore > bestScoreSoFar) {
                continue;
            }

            if (fScore <= bestScoreSoFar) {
                bestScoreSoFar = fScore;
                newLoc.clear();
            }
            newLoc.add(potLoc);


        }
        GroundControl fu = null;
        if (newLoc != null) {
            int winningIndex = 0;
            if (newLoc.numObjs >= 1) {
                winningIndex = f.random.nextInt(newLoc.numObjs);
            }
            //System.out.println("other" + newLoc.numObjs);
          fu= (GroundControl) newLoc.objs[winningIndex];

        }
        return fu;
    }
    
    public GroundControl getNextTile(Flood f, GroundControl subgoal, GroundControl position) {

        // move in which direction?
        int moveX = 0, moveY = 0;
        int dx = subgoal.getX() - position.getX();
        int dy = subgoal.getY() - position.getY();
        if (dx < 0) {
            moveX = -1;
        } else if (dx > 0) {
            moveX = 1;
        }
        if (dy < 0) {
            moveY = -1;
        } else if (dy > 0) {
            moveY = 1;
        }
        //((FieldUnit) o).loc

        // can either move in Y direction or X direction: see which is better
        GroundControl xmove = ((GroundControl) f.allRegions.field[position.getX() + moveX][position.getY()]);
        GroundControl ymove = ((GroundControl) f.allRegions.field[position.getX()][position.getY() + moveY]);
             

        boolean xmoveToRoad = (f.roadGrid.get(xmove.getX(), xmove.getY())) > 0;
        boolean ymoveToRoad = (f.roadGrid.get(ymove.getX(), ymove.getX())) > 0;

        if (moveX == 0 && moveY == 0) { // we are ON the subgoal, so don't move at all!
            // both are the same result, so just return the xmove (which is identical)
            return xmove;
        } else if (moveX == 0) // this means that moving in the x direction is not a valid move: it's +0
        {
            return ymove;
        } else if (moveY == 0) // this means that moving in the y direction is not a valid move: it's +0
        {
            return xmove;
        } else if (xmoveToRoad == ymoveToRoad) { //equally good moves: pick randomly between them
            if (f.random.nextBoolean()) {
                return xmove;
            } else {
                return ymove;
            }
        } else if (xmoveToRoad && moveX != 0) // x is a road: pick it
        {
            return xmove;
        } else if (ymoveToRoad && moveY != 0)// y is a road: pick it
        {
            return ymove;
        } else if (moveX != 0) // move in the better direction
        {
            return xmove;
        } else if (moveY != 0) // yes
        {
            return ymove;
        } else {
            return ymove; // no justification
        }
    }
    
    //...
}
