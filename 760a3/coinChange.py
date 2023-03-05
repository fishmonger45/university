# -*- coding: utf-8 -*-
import math

cache = []


def optimalCoinChange(x, denoms, y=True):
    # Function finds the minimum number of coins required to change a monetary
    # amount.
    # Inputs:
    # x = amount of money to be given in coins, given as an INTEGER, in cents.
    #           e.g. $1.35 is input as 135
    # denoms = denominations of coins available, in INTEGER cents,
    #           given as a ROW VECTOR.
    # Output:
    # numCoins = optimal number of coins used to find x
    # Andreas Knapp (akna890)

    if y == True:
        denoms.sort()
        for amount in range(x+1):
            cache.append(0)
            cache[amount] = optimalCoinChange(amount, denoms, False)
        return cache[x]
    else:
        # Invalid coin selection
        if x < 0:
            return math.inf
        # No coins required for zero
        if x == 0:
            return 0

        candidates = []
        for d in denoms:
            candidates.append(1 + cache[x-d])

        return min(candidates)


if __name__ == "__main__":
    x = 12
    denoms = [1, 2, 3]

    for d in denoms:
        if d <= 0 or isinstance(d, float):
            print(
                f"Denominations cannot contain a negative number or float: {d}")
            exit(1)

    if x < 0:
        print(f"Cannot give change for a negative value: {x}")
        exit(1)

    if isinstance(x, float):
        print(f"Input x must be nonfloat : {x}")
        exit(1)

    # LETS DO IT
    print(optimalCoinChange(x, denoms))
