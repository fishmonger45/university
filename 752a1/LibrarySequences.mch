MACHINE LibrarySequences(T)

OPERATIONS

// Find the index of the first head to tail element matching x
// Precondition that the element exists within the list
// Note that sequences begin at index one (1)
index <-- IndexOf(x, xs) = PRE x: T & xs: seq(T) & {x} <: ran(xs)	THEN
	IF x = first(xs) THEN
		index := 1
	ELSE
		index := 1 + (IndexOf(x, (tail(xs)))) 
	END
END;

// Remove an element at index i
// Note that sequences begin at index one (1)
ys <-- RemoveIndex(i, xs) = PRE i: NATURAL1 & xs: seq(T) &  i <= size(xs) THEN
	ys := (xs /|\ i-1) ^ (xs \|/ i)
END


/**
// Removes the first instance of the element y within sequence xs
ys <-- RemoveElem(y, xs) = PRE y: T & xs: seq(T) THEN
	IF xs = {} THEN
		 ys := {}
	ELSIF y = first(xs) THEN
		ys := tail(xs)
	ELSE
		ys := [first(xs)] ^ RemoveElem(y, tail(xs))
	END
END
**/

END

