/*
This calculates the summation of an integer value passed to it.

Example:
	$gcc ThreadedSummation.c -lpthread
	$./a.out 5
	Sum = 15
*/

#include <pthread.h> 
#include <stdio.h> 

int sum = 0; 

void *summation(void *param) {
	/*The thread will begin control in this function*/	
	printf("Hello from sum thread %u.\n", pthread_self());

	int i;
	int upper = atoi(param);
	
	for (i = 1; i <= upper; i++)
		sum += i;
		
	/*Exit and free thread resources*/
	pthread_exit(0);	
}

int main(int argc, char *argv[]) {
	/*Begin thread for main()*/
	printf("Hello from main thread %u.\n", pthread_self());	

	/* The thread ID */
	pthread_t tid; 	
	
	if (argc != 2) {
		printf("Usage: a.out <integer value>\n");	
		return -1;
	}	
	
	/*Thread Creation*/
	pthread_create(&tid, NULL, summation, argv[1]);
	
	/*Wait for a thread to exit*/
	pthread_join(tid, NULL);
	
	printf("Sum = %d\n", sum);	
}