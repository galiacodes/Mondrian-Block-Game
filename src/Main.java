//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        Block blockDepth2 = new Block(0,2);
//        blockDepth2.updateSizeAndPosition(16,0,0);
//        blockDepth2.printBlock();

//        Block blockDepth3 = new Block(0,3);
//        blockDepth3.updateSizeAndPosition(16,0,0);
//        Block b1 = blockDepth3.getSelectedBlock(3,5,2);
//        b1.printBlock();


        //testing flatten()
        Block testBlock = new Block(0, 2); // Creating a block with level 0 and maxDepth of 2
        testBlock.updateSizeAndPosition(16, 0, 0); // Updating size and position
        testBlock.printColoredBlock(); // Printing the colored block
    }
}