# HALInterpreter

## A HAL1957 Interpreter written for a student project

An interpreter for a simple Von Neumann implementation written in JAVA

> This is written in Java18 so you need a JRE18 to run this

At the moment the modeled RAM has addresses up to 0xFF

## Instruction set


| Name        | Operand     | Explanation         |
| ----------- | ----------- | ------------------- |
| START       |             | Starts the program  |
| STOP        |             | Stops the program   |
| IN          |             | Reads from the command line into the accumulator |
| OUT         |             | Print to the command line |
| LOAD        | r           | Loads the contents of register r into the accumulator |
| LOADNUM     | c           | Loads the constant k into the accumulator |
| STORE       | r           | Stores the content of the accumulator to register r |
| JUMPNEG     | a           | Jump to address a if accumulator < 0 |
| JUMPPOS     | a           | Jump to address a if accumulator > 0 |
| JUMPNULL    | a           | Jump to address a if accumulator = 0 |
| JUMP        | a           | Jump to address a |
| ADD         | r           | Adds the content of register r to the accumulator |
| ADDNUM      | c           | Adds the constant c to the accumulator |
| SUB         | r           | Substract the content of register r from the accumulator |
| SUBNUM      | c           | Substract the constant c from the accumulator |
| MUL         | r           | Multiplies the content of register r with the accumulator |
| MULNUM      | c           | Multiplies the constant c with the accumulator |
| DIV         | r           | Divides the accumulator by the content of register r |
| DIVNUM      | c           | Divides the accumulator by the constant c |
| LOADIND     | r           | Loads the register to which the contents of register r points (load indirect, ACC = [[r]]) into the accumulator |
| STOREIND    | r           | Stores the accumulator to the register to which the content of register r points to (store indirect) [[r]] = ACC |
