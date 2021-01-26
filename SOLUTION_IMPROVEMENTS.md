# SOLUTION IMPROVEMENTS


1) To improve performance in latency sensitive environment, we should try to reduce object allocation rate. This can be acheived by 'warming' the Map objects to create placeholder objects which can then be set when in use. 

2) Currently the soution uses auto-boxing when working with the Maps.  We can rework the solution to use only primitive types, and use primitive collections library like trove4j, that way we can avoid object creation in the heap.
 
3) Minimising GC, and working with smaller heaps could make the performance better. We could shard the instances of the application to deal with certain ranges of order ids. This will allow us to distribute and scale multiple application instances where each OrderBook only deals with the sharded range of values. This approach could help scaling up horizontally by starting up more instances to deal with orders respectively

4) Currently all the methods are synchronized using the object's monitor, which will serialize all writes to the maps, can be improved by using explicit ReentrantReadWrite Lock.

5) The Solution's Read methods return materialized lists, these could be changed to streaming endpoints to allow for a less memory intensive way of consumption


