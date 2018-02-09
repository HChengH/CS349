# CS349 A1
Student: h33cheng
Marker: Xiaowei Kuang


Total: 37 / 38 (97.37%)

Code: 
(CO: won’t compile, CR: crashes, FR: UI freezes/unresponsive, NS: not submitted)


Notes:   

## REQUIREMENTS

1. [2/2] Create a 850 x 250 window with a window title “Frog”.

2. [2/2] Pressing ‘q’ exits the program.

3. [2/2] Draw a 50px by 50px rectangle as a Frog at the center bottom of the window (we call this position as “initial position”).

4. [2/2] Use Displayables and a display list for all your shapes.

5. [2/2] When arrow keys are pressed, the Frog moves 50px to the direction specified with the arrow key.

6. [2/2] Redraw window at frame rate 30FPS as a default setting. Change the frame rate based on the function parameter. (ex. Change frame rate to 60FPS if you > ./a1-basic 60).

7. [2/2] The Frog should always stay inside of the window. For example, the Frog should not go outside of the window in a Down key event when the Frog is at the bottom of the window. In this case, the Frog should stay in place.

8. [2/2] Draw text “Level: N” at the top right of the window. N showing the current level starts at 1.

9. [2/2] When the Frog is in the goal area (top of the window), user can start next level of the game by pressing ‘n’ key. When ‘n’ key is pressed, the Frog moves back to the initial position and the level is increased by 1.

10. [2/2] The Frog can move left and right but not up or down after it reached the goal area. It should be able to move up and down once again a level or a new game is started.

11. [2/2] ‘n’ key should work only after the Frog reached the goal area. If ‘n’ key is pressed when the Frog is not at the goal area, do nothing.

12. [2/2] Draw three 50px width blocks in the first line 50 px below the top of the window, four 20px width blocks in the second line, and two 100px width blocks in the third line. All blocks in each line must be evenly spread across the window width. The height of all of the blocks should be 50px. See image below for the size and initial location of each blocks and the Frog.

13. [2/2] The top and bottom lines of the blocks move to the right and middle line of the blocks move to the left. All of the blocks move at the same speed. At the level 1, the blocks move at the speed of 1px per frame.

14. [2/2] When the level goes up, the speed of the blocks goes up by 1px per frame. (level1: 1px/frame, level2: 2px per frame, level3: 3px per frame …)

15. [2/2] At the moment when a block reached one side of the window, a new block comes out from the other side of the window.

16. [2/2] Detect collision between the blocks and the Frog. If the Frog and a block collide, reset the level to 1 and move the frog back to the initial position.

## ENHANCEMENT [2/2]

## GENERAL [3/4] (can be negative)

-1 some comments, but poor or token ones
