package com.example.ranking.common;


import java.util.List;

//data structure which holds elements of type T in their sorted order
//T is the type of actual element being stored in the data structure
//K is the type of its key
public interface Ranking<T , K> {
    //returns a sorted list of top k elements
    List<T> topK(int k);

    void add(T element);

    void update(K key , T newElement);

    void remove(K key);

}
