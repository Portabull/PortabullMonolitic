package com.portabull.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {

    public static void main(String args[]) {


        System.out.print("Enter Size:");
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();
        List<Integer> list = new ArrayList(size);

        for (int i = 0; i < size; i++) {
            System.out.print("Enter " + (i + 1) + " Element:");
            list.add(scanner.nextInt());
        }

        int diffMaxSize = getDiffence(list);
        System.out.print("Size: "+diffMaxSize);
        System.out.println("");


    }

    private static int getDiffence(List<Integer> list) {
        List<List<Integer>> lists = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.size() > i + 1) {
                int y = list.get(i) - list.get(i + 1);
                if (y == 0 || y == 1 || y == -1) {
                    if (lists.size() == 0) {
                        lists.add(new ArrayList<>());
                    }
                    List<Integer> seqs = lists.get(lists.size() - 1);
                    if (seqs.size() > 0) {
                        Integer val = seqs.get(seqs.size() - 1);
                        if (val != list.get(i)) {
                            seqs.add(list.get(i));
                        }
                        seqs.add(list.get(i + 1));
                    } else {
                        seqs.add(list.get(i));
                        seqs.add(list.get(i + 1));
                    }
                } else {
                    List<Integer> seqs = new ArrayList<>();
                    lists.add(seqs);
                }
            }

        }
        int size = 0;
        for (int i = 0; i < lists.size(); i++) {
            if (size < lists.get(i).size()) {
                size = lists.get(i).size();
            }
        }
        return size;
    }

}
