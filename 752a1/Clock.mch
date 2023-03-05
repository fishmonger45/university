// Clock models time
MACHINE Clock 
VARIABLES
	time
CONSTANTS
	maxTime
PROPERTIES
	maxTime = 10

INVARIANT
	time: NATURAL
INITIALISATION
	time := 0

OPERATIONS

// Current time status
res <-- Time =
BEGIN
	res := time
END;

// Increase the time by one (1)
Tick = PRE time < maxTime THEN
	BEGIN
		time := time + 1
	END
END

END