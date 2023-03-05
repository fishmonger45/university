/*
    The Hybrid Merge Sort to use for Operating Systems Assignment 1 2021
    written by Robert Sheehan

    Modified by: Andreas Knapp
    UPI: akna890

    By submitting a program you are claiming that you and only you have made
    adjustments and additions to this code.
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/resource.h>
#include <stdbool.h>
#include <pthread.h>
#include <sys/time.h>
#include <sys/times.h>
#include <math.h>

#define SIZE 4
#define MAX 1000
#define SPLIT 16

struct block
{
    int size;
    int *data;
};

void print_data(struct block *block)
{
    for (int i = 0; i < block->size; ++i)
        printf("%d ", block->data[i]);
    printf("\n");
}

/* The insertion sort for smaller halves. */
void insertion_sort(struct block *block)
{
    for (int i = 1; i < block->size; ++i)
    {
        for (int j = i; j > 0; --j)
        {
            if (block->data[j - 1] > block->data[j])
            {
                int temp;
                temp = block->data[j - 1];
                block->data[j - 1] = block->data[j];
                block->data[j] = temp;
            }
        }
    }
}

/* Combine the two halves back together. */
void merge(struct block *left, struct block *right)
{
    int *combined = calloc(left->size + right->size, sizeof(int));
    if (combined == NULL)
    {
        perror("Allocating space for merge.\n");
        exit(EXIT_FAILURE);
    }
    int dest = 0, l = 0, r = 0;
    while (l < left->size && r < right->size)
    {
        if (left->data[l] < right->data[r])
            combined[dest++] = left->data[l++];
        else
            combined[dest++] = right->data[r++];
    }
    while (l < left->size)
        combined[dest++] = left->data[l++];
    while (r < right->size)
        combined[dest++] = right->data[r++];
    memmove(left->data, combined, (left->size + right->size) * sizeof(int));
    free(combined);
}

/* Merge sort the data. */
void merge_sort(struct block *block)
{
    if (block->size > SPLIT)
    {
        struct block left_block;
        struct block right_block;
        left_block.size = block->size / 2;
        left_block.data = block->data;
        right_block.size = block->size - left_block.size; // left_block.size + (block->size % 2);
        right_block.data = block->data + left_block.size;
        merge_sort(&left_block);
        merge_sort(&right_block);
        merge(&left_block, &right_block);
    }
    else
    {
        insertion_sort(block);
    }
}

/* Merge sort the data using eight threads*/
extern int current_processes_amount = 1;
int MAXIMUM_PROCESSES = 8;
void merge_sort_eight_processes_shared_memory(struct block *block)
{
    if (block->size > SPLIT)
    {
        // Do prelude work for blocks on this process
        struct block left_block;
        struct block right_block;
        left_block.size = block->size / 2;
        left_block.data = block->data;
        right_block.size = block->size - left_block.size; // left_block.size + (block->size % 2);
        right_block.data = block->data + left_block.size;

        // maximum amount of processes exceeded. Do sort and merge on current thread
        if (current_processes_amount >= MAXIMUM_PROCESSES)
        {
            printf("Reached max number of processes, mergingsorting current thread! \n");
            merge_sort(&left_block);
            merge_sort(&right_block);
            merge(&left_block, &right_block);
        }
        else
        {
            int pipe_fds[2];
            int *read_fd = &pipe_fds[0];
            int *write_fd = &pipe_fds[1];
            current_processes_amount += 1;
            pipe(pipe_fds);
            printf("Created process: %d\n", current_processes_amount);
            pid_t pid = fork();

            // Parent process
            if (pid != 0)
            {
                merge_sort(&left_block);
                // reconstruct other block
                struct block other_block;
                // read size
                read(*read_fd, &other_block.size, sizeof(int));
                // allocate heap for data
                other_block.data = (int *)calloc(other_block.size, sizeof(int));
                // read each piece of the block
                for (int i = 0; i < other_block.size; i += 16000)
                {
                    if (other_block.size - i < 16000)
                    {
                        int size1 = other_block.size - i;
                        read(*read_fd, &other_block.data[i], sizeof(int) * size1);
                    }
                    else
                    {
                        read(*read_fd, &other_block.data[i], sizeof(int) * 16000);
                    }
                }
                merge(&left_block, &other_block);
                // free after use
                free(other_block.data);
            }
            else
            {
                // child process
                merge_sort_eight_processes_shared_memory(&right_block);
                // write size
                write(*write_fd, &right_block.size, sizeof(int));
                // write data
                for (int i = 0; i < right_block.size; i += 16000)
                {
                    if (right_block.size - i < 16000)
                    {
                        int size1 = right_block.size - i;
                        write(*write_fd, &right_block.data[i], sizeof(int) * size1);
                    }
                    else
                    {
                        write(*write_fd, &right_block.data[i], sizeof(int) * 16000);
                    }
                }
                exit(0);
            }
        }
    }
    else
    {
        insertion_sort(block);
    }
}

/* Check to see if the data is sorted. */
bool is_sorted(struct block *block)
{
    bool sorted = true;
    for (int i = 0; i < block->size - 1; i++)
    {
        if (block->data[i] > block->data[i + 1])
            sorted = false;
    }
    return sorted;
}

/* Fill the array with random data. */
void produce_random_data(struct block *block)
{
    srand(1); // the same random data seed every time
    for (int i = 0; i < block->size; i++)
    {
        block->data[i] = rand() % MAX;
    }
}

int main(int argc, char *argv[])
{
    long size;

    if (argc < 2)
    {
        size = SIZE;
    }
    else
    {
        size = atol(argv[1]);
    }
    struct block block;
    block.size = (int)pow(2, size);
    block.data = (int *)calloc(block.size, sizeof(int));
    if (block.data == NULL)
    {
        perror("Unable to allocate space for data.\n");
        exit(EXIT_FAILURE);
    }

    produce_random_data(&block);

    struct timeval start_wall_time, finish_wall_time, wall_time;
    struct tms start_times, finish_times;
    gettimeofday(&start_wall_time, NULL);
    times(&start_times);

    printf("Created process: %d (main) \n", 1);
    merge_sort_eight_processes_shared_memory(&block);

    gettimeofday(&finish_wall_time, NULL);
    times(&finish_times);
    timersub(&finish_wall_time, &start_wall_time, &wall_time);
    printf("start time in clock ticks: %ld\n", start_times.tms_utime);
    printf("finish time in clock ticks: %ld\n", finish_times.tms_utime);
    printf("wall time %ld secs and %ld microseconds\n", wall_time.tv_sec, wall_time.tv_usec);

    if (block.size < 1025)
        print_data(&block);

    printf(is_sorted(&block) ? "sorted\n" : "not sorted\n");
    free(block.data);
    exit(EXIT_SUCCESS);
}
