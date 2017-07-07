using System;
using System.Linq;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution
{
    static void Main(string[] args)
    {
        int N = int.Parse(Console.ReadLine());
        int min = 100000;
        var list = new List<int>();
        for (int i = 0; i < N; i++)
        {
            list.Add(int.Parse(Console.ReadLine()));
        }
        list.Sort();
        for (int i = 1; i < list.Count; i++)
        {
            var diff = list[i] - list[i-1];
            if (diff < min) min = diff;
        }

        Console.WriteLine(min);
    }
}