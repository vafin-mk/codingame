#include <iostream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
int main()
{
    int N;
    int min = 100000;
    cin >> N; cin.ignore();
    vector<int> arr(N);
    for (int i = 0; i < N; i++) {
        int Pi;
        cin >> Pi; cin.ignore();
        arr[i]=Pi;
    }
    sort(begin(arr), end(arr));
    for(vector<int>::size_type i = 1; i != arr.size(); i++) {
      int diff = arr[i] - arr[i-1];
      if (diff < min) min = diff;
    }
    cout << min << endl;
}