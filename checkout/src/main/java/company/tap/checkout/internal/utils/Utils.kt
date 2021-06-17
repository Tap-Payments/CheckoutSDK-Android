package company.tap.checkout.internal.utils

/**
 * Created by AhlaamK on 6/17/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
 class Utils {
    /**
         * The type List.
         */
        object List {
            /**
             * Filter t.
             *
             * @param <E>    the type parameter
             * @param <T>    the type parameter
             * @param list   the list
             * @param filter the filter
             * @return the t
            </T></E> */
            fun <E, T : kotlin.collections.List<E>?> filter(
                list: T,
                filter: Filter<E>
            ): T {
                val result: T = try {
                    val listClass: T = list
                    listClass as T
                } catch (e: IllegalAccessException) {
                    return list
                } catch (e: InstantiationException) {
                    return list
                }
                for (element in list!!) {
                    if (filter.isIncluded(element)) {
                       // result.add(element)
                    }
                }
                return result
            }
            /**
             * The interface Filter.
             *
             * @param <T> the type parameter
            </T> */
            interface Filter<T> {
                /**
                 * Is included boolean.
                 *
                 * @param object the object
                 * @return the boolean
                 */
                fun isIncluded(`object`: T): Boolean
            }

        }

}
