import constants
import statistics as stats
import random
from copy import deepcopy
import matplotlib.pyplot as plt

# ENGSCI 760: Assignment 1
# Completed by: Andreas Knapp

# global intermediate values cache
# tracking worth of each crucible in the best solution
intermediates = [0.0] * int(constants.NO_POTS / constants.POTS_PER_CRUCIBLE)


def gen_start():
    # Generate a boring starting solution
    pot = 0
    solution = []
    for c in range(0, constants.NO_CRUCIBLES):
        crucible = []
        for r in range(0, constants.POTS_PER_CRUCIBLE):
            crucible.append(pot)
            pot += 1
        solution.append(crucible)
    return solution


def gen_random():
    # Generate a random solution
    pots = list(range(0, constants.NO_POTS))
    random.shuffle(pots)
    solution = []
    for i in range(0, constants.NO_POTS, 3):
        solution.append(pots[i:i+3])
    return solution


def calc_cruicible_value(crucible_al, crucible_fe):
    # Calculate the value of a crucible, known as g(al,fe) in assignment
    value = 0.0
    for i in range(constants.NO_QUALITIES-1, -1, -1):
        if crucible_al >= constants.QUALITY_MIN_AL[i] - 0.00001:
            if crucible_fe <= constants.QUALITY_MAX_FE[i] + 0.00001:
                value = constants.QUALITY_VALUE[i]
                break
    return value


def view_solution(x):
    # View a pretty printed solution with objective function (price) summary
    cruicible_values = []
    max_spread = 0
    for c in range(0, len(x)):
        spread = max(x[c]) - min(x[c])
        max_spread = max(spread, max_spread)
        crucible_al = stats.mean([constants.POT_AL[i] for i in x[c]])
        crucible_fe = stats.mean([constants.POT_FE[i] for i in x[c]])
        cruicible_values.append(
            calc_cruicible_value(crucible_al, crucible_fe))
        # Pretty print solution with index offset 1
        print(
            f'{c+1} {[a+1 for a in x[c]]} {crucible_al:.2f}Al {crucible_fe:.2f}Fe {cruicible_values[c]:.2f} {spread}')
    print(f'Sum,Max= {sum(cruicible_values):.2f}, {max_spread}')


def calc_solution_value(x):
    # Calculate the value of a solution, known as f(x) in assignment
    # Note: The re-calculation of intermediate
    # values is not done here but rather in the ascend_to_local_max function. I
    # am treating this function as a pure function (no modification of external
    # variables)
    value = 0.0
    for c in range(0, len(x)):
        crucible_al = stats.mean([constants.POT_AL[i] for i in x[c]])
        crucible_fe = stats.mean([constants.POT_FE[i] for i in x[c]])
        crucible_value = calc_cruicible_value(crucible_al, crucible_fe)
        value += crucible_value
    return value


def w(q, w, e):
    # Wrapper to the objective function which allows input of three pots indexes
    c = [q, w, e]
    crucible_al = stats.mean([constants.POT_AL[i] for i in c])
    crucible_fe = stats.mean([constants.POT_FE[i] for i in c])
    return calc_cruicible_value(crucible_al, crucible_fe)


def delta(x, c, p, a, b):
    # The optimised difference (f(y(x,p,a,b)-f(x))) in value between a neighbour and the original solution
    diff = 0.0
    offset = intermediates[c] + intermediates[p]
    if a == 0 and b == 0:
        diff = w(x[p][0], x[c][1], x[c][2]) + \
            w(x[c][0], x[p][1], x[p][2])
    elif a == 0 and b == 1:
        diff = w(x[p][1], x[c][1], x[c][2]) + \
            w(x[p][0], x[c][0], x[p][2])
    elif a == 0 and b == 2:
        diff = w(x[p][2], x[c][1], x[c][2]) + \
            w(x[p][0], x[p][1], x[c][0])
    elif a == 1 and b == 0:
        diff = w(x[c][0], x[p][0], x[c][2]) + \
            w(x[c][1], x[p][1], x[p][2])
    elif a == 1 and b == 1:
        diff = w(x[c][0], x[p][1], x[c][2]) + \
            w(x[p][0], x[c][1], x[p][2])
    elif a == 1 and b == 2:
        diff = w(x[c][0], x[p][2], x[c][2]) + \
            w(x[p][0], x[p][1], x[c][1])
    elif a == 2 and b == 0:
        diff = w(x[c][0], x[c][1], x[p][0]) + \
            w(x[c][2], x[p][1], x[p][2])
    elif a == 2 and b == 1:
        diff = w(x[c][0], x[c][1], x[p][1]) + \
            w(x[p][0], x[c][2], x[p][2])
    elif a == 2 and b == 2:
        diff = w(x[c][0], x[c][1], x[p][2]) + \
            w(x[p][0], x[p][1], x[c][2])
    else:
        print("difference: invalid values of a and b")
        print(f'({a},{b})')
        exit(1)
    return round(diff - offset, 2)


