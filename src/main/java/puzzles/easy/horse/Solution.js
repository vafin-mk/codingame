var N = parseInt(readline());
const arr = [];
for (var i = 0; i < N; i++) {
    var pi = parseInt(readline());
    arr.push(pi);
}
var min=100000;
arr.sort((a, b) => (a - b));
for (var i = 1; i < arr.length; i++) {
    var diff = arr[i]-arr[i-1];
    if(diff < min) min = diff;
}
print(min);