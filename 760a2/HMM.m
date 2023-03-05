function [x] = HMM(p,pi,b,y)
%% This function implements the Viterbi algorithm, to find the most likely
% sequence of states given some set of observations.
%
%% INPUT
%  p is a matrix of transition probabilies for states x;
%  pi is a vector of prior distributions for states x;
%  b is a matrix of emission probabilities;
%  y is a vector of observations.
%
%% OUTPUT
% x is the most likely sequence of states, given the inputs.

n=length(y);
m=length(pi);

gamma=zeros(m,n);
phi=zeros(m,n);

%% You must complete the code below

for i = 1:26
    % Your code goes here (initialisation)
end

for t = 2:n
	for k = 1:26
		for j = 1:26
			% Your code goes here
		end
	end
end

best=0;
x=zeros(1,n);
% Find the final state in the most likely sequence x(n).
for k = 1:26
	if best<=gamma(k,n)
		best=gamma(k,n);
		x(n)=k;
	end
end

for i = n-1:-1:1
    % Your code goes here
end

return