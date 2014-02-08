/**
 * Class used to compress all symmetric 4x4 configurations into a single 64-bit long.
 * @author Kerrick Staley
 *
 */
public class Canonicalize
{
    static void numericFlip(byte[] board)
    {
        for (int i = 0; i < 16; i++)
            if (board[i] != 0)
                board[i] = (byte)(17 - board[i]);
    }
    
    static void cycle4(byte[] board, int a, int b, int c, int d)
    {
        byte tmp = board[a];
        board[a] = board[b];
        board[b] = board[c];
        board[c] = board[d];
        board[d] = tmp;
    }
    
    static void rotate(byte[] board)
    {
        cycle4(board, 0, 3, 15, 12);
        cycle4(board, 1, 7, 14, 8);
        cycle4(board, 2, 11, 13, 4);
        cycle4(board, 5, 6, 9, 10);
    }
    
    static void cycle2(byte[] board, int a, int b)
    {
        byte tmp = board[a];
        board[a] = board[b];
        board[b] = tmp;
    }
    
    static void vmirror(byte[] board)
    {
        cycle2(board, 0, 3);
        cycle2(board, 1, 2);
        cycle2(board, 4, 7);
        cycle2(board, 5, 6);
        cycle2(board, 8, 11);
        cycle2(board, 9, 10);
        cycle2(board, 12, 15);
        cycle2(board, 13, 14);
    }
    
    static void dmirror(byte[] board)
    {
        cycle2(board, 0, 15);
        cycle2(board, 1, 11);
        cycle2(board, 2, 7);
        cycle2(board, 4, 14);
        cycle2(board, 5, 10);
        cycle2(board, 8, 13);
    }
    
    static void shuffle1(byte[] board)
    {
        cycle2(board, 0, 5);
        cycle2(board, 1, 4);
        cycle2(board, 2, 7);
        cycle2(board, 3, 6);
        cycle2(board, 8, 13);
        cycle2(board, 9, 12);
        cycle2(board, 10, 15);
        cycle2(board, 11, 14);
    }
    
    static void shuffle2(byte[] board)
    {
        cycle2(board, 1, 2);
        cycle2(board, 7, 11);
        cycle2(board, 14, 13);
        cycle2(board, 8, 4);
        cycle2(board, 5, 10);
        cycle2(board, 6, 9);
    }
    
    /*
     * Takes a byte[] representing the board. The mapping from
     * location on the board to position in this vector is as follows:
     * 
     *  0  1  2  3
     *  4  5  6  7
     *  8  9 10 11
     * 12 13 14 15
     * 
     * Returns a uint64_t representing the board. All boards that are
     * equivalent to one another (under the various equivalence operations
     * defined above) will yield the same value when passed to this
     * function.
     */
    
    long canonicalize(byte[] board)
    {
        /*
         * Check for an empty board, which is a special case. We assign the
         * representation 0xFFFF to the empty board; 0xFFFF is the smallest
         * value that isn't otherwise a valid representation.
         */
        byte tmp = 0;
        for (byte c : board)
            tmp |= c;
        if (tmp == 0)
            return 0xFFFF;
        
        long rv = -1;
        
        for (int nf = 0; nf < /*2*/1; nf++)
        {
        //	numericFlip(board);
        
            /*
             * We need to find the smallest value in the board so it can be
             * used later.
             */
            int smallest = 17;
            for (int i = 0; i < 16; i++)
                if (board[i] != 0 && board[i] < smallest)
                    smallest = board[i];
        
        for (int rot = 0; rot < 4; rot++)
        {
        rotate(board);
        
        for (int vm = 0; vm < 2; vm++)
        {
        vmirror(board);
        
        for (int dm = 0; dm < 2; dm++)
        {
        dmirror(board);
        
        for (int s1 = 0; s1 < 2; s1++)
        {
        shuffle1(board);
        
        for (int s2 = 0; s2 < 2; s2++)
        {
        shuffle2(board);
            
            /*
             * If the smallest number is not in one of the first two
             * positions, skip this arrangement of the board.
             */
            if (board[0] != smallest && board[1] != smallest)
                continue;
            
            /*
             * The following is a rather inelegant way to map the board onto
             * a unique 64-bit integer, given that the smallest number in
             * the board is either board[0] or board[1].
             * 
             * The representation is guaranteed to be unique because it
             * contains enough information to fully reconstruct the board.
             * 
             * We use the following format to compress the board:
             * bit 63: 0 if board[0] == smallest, 1 if board[1] == smallest
             * bits 60-62: (smallest - 1) % 8
             * bits 0-59:  15 4-bit numbers representing (number - 1) for all the numbers in the board except smallest.
             *             If smallest >= 8, we invert all bits in this section. This is needed because otherwise, there's
             *             not enough information to fully reconstruct the board, because the value of smallest can't be
             *             determined. It's easy to determine whether it's been inverted, because there will be multiple
             *             numbers whose value is 15.
             */
            
            long val = (board[0] == smallest ? 0 : 1);
            
            val <<= 3;
            val |= (smallest - 1) % 8;
            
            for (int i = 0; i < 16; i++)
            {
                if (board[i] == smallest)
                    // this will happen when either i = 0 or i = 1;
                    continue;
                
                val <<= 4;
                if (board[i] != 0)
                    val += board[i] - 1;
            }
            
            if (smallest >= 8)
                val ^= (1L << 60) - 1;
            
            // we will return the smallest val we find from all the possible rearrangements
            // we could just do a straight comparison, but I think it makes more sense to do it in an unsigned fashion
            // unsigned compare stolen from http://www.javamex.com/java_equivalents/unsigned_arithmetic.shtml
            if ((val < rv) ^ ((val < 0) != (rv < 0)))
                rv = val;
        }}}}}}
        
        return rv;
    }
}
