function [p, prior]=constructTransitions(filename)
%% This function constructs tranisition matricies for lowercase characters.
% It is assumed that the file 'filename' only contains lowercase characters
% and whitespace.
%% INPUT
%  filename is the file containing the text from which we wish to develop a
%  Markov process.
%
%% OUTPUT
%  p is a 26 x 26 matrix containing the probabilities of transition from a
%  state to another state, based on the frequencies observed in the text.
%  prior is a vector of prior probabilities based on how often each character
%  appears in the text

%% Read the file into a sting called text
fileID = fopen(filename,'r');
text = fscanf(fileID,'%c');
fclose(fileID);

%% Your code goes here.

return