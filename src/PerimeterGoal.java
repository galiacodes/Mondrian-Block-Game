
import java.awt.Color;

public class PerimeterGoal extends Goal{

    public PerimeterGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] flatArr = board.flatten();
        int score = 0;

        for(int i = 0; i< flatArr.length; i++){
            for(int j=0; j<flatArr[i].length; j++){
                if(i==0 && flatArr[i][j].equals(this.targetGoal)){
                    if(j==flatArr[i].length-1 || j == 0){
                        score +=2;
                    }
                    else{
                        score +=1;
                    }
                } else if (i == flatArr.length-1 && flatArr[i][j].equals(this.targetGoal)) {
                    if(j==flatArr[i].length-1 || j==0){
                        score+=2;
                    }
                    else{
                        score+=1;
                    }
                }
                else if((j==0 || j==flatArr[i].length-1) && flatArr[i][j].equals(this.targetGoal)){
                    score+=1;
                }
            }
        }
        return score;
    }

    @Override
    public String description() {
        return "Place the highest number of " + GameColors.colorToString(targetGoal)
                + " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
    }

}
