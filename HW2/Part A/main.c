#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdint.h>

int main() {
    uint8_t i, n;
    
    // Set up file pointer to write to a file
    FILE *file = fopen("Fork_Output.csv", "a");
    if (file == NULL) {
        perror("Failed to open file");
        return 1;
    }
    
    // File print then fflush() prevents the above fprintf() from being repeated
    fprintf(file, "Process ID,Parent Process ID\n");
    fflush(file);   

    printf("Enter the number of fork() calls: ");
    scanf("%hhu", &n);

    for (i = 1; i <= n; i++) {
        pid_t child_maker = fork();
        if (child_maker == 0) {
            fprintf(file, "%d,%d\n", getpid(), getppid());
            fflush(file);
        }
    }

    for (i = 1; i <= n; i++) {
        wait(NULL);     // equivalent to waitpid(-1, NULL, 0) or waitpid() with params of: pid_t child_pids[i]
    }

    fclose(file);
    return 0;
}