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
#include <sys/time.h>
#include <sys/times.h>
#include <math.h>
#include <pthread.h>

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

// Example work function used for testing
void work_func()
{
    printf("====== WORK DONE ======\n");
    sleep(2);
}

// A linked list of functions to execute
typedef struct work
{
    void *fn;
    void *arg;
    struct work *next;
} work;

typedef struct threadpool
{
    work *work_list;               // A linked list of work
    int *num_work;                 // The length of the linked list
    pthread_mutex_t *mutex;        // locks the entire threadpool!
    pthread_cond_t *work_cond;     // signal that there is work to be done!
    pthread_cond_t *no_work_cond;  // signal that all threads are inactive!
    int working_threads;           // Amount of threads that are currently active!
    pthread_t thread_references[]; // thread references

} threadpool;

// a wrapper for pthread_create that includes blocking until signal to do work
void create_thread(threadpool *tp)
{
    printf("creating thread (create thread)\n");
    // We want the thread to go through this process forever (doing work)
    while (true)
    {
        pthread_mutex_lock(tp->mutex);
        printf("got mutex lock (create thread)\n");

        // if there is no work wait for there to be work and then execute that work
        while (tp->num_work == 0)
        {
            printf("waiting for work (create thread)\n");
            pthread_cond_wait(tp->work_cond, tp->mutex);
        }
        printf("found work (create thread)\n");
        // there should now be work on the threadpool. Lets start to execute!
        printf("popping work (pop work)\n");
        // get the head of the linked list
        work *popped_work = tp->work_list;
        // remove the pointer to the head of the linked list in the thread pool
        tp->work_list = tp->work_list->next;
        tp->num_work -= 1;
        printf("popped work (pop work)\n");

        // work *popped_work = pop_work(tp);
        // pthread_mutex_lock(tp->mutex);

        // we are now doing work from this point onwards!
        tp->working_threads += 1;
        // unlock mutex
        pthread_mutex_unlock(tp->mutex);
        printf("starting work (create thread)\n");
        // call the work function
        ((void (*)())popped_work->fn)();
        printf("did work (create thread)\n");
        // ((void (*)())popped_work->fn)(popped_work->arg);
        // now we are finished with our work!
        pthread_mutex_lock(tp->mutex);
        tp->working_threads -= 1;
        if(tp->working_threads == 0) {
            pthread_cond_signal(tp->no_work_cond);
        }
        pthread_mutex_unlock(tp->mutex);
    }
}

// GLOBALS
pthread_mutex_t LOCK = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t WORKING_COND = PTHREAD_COND_INITIALIZER;
pthread_cond_t NO_WORKING_COND = PTHREAD_COND_INITIALIZER;

threadpool *create_threadpool(int num_threads)
{
    printf("creating threadpool\n");
    // create the threadpool
    threadpool *tp = malloc(sizeof(threadpool));
    tp->work_list = NULL;
    tp->num_work = 0;
    tp->mutex = &LOCK;
    tp->work_cond = &WORKING_COND;
    tp->no_work_cond = &NO_WORKING_COND;
    tp->working_threads = 0;
    tp->thread_references[num_threads];

    // create the threads and sleep them until work is submitted
    for (int i = 0; i < num_threads; i++)
    {
        pthread_t t1;
        pthread_create(&t1, NULL, (void *)&create_thread, tp);
        tp->thread_references[i] = t1;
    }

    return tp;
}

// mutate the threadpool to push work to the linked list
// also signal the work_cond to indicate that there is work to be done
void push_work(threadpool *tp, void *fn, void *arg)
{
    // create work
    work *new_work = malloc(sizeof(work));
    new_work->fn = fn;
    new_work->arg = arg;
    new_work->next = NULL;

    pthread_mutex_lock(tp->mutex);
    work *cur_work = tp->work_list;
    if (cur_work == NULL)
    {
        tp->work_list = new_work;
    }
    else
    {
        while (cur_work->next != NULL)
        {
            cur_work = cur_work->next;
        }
        cur_work->next = new_work;
    }

    tp->num_work += 1;
    printf("updated working threads number(push work)\n");
    // signal that there is work to do and release thread pool lock

    pthread_mutex_unlock(tp->mutex);
    printf("unlocked mutex (push work)\n");

    pthread_cond_signal(tp->work_cond);
    printf("cond signaled (push work)\n");
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
    printf("starting (main)\n");
    // number of threads that I want working
    int num_threads = 5;
    // create threadpool
    threadpool *tp = create_threadpool(num_threads);
    printf("created threadpool (main)\n");

    // push a lot of work to the work list
    for (int i = 0; i < 25; ++i)
    {
        // work an example function (work_func)
        push_work(tp, (void *)&work_func, NULL);
    }

    pthread_cond_wait(tp->no_work_cond, tp->mutex);
    printf("EVERYTHING DONE~\n");

    return 0;
}

// int main(int argc, char *argv[])
// {
//     long size;

//     if (argc < 2)
//     {
//         size = SIZE;
//     }
//     else
//     {
//         size = atol(argv[1]);
//     }
//     struct block block;
//     block.size = (int)pow(2, size);
//     block.data = (int *)calloc(block.size, sizeof(int));
//     if (block.data == NULL)
//     {
//         perror("Unable to allocate space for data.\n");
//         exit(EXIT_FAILURE);
//     }

//     produce_random_data(&block);

//     struct timeval start_wall_time, finish_wall_time, wall_time;
//     struct tms start_times, finish_times;
//     gettimeofday(&start_wall_time, NULL);
//     times(&start_times);

//     merge_sort(&block);

//     gettimeofday(&finish_wall_time, NULL);
//     times(&finish_times);
//     timersub(&finish_wall_time, &start_wall_time, &wall_time);
//     printf("start time in clock ticks: %ld\n", start_times.tms_utime);
//     printf("finish time in clock ticks: %ld\n", finish_times.tms_utime);
//     printf("wall time %ld secs and %ld microseconds\n", wall_time.tv_sec, wall_time.tv_usec);

//     if (block.size < 1025)
//         print_data(&block);

//     printf(is_sorted(&block) ? "sorted\n" : "not sorted\n");
//     free(block.data);
//     exit(EXIT_SUCCESS);
// }
