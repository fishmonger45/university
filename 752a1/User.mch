MACHINE User
SETS
	USER
CONSTANTS
	maxCash
PROPERTIES
	maxCash = 100
VARIABLES
	balance
INVARIANT
	balance: USER --> NATURAL 
INITIALISATION
	balance := USER * {0}
OPERATIONS

// Users work to earn a constant amount of currency (which is statically set to 10)
Work(u) = PRE u: USER & balance(u) < maxCash THEN
	LET amount BE amount = 10 IN
		balance(u) := min({balance(u) + amount, maxCash}) 
	END 
END;

SetBalance(u, n) = PRE u: USER & n: NATURAL THEN
	balance(u) := n
END

END
