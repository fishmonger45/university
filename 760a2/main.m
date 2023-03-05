clc
% The text messages you have received.
input={};
input{1}='cljlx ypi ktxwf a pwfi psti vgicien aabdwucg vpd me and vtiex voe zoicw';
input{2}='qe qzby yii tl gp tp yhr cpozwdt fwstqurzby';
input{3}='qee ypi xfjvkjv ygetw ib ulur vae';
input{4}='wgrrr zrw uiu';
input{5}='hpq fzr qee ypi vrpm grfw';
input{6}='qe zfr xtztvkmh';
input{7}='wgzf tjmr will uiu xjoq jp ywfw';

%The probability of hitting the intended key.
pr_correct= %Complete this line;

% An adjacency matrix set to 1 if the ith letter in the alphabet is next to
% the jth letter in the alphabet on the keyboard.
adj=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,1;0,0,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0;0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0;0,0,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,0,0;0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0;0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,0;0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0;0,1,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,1,0;0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0;0,0,0,0,0,0,0,1,1,0,1,0,1,1,0,0,0,0,0,0,1,0,0,0,0,0;0,0,0,0,0,0,0,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0;0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0;0,0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0;0,1,0,0,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0;0,0,0,0,0,0,0,0,1,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0;0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0;1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0;0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0;1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1;0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0;0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0;0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0;1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0;0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1;0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0;1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0];

% Call a function to construct the emission probabilities of hitting a key
% given you tried to hit a (potentially) different key.
b=constructEmissions(pr_correct,adj);

% Call a function to construct transmission probabilities and a prior distribution
% from the King James Bible.
if ~exist('p','var') || ~exist('prior','var')
    [p, prior]=constructTransitions('bible.txt');
end
    
% Run the Viterbi algorithm on each word of the messages to determine the
% most likely sequence of characters.
for t=1:length(input)
    s_in = strsplit(input{t},' ') ; %divide each message into a list of words

    output='';
    
    for i=1:length(s_in)
		y=zeros(1,length(s_in{i}));
		
		for j=1:length(y)
			y(j)=double(s_in{i}(j))-96; %convert the letters to numbers 1-26
		end
        
		x=HMM(p,prior,b,y); %perform the Viterbi algorithm
		
		for j=1:length(x)
			output=[output char(x(j)+96)]; %convert the states x back to letters
		end
		
        if i~=length(s_in)
            output=[output ' ']; %recreate the message
        end
    end
    disp(input{t}); %display received message
    disp(output); %display decoded message
    disp(' ');
end
