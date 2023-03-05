function [b] = constructEmissions(pr_correct,adj)
%% This function takes in a matrix detailing the adjacent letters on a keyboard, and the
%  probability of hitting the correct key and outputs a matrix of emission probabilities
%
%% INPUT
% pr_correct - the probability of correctly hitting the intended key;
% adj - a 26 x 26 matrix with adj(i,j) = 1 if the ith letter in the alphabet is adjacent
% to the jth letter.
%
%% OUTPUT
% b - a 26 x 26 matrix with b(i,j) being the probability of hitting key j if you intended
% to hit key i (the probabilities of hitting all adjacent keys are identical).

%% Your code goes here.