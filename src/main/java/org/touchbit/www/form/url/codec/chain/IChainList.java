package org.touchbit.www.form.url.codec.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * interface for working with from url encoded array
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 04.03.2022
 */
public interface IChainList extends List<Object> {

    /**
     * @return sign that from array is not indexed
     */
    boolean isNotIndexed();

    /**
     * @return sign that indexed form array is not filled (has nullable values)
     */
    boolean isNotFilled();

    /**
     * {@link ArrayList} for working with form array
     * <p>
     *
     * @author Oleg Shaburov (shaburov.o.a@gmail.com)
     * Created: 04.03.2022
     */
    class Default extends ArrayList<Object> implements IChainList {

        /**
         * sign that form array is indexed
         */
        private final boolean isIndexed;

        /**
         * @param isIndexed - sign that form array is indexed
         */
        public Default(final boolean isIndexed) {
            this.isIndexed = isIndexed;
        }

        /**
         * @return sign that form array is not indexed
         */
        @Override
        public boolean isNotIndexed() {
            return !isIndexed;
        }

        /**
         * @return sign that indexed form array is not filled (has nullable values)
         */
        @Override
        public boolean isNotFilled() {
            return !isIndexed || this.stream().anyMatch(Objects::isNull);
        }

    }
}