def y(x, c, p, a, b):
    # Neighbourhood rule swapping two pots between two crucibles
    data = deepcopy(x)
    temp = data[c][a]
    data[c][a] = data[p][b]
    data[p][b] = temp
    return data


def ascend_to_local_max(x):
    # Performs a next ascent local search
    # Start intermediate cache of crucible objective values
    for c in range(0, len(x)):
        crucible_al = stats.mean([constants.POT_AL[i] for i in x[c]])
        crucible_fe = stats.mean([constants.POT_FE[i] for i in x[c]])
        intermediates[c] = calc_cruicible_value(crucible_al, crucible_fe)

    best_solution = x
    current_solution = x
    neighbours_explored = 0
    to_explore = constants.H
    while True:
        for (c, p, a, b) in to_explore:
            neighbours_explored += 1
            if delta(best_solution, c, p, a, b) > 0:
                # Only compute the current solution if the is neighbour is
                # better than the best solution, then set the best solution
                best_solution = y(best_solution, c, p, a, b)

                # Update intermediates
                # Intermediate c
                crucible_al = stats.mean(
                    [constants.POT_AL[i] for i in best_solution[c]])
                crucible_fe = stats.mean(
                    [constants.POT_FE[i] for i in best_solution[c]])
                intermediates[c] = calc_cruicible_value(
                    crucible_al, crucible_fe)

                # Intermediate p
                crucible_al = stats.mean(
                    [constants.POT_AL[i] for i in best_solution[p]])
                crucible_fe = stats.mean(
                    [constants.POT_FE[i] for i in best_solution[p]])
                intermediates[p] = calc_cruicible_value(
                    crucible_al, crucible_fe)

                # Reset number of neighbours explored
                neighbours_explored = 0

                # Update neighbours to explore to prove that the current
                # solution is a local optima. This change is done such that the
                # algorithm does not explore neighbours that it has already
                # explored already and reduces the amount of redundant computation
                flag = False
                sub1 = []
                sub2 = []
                for cpab in constants.H:
                    if cpab[0] == c and cpab[1] == p and cpab[2] == a and cpab[3] == b:
                        flag = True
                    elif flag == False:
                        sub1.append(cpab)
                    elif flag == True:
                        sub2.append(cpab)
                    else:
                        print(
                            "ascend_to_local_max: unexpected flag value")
                        exit(1)
                # Update the neighbours to explore
                to_explore = sub2 + sub1
                # Note: we are not exploring extra neighbours by breaking here.
                # to_explore has been updated to reflect the changed state but
                # we must update the for loop variable state
                break

            if neighbours_explored == len(to_explore):
                # We have explored all neighbours and have come to a local optimum
                return best_solution
    return best_solution


