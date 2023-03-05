import copy

def constructEmissions(pr_correct, adj):
    # This function takes in a matrix detailing the adjacent letters on a keyboard, and the
    #  probability of hitting the correct key and outputs a matrix of emission probabilities
    #
    # INPUT
    # pr_correct - the probability of correctly hitting the intended key;
    # adj - a 26 x 26 matrix with adj[i][j] = 1 if the i'th letter in the alphabet is adjacent
    # to the j'th letter.
    #
    # OUTPUT
    # b - a 26 x 26 matrix with b[i][j] being the probability of hitting key j if you intended
    # to hit key i (the probabilities of hitting all adjacent keys are identical).
    
	# Your code goes here.
    b = copy.deepcopy(adj)
    for r in range(0, len(b)):
        prob = (1-pr_correct)/len([1 for i in b[r] if i == 1])
        for c in range(0, len(b[r])):
            if r == c:
                b[r][c] = 0.5
            elif adj[r][c] == 1:
                b[r][c] = prob
            else:
                pass
    return b

def constructTransitions(filename):
    # This function constructs tranisition matricies for lowercase characters.
    # It is assumed that the file 'filename' only contains lowercase characters
    # and whitespace.
    # INPUT
    #  filename is the file containing the text from which we wish to develop a
    #  Markov process.
    #
    # OUTPUT
    #  p is a 26 x 26 matrix containing the probabilities of transition from a
    #  state to another state, based on the frequencies observed in the text.
    #  prior is a vector of prior probabilities based on how often each character
    #  appears in the text

    # Read the file into a sting called text
    text = None
    with open('bible.txt', 'r') as myfile:
        text = myfile.read()

	# Your code goes here.
    #p = [[0]*26]*26 # Transition probabilities from i to j
    p = [[0 for x in range(26)] for y in range(26)]
    #prior = [0]*26 # Probabilities of a certain letter appearing
    prior = [0 for x in range(26)]

    # Remove non alpha charactes
    text = text.replace(" ", "")
    text = text.replace("\n", "")
    text = text.replace(".", "")
    text = text.replace("(", "")
    text = text.replace(")", "")
    text = text.replace(":", "")
    text = text.replace(";", "")
    text = text.replace("!", "")
    # Create transition matrix
    for i in range(0, len(text)-1):
        n = ord(text[i]) - 97
        m = ord(text[i+1]) - 97
        p[n][m] += 1
        prior[n] += 1

    for r in range(0,len(p)):
        # Find the number of transitions for the current letter
        no_trans = text.count(chr(r + 97)) - 1
        for c in range(0, len(p[r])):
            p[r][c] = p[r][c]/no_trans
        

    # How often each character appears in the text
    prior = list(map(lambda x: x/len(text), prior))
    return (p, prior)


def HMM(p, pi, b, y):
    # This function implements the Viterbi algorithm, to find the most likely
    # sequence of states given some set of observations.
    #
    # INPUT
    #  p is a matrix of transition probabilies for states x;
    #  pi is a vector of prior distributions for states x;
    #  b is a matrix of emission probabilities;
    #  y is a vector of observations.
    #
    # OUTPUT
    # x is the most likely sequence of states, given the inputs.

    n = len(y)
    m = len(pi)

    gamma = {}
    phi = {}

    # You must complete the code below
    for i in range(26):
        # Your code goes here (initialisation)
        # b_u(y1)phi
        print("yoyo")

    for t in range(1, n):
        for k in range(26):
			#gamma[k, t] = 0
            for j in range(26):
                # Your code goes here
                print("yoyo")
                    
    best=0
    x=[]
    for t in range(n):
        x.append(0)

    # Find the final state in the most likely sequence x(n).
    for k in range(26):
        if best<=gamma[k,n-1]:
            best=gamma[k,n-1]
            x[n-1]=k

    for i in range(n-2,-1,-1):
        # Your code goes here
        print("yoyo")

    return x

def main():
    # The text messages you have received.
    msgs=[]
    msgs.append('cljlx ypi ktxwf a pwfi psti vgicien aabdwucg vpd me and vtiex voe zoicw')
    msgs.append('qe qzby yii tl gp tp yhr cpozwdt fwstqurzby')
    msgs.append('qee ypi xfjvkjv ygetw ib ulur vae')
    msgs.append('wgrrr zrw uiu')
    msgs.append('hpq fzr qee ypi vrpm grfw')
    msgs.append('qe zfr xtztvkmh')
    msgs.append('wgzf tjmr will uiu xjoq jp ywfw')

    # The probability of hitting the intended key.
    pr_correct= 0.5# Complete this line

    # An adjacency matrix, adj(i,j) set to 1 if the i'th letter in the alphabet is next
    # to the j'th letter in the alphabet on the keyboard.
    adj=[[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,1],[0,0,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0],[0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0],[0,0,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,0,0],[0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0],[0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,0],[0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0],[0,1,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,1,0],[0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0],[0,0,0,0,0,0,0,1,1,0,1,0,1,1,0,0,0,0,0,0,1,0,0,0,0,0],[0,0,0,0,0,0,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0],[0,1,0,0,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,1,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0],[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0],[0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0],[1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1],[0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0],[0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],[0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0],[0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1],[0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0],[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0]]

    # Call a function to construct the emission probabilities of hitting a key
    # given you tried to hit a (potentially) different key.
    b=constructEmissions(pr_correct,adj)

    # Call a function to construct transmission probabilities and a prior distribution
    # from the King James Bible.
    [p, prior]=constructTransitions('bible.txt')
        
    # Run the Viterbi algorithm on each word of the messages to determine the
    # most likely sequence of characters.
    for msg in msgs:
        s_in = msg.split(' ') #divide each message into a list of words

        output=''
        
        for i in range(len(s_in)):
            y=[]
            
            for j in range(len(s_in[i])):
                y.append(ord(s_in[i][j])-97) #convert the letters to numbers 0-25
            end

            x=HMM(p,prior,b,y); #perform the Viterbi algorithm

            output=''
            for j in range(len(x)):
                output=output+chr(x[j]+97) #convert the states x back to letters
                    
            if i!=len(s_in)-1:
                output=output+' ' #recreate the message
                
        print(msg) #display received message
        print(output) #display decoded message
        print('')

if __name__ == "__main__":
    main()
