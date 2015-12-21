BEGIN {
    c = 0;
    sum = 0;
    variance = 0;
  }
  $1 ~ /^[0-9]*(\.[0-9]*)?$/ {
    if ($1 > 0) {
        a[c++] = $1;
        sum += $1;
    }
  }
  END {
    ave = sum / c;
    if( (c % 2) == 1 ) {
      median = a[ int(c/2) ];
    } else {
      median = ( a[c/2] + a[c/2-1] ) / 2;
    }

    for (i in a)
    {
        variance +=  (ave - a[i])^2;
    }
    variance = variance / c;
    standardDeviation = sqrt(variance);
    OFS="\t";
    print sum, c, ave, median, a[0], a[c-1], standardDeviation;
  }
