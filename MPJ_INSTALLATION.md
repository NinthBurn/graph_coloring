## MPJ Express Setup Guide

In order for MPJ Express to run properly, you must add some 
environment paths.

Below is a simplified version of the full guides by the MPJ
Express team. If you find that you still have trouble, then
please refer to the following:  
http://mpjexpress.org/docs/guides/linuxguide.pdf  
http://mpjexpress.org/docs/guides/windowsguide.pdf

Unfortunately there is no better solution and this is what
MPJ Express authors recommend.

### ⚠️ Note below ⚠️
Please replace **{ROOT_DIR}** with the path to the directory of
this project.  
e.g. C:\Users\darwj1\Documents\GraphColoring


### Windows: Adding environment paths
- Go to the environment path menu just like you know it
- Add a new entry in the system variables panel (the top 
    one is "user variables", don't use that one):

> Name: MPJ_HOME  
> Value: {ROOT_DIR}\mpj_express

- Edit the Path variable and append the following path:
> %MPJ_HOME%\bin


### UNIX: Adding environment paths
Run in a terminal:
```sh
export MPJ_HOME={ROOT_DIR}/mpj_express/
export PATH=$MPJ_HOME/bin:$PATH
```
