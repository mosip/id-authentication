package io.mosip.authentication.service.impl.indauth.builder;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * The HexSpliterator used to iterate the boolean Spliterator and produce
 * Integer Stream out of that. Here Four bits (booleans) are processed and
 * converted to a hexadecimal Integer value.
 * 
 * @author Loganathan Sekar
 */
public class HexSpliterator implements Spliterator<Integer> {
    
    private static final Integer TWO = 2;
    private static final Integer FOUR = 4;
    /** The boolean spliterator. */
    private Spliterator<Boolean> booleanSpliterator;

    /**
     * Instantiates a new hex spliterator.
     *
     * @param booleanSpliterator the boolean spliterator
     */
    public HexSpliterator(Spliterator<Boolean> booleanSpliterator) {
        this.booleanSpliterator = booleanSpliterator;
    }

    /* (non-Javadoc)
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     */
    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        StringBuilder buffer = new StringBuilder();
        if(IntStream.range(0, FOUR).allMatch(i -> tryAdvanceBoolean(buffer))) {
            String binaryStr = buffer.toString();
            int intVal = Integer.parseInt(binaryStr, TWO);
            action.accept(intVal);
            return true;
        }
        return false;
    }

    /**
     * Try advance boolean.
     *
     * @param buffer the buffer
     * @return true, if successful
     */
    private boolean tryAdvanceBoolean(StringBuilder buffer) {
        return booleanSpliterator.tryAdvance(b -> buffer.insert(0, b ? 1 : 0));
    }

    /* (non-Javadoc)
     * @see java.util.Spliterator#trySplit()
     */
    @Override
    public Spliterator<Integer> trySplit() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Spliterator#estimateSize()
     */
    @Override
    public long estimateSize() {
        return booleanSpliterator.estimateSize() / FOUR;
    }

    /* (non-Javadoc)
     * @see java.util.Spliterator#characteristics()
     */
    @Override
    public int characteristics() {
        return booleanSpliterator.characteristics();
    }

}
