read N
MIN=100000
arr=()
for((i=0 ; i<N ; i++)); do
  read h
  arr[h]=1
done

if [[ ${#arr[*]} -ne $N ]]; then
  echo 0
  exit; fi

last=-1
for h in ${!arr[@]}; do
  if [[ $last -ne -1 && $(($h-$last)) -lt $MIN ]]; then
    MIN=$(($h-$last)); fi
  last=$h
done

echo $MIN