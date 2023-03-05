MACHINE OnlineShop
INCLUDES LibrarySequences(ITEMS), User, Clock
PROMOTES Work 

SETS 
	ITEMS

VARIABLES 
	user,
	float,
	cost,
	cart,
	item,
	stock,
	shippingToUser,
	shippingToStore

INVARIANT 
// Types
user: seq(USER) &
float: NATURAL &
cost: ITEMS +-> NATURAL1 &
item: USER +-> seq(ITEMS) &
stock: ITEMS +-> NATURAL &
cart: USER +-> seq(ITEMS) &
shippingToUser: USER +-> seq(ITEMS) &
shippingToStore: seq(ITEMS) &

// Invariant conditions
ran(user) <: USER &
dom(cart) = ran(user) &
dom(item) = ran(user) &
dom(shippingToUser) = ran(user) &
ran(shippingToStore) <: dom(cost) &
dom(cost) <: ITEMS &
dom(stock) = dom(cost)

INITIALISATION 
// Variables
user := {} ||
float := 10 ||
cost :={} ||
item := {} ||
stock := {} ||
cart := {} ||
shippingToUser := {} ||
shippingToStore := {}

OPERATIONS

// Create an account for user u
CreateAccount(u) = PRE u: USER & u /: ran(user) THEN
	BEGIN
		user := user <- u;
		cart(u) := {};
		item(u) := {};
		shippingToUser(u) := {};
		shippingToStore := {}
	END
END;

// Add an item i for sale at the shop at cost c
AddItemForSale(i, c) = PRE i: ITEMS & c: NATURAL1 & i /: dom(cost) THEN
	BEGIN
		cost(i) := c;
		stock(i) := 0
	END

END;

// Add an item i to user u's cart 
AddtoItemCart(u, i) = PRE u: ran(user) & i: dom(cost)THEN
	IF u /: dom(cart) THEN
		 cart(u):= [i] 
	ELSE 
		cart(u) := cart(u) <- i 
	END
END;

// Removes the first (scanned from front to last) occurance of an item in the users cart
// Requires that the item is part of the users cart
RemoveItemFromCart(u, i) = PRE u: ran(user) & i: ITEMS & #e. (cart(u)(e) = i) THEN
	LET q
	BE q = IndexOf(i, cart(u))
	IN cart(u) := RemoveIndex(q, cart(u))
	END
END;

// Empty a users cart
EmptyCart(u) = PRE u: ran(user) & cart(u) /= {} THEN
	cart(u) := {}
END;

// Checkout a users cart. The user pays for the items and they are shipped to the user
CheckoutCart(u) = PRE u: ran(user) & cart(u) /= {} & balance(u) >= CartValue(u) & CanFufilCart(u) = TRUE THEN 
	VAR i
	IN i := 1;
		BEGIN
			// Remove cost of items and shipping from user
			SetBalance(u, balance(u) - CartValue(u));
			// And add it to the shops float
			float := float + CartValue(u);
	
			// Remove items from shop stock 
			WHILE i <=  size(cart(u)) DO
					stock(cart(u)(i)) := stock(cart(u)(i))  - 1;
					i := i + 1
			INVARIANT size(cart(u)) = size(cart(u))
			VARIANT size(cart(u)) - i			
			END;

			// Ship the items for the user	
			IF shippingToUser(u) = {} 
			THEN
				shippingToUser(u) := cart(u)
			ELSE
				shippingToUser(u) := shippingToUser(u) ^ cart(u)
			END;
		
			// Empty the users cart
			cart(u) := {}
		END
	END
END;

// The total value of the users cart
value <-- CartValue(u) = PRE u: dom(cart) THEN 
	IF cart(u) = {}
	THEN 
		value := 0
	ELSE
		value := SIGMA(i).(i : 1..size(cart(u)) | cost(cart(u)(i)) )
	END
END;



// Predicate for If the shop can fufil the order using its stock levels
value <-- CanFufilCart(u) = PRE u: ran(user) THEN
BEGIN
	VAR i, canFufil
	IN i := 1; canFufil := TRUE;
		WHILE i <=  size(cart(u)) DO
			IF canFufil = TRUE & stock(cart(u)(i)) < card(cart(u) |> {cart(u)(i)})
			THEN
				canFufil := FALSE
			END;
			i := i + 1
		INVARIANT size(cart(u)) = size(cart(u))
		VARIANT size(cart(u)) - i
		END;
	value := canFufil
	END
END

END;

// Restock an amount of an item that the shop sells
RestockItem(i, n) = PRE i: dom(cost) & n : 1..3 & n*cost(i) <= float THEN
	BEGIN
		// Pay for the items
		float := float - n*cost(i);
		// Add items to the stock
		stock(i) := stock(i) + n
	END
END;

// Return an item to the shop by shipping it back
ReturnItem(u, i) = PRE u: ran(user) & i: ran(item(u)) & i: dom(cost) THEN
	BEGIN
	// Remove the item from the users items
	item(u) := RemoveIndex(IndexOf(i, item(u)), item(u));
	// And ship it back to the store
	shippingToStore := shippingToStore <- i;
	// Refund the user
	SetBalance(u, balance(u) + cost(i))
	END
END;

// Begin a new day by incrementing the time and shipping all items to the store and delivering bought items to user
NextDay = PRE time < maxTime THEN
BEGIN
	// Start new day
	Tick;

	// Deliver items to users
	VAR i
	IN i := 1;
		WHILE i <=  size(user) DO
			item(user(i)) := item(user(i))  ^ shippingToUser(user(i));
			shippingToUser(user(i)) := {};
			i := i + 1
		INVARIANT card(user) = card(user)
		VARIANT card(user) - i
		END
	END;

	// Return items to store
	VAR i
	IN i := 1;
		WHILE i <=  size(shippingToStore) DO
			stock(shippingToStore(i)) := stock(shippingToStore(i)) + 1;
			i := i + 1
		INVARIANT size(shippingToStore) = size(shippingToStore) 
		VARIANT size(shippingToStore)  - i
		END
	END;
	// Clear shipping items to store
	shippingToStore := {}
END
END

END
