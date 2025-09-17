
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
    private int xCoord;
    private int yCoord;
    private int size; // height/width of the square
    private int level; // the root (outer most block) is at level 0
    private int maxDepth;
    private Color color;

    private Block[] children; // {UR, UL, LL, LR}

    public static Random gen = new Random(2);


    public Block() {}

    public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
        this.xCoord=x;
        this.yCoord=y;
        this.size=size;
        this.level=lvl;
        this.maxDepth = maxD;
        this.color=c;
        this.children = subBlocks;
    }



    /*
     * Creates a random block given its level and a max depth.
     *
     * (coords will all be initialized by default)
     */
    public Block(int lvl, int maxDepth) {
        this.level = lvl;
        this.maxDepth = maxDepth;

        if (lvl < maxDepth ) {
            if(gen.nextDouble() < Math.exp(-0.25 * lvl)) {
                this.children = new Block[4];
                for (int i = 0; i < 4; i++) {
                    this.children[i] = new Block(lvl + 1, maxDepth);

                }
            }
            else{
                Color[] blockColors = GameColors.BLOCK_COLORS;
                int randomIndex = gen.nextInt(blockColors.length);
                this.color = blockColors[randomIndex];
                this.children = new Block[0];
            }

        }
        else {
            Color[] blockColors = GameColors.BLOCK_COLORS;
            int randomIndex = gen.nextInt(blockColors.length);
            this.color = blockColors[randomIndex];
            this.children = new Block[0];
        }
    }


    /*
     * Updates size and position for the block and all of its sub-blocks, while
     * ensuring consistency between the attributes and the relationship of the
     * blocks.
     *
     *  The size is the height and width of the block. (xCoord, yCoord) are the
     *  coordinates of the top left corner of the block.
     */

    public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
        int temp = size;
        if (temp < 0){
            throw new IllegalArgumentException("Invalid block size.");
        }

        for (int i = this.level; i<this.getMaxDepth(); i++){
            if(temp % 2 != 0){
                throw new IllegalArgumentException("input size is not valid.");
            }
            temp/=2;
        }

        this.size = size;
        this.xCoord = xCoord;
        this.yCoord = yCoord;

        if (this.children.length != 0) {
            int newSize = size / 2;
            children[0].updateSizeAndPosition(newSize, xCoord+newSize, yCoord);
            children[1].updateSizeAndPosition(newSize, xCoord, yCoord);
            children[2].updateSizeAndPosition(newSize, xCoord, yCoord+newSize);
            children[3].updateSizeAndPosition(newSize, xCoord+newSize, yCoord+newSize);

        }
    }


    /*
     * Returns a List of blocks to be drawn to get a graphical representation of this block.
     *
     * This includes, for each undivided Block:
     * - one BlockToDraw in the color of the block
     * - another one in the FRAME_COLOR and stroke thickness 3
     *     *
     */
    public ArrayList<BlockToDraw> getBlocksToDraw() {
        ArrayList<BlockToDraw> blocksToDrawList = new ArrayList<>();

        if (this.children.length == 0) {
            blocksToDrawList.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
            blocksToDrawList.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
        }
        else {
            for (Block child : this.children) {
                blocksToDrawList.addAll(child.getBlocksToDraw());
            }
        }

        return blocksToDrawList;
    }



    public BlockToDraw getHighlightedFrame() {
        return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
    }



    /*
     * Return the Block within this Block that includes the given location
     * and is at the given level. If the level specified is lower than
     * the lowest block at the specified location, then return the block
     * at the location with the closest level value.
     *
     * The location is specified by its (x, y) coordinates. The lvl indicates
     * the level of the desired Block. Note that if a Block includes the location
     * (x, y), and that Block is subdivided, then one of its sub-Blocks will
     * contain the location (x, y) too. This is why we need lvl to identify
     * which Block should be returned.
     *
     * Input validation:
     * - this.level <= lvl <= maxDepth (if not throw exception)
     * - if (x,y) is not within this Block, return null.
     */



    public Block getSelectedBlock(int x, int y, int lvl) {

        if (lvl < this.level || lvl > this.maxDepth) {
            throw new IllegalArgumentException("Selected level is out of bounds.");
        }

        if (this.level == lvl || this.children.length == 0) {
            return this;
        }

        if (!(x >= this.xCoord && x < this.xCoord + this.size && y >= this.yCoord && y < this.yCoord + this.size)) {
            return null;
        }

        int p1 = this.xCoord +this.size/2;
        int p2 = this.yCoord +this.size/2;

        if(x<p1){
            if(y<p2){
                return this.children[1].getSelectedBlock(x,y,lvl);
            }
            else{
                return this.children[2].getSelectedBlock(x,y,lvl);
            }
        }
        else{
            if(y<p2){
                return this.children[0].getSelectedBlock(x,y,lvl);
            }
            else{
                return this.children[3].getSelectedBlock(x,y,lvl);
            }
        }
    }


    /*
     * Swaps the child Blocks of this Block.
     * If input is 1, swap vertically. If 0, swap horizontally.
     * If this Block has no children, do nothing.
     *
     */


    public void reflect(int direction) {
        if (direction != 1 && direction != 0) {
            throw new IllegalArgumentException("Invalid direction parameter. Choose 0 or 1.");
        }
        if (this.children.length == 0) {
            return;
        }

        int[] indices;
        if (direction == 0) {
            indices = new int[]{3, 2, 1, 0};
        } else {
            indices = new int[]{1, 0, 3, 2};
        }

        Block[] temp = new Block[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = this.children[indices[i]];
        }

        this.children = temp;
        for (Block child : this.children) {
            child.reflect(direction);
        }
        this.updateSizeAndPosition(this.size, xCoord, yCoord);
    }



    /*
     * Rotate this Block and all its descendants.
     * If the input is 1, rotate clockwise. If 0, rotate
     * counterclockwise. If this Block has no children, do nothing.
     */



    public void rotate(int direction) {
        if (direction != 1 && direction != 0) {
            throw new IllegalArgumentException("Invalid direction parameter. Choose 0 or 1.");
        }
        if (this.children == null || this.children.length <4) {
            return;
        }

        int[] indices;
        if (direction == 0) {
            indices = new int[]{3, 0, 1, 2};
        } else {
            indices = new int[]{1, 2, 3, 0};
        }
        Block[] temp1 = new Block[4];
        Block[] temp2 = new Block[4];

        for (int i = 0; i < 4; i++) {
            temp2[i] = this.children[indices[i]];
        }

        //this.children = temp;
        for (int i = 0; i < 4; i++) {
            temp1[i] = temp2[i];
            temp1[i].rotate(direction);
        }
        this.children = temp1;
        this.updateSizeAndPosition(this.size, xCoord, yCoord);
    }




    /*
     * Smash this Block.
     *
     * If this Block can be smashed,
     * randomly generate four new children Blocks for it.
     * Return True if this Block was smashed and False otherwise.
     *
     */
    public boolean smash() {
        if(this.level !=0  && this.level < this.maxDepth){
            this.children = new Block[4];
            this.children[0] = new Block(this.level+1, this.maxDepth);
            this.children[1] = new Block(this.level+1, this.maxDepth);
            this.children[2] = new Block(this.level+1, this.maxDepth);
            this.children[3] = new Block(this.level+1, this.maxDepth);
            this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
            return true;
        }
        return false;
    }


    /*
     * Return a two-dimensional array representing this Block as rows and columns of unit cells.
     *
     * Return and array arr where, arr[i] represents the unit cells in row i,
     * arr[i][j] is the color of unit cell in row i and column j.
     *
     * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
     */

    public Color[][] flatten() {
        int flatArr = (int) Math.pow(2, this.maxDepth - this.level);
        Color[][] colorArr = new Color[flatArr][flatArr];

        if (this.children.length == 0 || this.level == maxDepth) {
            Color fill = this.color;
            int i = 0;
            while (i < flatArr) {
                int j = 0;
                while (j < flatArr) {
                    colorArr[i][j] = fill;
                    j++;
                }
                i++;
            }
        } else {
            int halfSize = flatArr / 2;
            Color[][] upperLeft = this.children[0].flatten();
            Color[][] upperRight = this.children[1].flatten();
            Color[][] lowerLeft = this.children[2].flatten();
            Color[][] lowerRight = this.children[3].flatten();
//3
            int i = 0;
            while (i < halfSize) {
                int j = 0;
                while (j < halfSize) {
                    colorArr[i][j] = upperRight[i][j];
                    colorArr[i][j + halfSize] = upperLeft[i][j];
                    colorArr[i + halfSize][j] = lowerLeft[i][j];
                    colorArr[i + halfSize][j + halfSize] = lowerRight[i][j];
                    j++;
                }
                i++;
            }
        }
        return colorArr;
    }



    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getLevel() {
        return this.level;
    }



    public String toString() {
        return String.format("pos=(%d,%d), size=%d, level=%d"
                , this.xCoord, this.yCoord, this.size, this.level);
    }


    public void printBlock() {
        this.printBlockIndented(0);
    }

    private void printBlockIndented(int indentation) {
        String indent = "";
        for (int i=0; i<indentation; i++) {
            indent += "\t";
        }

        if (this.children.length == 0) {
            // it's a leaf. Print the color!
            String colorInfo = GameColors.colorToString(this.color) + ", ";
            System.out.println(indent + colorInfo + this);
        } else {
            System.out.println(indent + this);
            for (Block b : this.children)
                b.printBlockIndented(indentation + 1);
        }
    }


    private static void coloredPrint(String message, Color color) {
        System.out.print(GameColors.colorToANSIColor(color));
        System.out.print(message);
        System.out.print(GameColors.colorToANSIColor(Color.WHITE));
    }

    public void printColoredBlock(){
        Color[][] colorArray = this.flatten();
        for (Color[] colors : colorArray) {
            for (Color value : colors) {
                String colorName = GameColors.colorToString(value).toUpperCase();
                if(colorName.length() == 0){
                    colorName = "\u2588";
                }else{
                    colorName = colorName.substring(0, 1);
                }
                coloredPrint(colorName, value);
            }
            System.out.println();
        }
    }

}