def test_ascend_to_local_max(x):
    # Tests next ascent local search
    # For sake of simplicity this is a modified, but seperate version of ascend_to_local_max
    # Start intermediate cache of crucible objective values
    for c in range(0, len(x)):
        crucible_al = stats.mean([constants.POT_AL[i] for i in x[c]])
        crucible_fe = stats.mean([constants.POT_FE[i] for i in x[c]])
        intermediates[c] = calc_cruicible_value(crucible_al, crucible_fe)

    iteration = 0
    iterations = [iteration]
    best_solution = x
    solution_values = [calc_solution_value(best_solution)]
    current_solution = x
    neighbours_explored = 0
    to_explore = constants.H

    while True:
        for (c, p, a, b) in to_explore:
            neighbours_explored += 1

            # Log iteration
            iteration += 1
            iterations.append(iteration)
            current_solution = y(best_solution, c, p, a, b)
            solution_values.append(calc_solution_value(current_solution))

            # Check if the solution is better than the best solution
            if delta(best_solution, c, p, a, b) > 0:
                # Set the current solution as best solution
                best_solution = current_solution

                # Update intermediates
                # Intermediate c
                crucible_al = stats.mean(
                    [constants.POT_AL[i] for i in best_solution[c]])
                crucible_fe = stats.mean(
                    [constants.POT_FE[i] for i in best_solution[c]])
                intermediates[c] = calc_cruicible_value(
                    crucible_al, crucible_fe)

                # Intermediate p
                crucible_al = stats.mean(
                    [constants.POT_AL[i] for i in best_solution[p]])
                crucible_fe = stats.mean(
                    [constants.POT_FE[i] for i in best_solution[p]])
                intermediates[p] = calc_cruicible_value(
                    crucible_al, crucible_fe)

                # Reset number of neighbours explored
                neighbours_explored = 0

                # Update neighbours to explore to prove that the current
                # solution is a local optima. This change is done such that the
                # algorithm does not explore neighbours that it has already
                # explored already and reduces the amount of redundant computation
                flag = False
                sub1 = []
                sub2 = []
                for cpab in constants.H:
                    if cpab[0] == c and cpab[1] == p and cpab[2] == a and cpab[3] == b:
                        flag = True
                    elif flag == False:
                        sub1.append(cpab)
                    elif flag == True:
                        sub2.append(cpab)
                    else:
                        print(
                            "ascend_to_local_max: unexpected flag value")
                        exit(1)
                # Update the neighbours to explore
                to_explore = sub2 + sub1
                # Note: we are not exploring extra neighbours by breaking here.
                # to_explore has been updated to reflect the changed state but
                # we must update the for loop variable state
                break
            if neighbours_explored == len(to_explore):
                # We have explored all neighbours and have come to a local optimum
                # Graph the solution
                plt.plot(iterations, solution_values)
                plt.xlabel('Iterations')
                plt.ylabel('Objective function value (value of solution)')
                plt.title('Iterations vs objective values')
                # Print the best solution before showing graph as show is blocking
                print("Solution original")
                view_solution(x)
                print("\n")
                print("Solution after AscendToLocalMax")
                view_solution(best_solution)
                print("Please close the graph window to continue...")
                plt.show()
                return best_solution
    return best_solution


def do_repeated_ascents(n):
    # Repeat n ascents and plot the runtime of ascend to local max
    iteration = 0
    iterations = []
    current_solution = gen_random()
    best_solution = current_solution
    local_optima_values = []
    best_solutions_values = []
    while iteration < n:
        current_solution = gen_random()
        local_optima = ascend_to_local_max(current_solution)
        local_optima_values.append(calc_solution_value(local_optima))
        if calc_solution_value(local_optima) > calc_solution_value(best_solution):
            best_solution = local_optima
        best_solutions_values.append(calc_solution_value(best_solution))
        iterations.append(iteration)
        iteration += 1

    # Create plot
    plt.plot(iterations, local_optima_values, label="local optima values")
    plt.plot(iterations, best_solutions_values, label="best solution values")
    plt.xlabel('Iterations')
    plt.ylabel('Objective function value (value of solution)')
    plt.title('Iterations vs objective values')
    plt.legend(loc="upper left")
    # Print the best solution before showing graph as show is blocking
    print("Best solution")
    view_solution(best_solution)
    print("Please close the graph window to continue...")
    plt.show()
    return best_solution

# ==================================== Question 5 code ======================================
# ==================== Please don't mark as other questions implementation ==================


def g_double_dash(crucible_al, crucible_fe, a, b, c, s):
    # Objectve function for question 5. that takes the spread between crucibles
    # into account
    # Note: pots x_c,1, x_c,2, x_c,3 are renamed a, b, c
    # repectively for clarify
    sc = max(a, b, c) - min(a, b, c)
    k = max(s, sc)
    if k == s:
        return calc_cruicible_value(crucible_al, crucible_fe)
    else:
        val = calc_cruicible_value(crucible_al, crucible_fe)
        return (val - ((k/50)*0.50*val))


