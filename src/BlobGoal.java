import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override


    public int score(Block board) {
        Color[][] flattened = board.flatten();
        int[] numBlobs = new int[flattened.length * flattened.length];
        int c = 0;
        for(int i=0; i< flattened.length; i++){
            for(int j=0; j< flattened.length; j++){
                if(flattened[i][j].equals(this.targetGoal)){
                    numBlobs[c] = undiscoveredBlobSize(i,j,flattened, new boolean[flattened.length][flattened.length]);
                    c++;
                }
            }
        }

        int maxBlob = 0;
        for(int blob: numBlobs){
            if (blob>maxBlob){
                maxBlob = blob;
            }
        }
        return maxBlob;
    }


    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }




    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
        if(i<0 || j<0 || i>= unitCells.length || j>= unitCells[i].length ) {
            return 0;
        }

        if (!(unitCells[i][j].equals(this.targetGoal)) || visited[i][j]){
            return 0;
        }

        visited[i][j] = true;
        int x = 1;

        x += this.undiscoveredBlobSize(i,j-1,unitCells,visited);
        x += this.undiscoveredBlobSize(i,j+1,unitCells,visited);
        x += this.undiscoveredBlobSize(i-1,j,unitCells,visited);
        x += this.undiscoveredBlobSize(i+1,j,unitCells,visited);

        return x;

    }
}




