#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>

// Message data structure
struct message {
    long mtype;
    char mtext[500];
};

int main() {
    int parent_qid, child_qid;
    struct message chore_msg, reply_msg;
    pid_t pid;

    // Message queues
    parent_qid = msgget(IPC_PRIVATE, 0666);
    child_qid = msgget(IPC_PRIVATE, 0666);
    
    
    // Fork and detect for error at the same time
    if ((pid = fork()) == -1) {
        perror("fork failed");
        exit(1);
    }


    // Parent Process
    if (pid > 0) {  
        printf("I'm the parent %d! I need the following chore done:\n", getpid());

        // Sending msg
        printf("[INPUT CHORE] ");
        fgets(chore_msg.mtext, sizeof(chore_msg.mtext), stdin);
        chore_msg.mtype = 1;
        msgsnd(parent_qid, &chore_msg, sizeof(chore_msg.mtext), 0);
        printf("I sent the chore to my child\n");

        // Receive replying
        msgrcv(child_qid, &reply_msg, sizeof(reply_msg.mtext), 0, 0);
        printf("\nI'm the parent %d!\nMy child told me: %s\n", getpid(), reply_msg.mtext);

    } 
    
    
    // Child Process Replying
    else {  
        // Receiving msg
        msgrcv(parent_qid, &chore_msg, sizeof(chore_msg.mtext), 0, 0);
        printf("\nI'm child %d! My chore is: %s", getpid(), chore_msg.mtext);

        // Replying
        printf("[INPUT REPLY] ");
        fgets(reply_msg.mtext, sizeof(reply_msg.mtext), stdin);
        reply_msg.mtype = 1;
        printf("Sending my parent --> %s", reply_msg.mtext);
        msgsnd(child_qid, &reply_msg, sizeof(reply_msg.mtext), 0);
        printf("I sent my parent the reply\n");
    }


    // Destroy the message queues after the parent replies
    if (pid > 0) {
        msgctl(parent_qid, IPC_RMID, NULL);
        msgctl(child_qid, IPC_RMID, NULL);
    }

    return 0;
}