def calc_solution_value_with_spread(x, s):
    # Calculate the value of a solution considering spread for question 5.
    # Note: This function is only used in question 5 and should therefore only be
    # marked for question 5
    value = 0.0
    for c in range(0, len(x)):
        crucible_al = stats.mean([constants.POT_AL[i] for i in x[c]])
        crucible_fe = stats.mean([constants.POT_FE[i] for i in x[c]])
        crucible_value = g_double_dash(
            crucible_al, crucible_fe, x[c][0], x[c][1], x[c][2], s)
        value += crucible_value
    return value


def ascend_to_local_max_with_spread(x, s):
    # Performs a next ascent local search with spread for question 5, uses the
    # g_double_dash as an objective functionn.
    # Note: This function is only used in question 5 and should therefore only be
    # marked for question 5

    best_solution = x
    current_solution = x
    neighbours_explored = 0
    to_explore = constants.H
    while True:
        for (c, p, a, b) in to_explore:
            neighbours_explored += 1
            current_solution = y(best_solution, c, p, a, b)
            if calc_solution_value_with_spread(current_solution, s) - calc_solution_value_with_spread(best_solution, s) > 0:

                # better than the best solution, then set the best solution
                best_solution = current_solution
                # Reset number of neighbours explored
                neighbours_explored = 0

                # Update neighbours to explore to prove that the current
                # solution is a local optima. This change is done such that the
                # algorithm does not explore neighbours that it has already
                # explored already and reduces the amount of redundant computation
                flag = False
                sub1 = []
                sub2 = []
                for cpab in constants.H:
                    if cpab[0] == c and cpab[1] == p and cpab[2] == a and cpab[3] == b:
                        flag = True
                    elif flag == False:
                        sub1.append(cpab)
                    elif flag == True:
                        sub2.append(cpab)
                    else:
                        print(
                            "ascend_to_local_max_with_spread: unexpected flag value")
                        exit(1)
                # Update the neighbours to explore
                to_explore = sub2 + sub1
                # Note: we are not exploring extra neighbours by breaking here.
                # to_explore has been updated to reflect the changed state but
                # we must update the for loop variable state
                break

            if neighbours_explored == len(to_explore):
                # We have explored all neighbours and have come to a local optimum
                return best_solution
    return best_solution


def find_best_solution(s):
    # Find the best solution for question 5 using the spread objective function
    # This is a local search with random restarts
    # Note: This function is only used in question 5 and should therefore only be
    # marked for question 5
    current_solution = gen_random()
    best_solution = current_solution
    solution_number = 1
    while True:
        local_optima = ascend_to_local_max_with_spread(current_solution, s)
        # New best solution found
        if calc_solution_value_with_spread(local_optima, s) > calc_solution_value_with_spread(best_solution, s):
            best_solution = local_optima
            # print the best solution so far
            print(f'Solution number: {solution_number}')
            view_solution(best_solution)
            solution_number += 1
            print("Continuing search...")
        current_solution = gen_random()

# ============================= End of Question 5 code ======================================


def main():
    # Entry point to application. Markers should comment and uncomment tasks
    # here to run the code

    # Uncomment below for task 3A
    print("Calculating Task 3A...")
    x = gen_start()
    y = ascend_to_local_max(x)
    print("Solution original")
    view_solution(x)
    print("")
    print("Solution after AscendToLocalMax")
    view_solution(y)

    # Uncomment below for task 3B
    print("Calculating Task 3B...")
    x = gen_start()
    test_ascend_to_local_max(x)

    # Uncomment below for task 3C
    print("Calculating Task 3C (expect this to take up to 2 minutes)...")
    do_repeated_ascents(200)
    print("Finished!")

    # Uncomment below for question 5
    print("Calculating Question 5 with spread 6...")
    find_best_solution(6)
    print("Calculating Question 5 with spread 8...")
    #find_best_solution(8)
    print("Calculating Question 5 with spread 11...")
    #find_best_solution(11)


if __name__ == "__main__":
    main()
