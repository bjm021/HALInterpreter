# HALInterpreter

## A HAL1957 Interpreter written for a student project

An interpreter for a simple Von Neumann implementation written in JAVA

> This is written in Java18 so you need a JRE18 to run this

At the moment the modeled RAM has addresses up to 0xFF

## Usage

```cli
java -jar HALInterpreter.jar [-d, --debug] programFile.txt
```

## Program files

The program files are simple ```.txt``` files.
Each lines consists of three parts, a line number, a instruction, and an optional operand.

```
10 LOADNUM 5
^  ^       ^
I  I       Operand
I  Instruction name
Line number
```

Note that line numbers don't need to be in the correct order to be executed in the correct order.<br>
Line numbers are seen as addresses in the folowwing table. ```JUMP 10``` will jump to line 10. <br>
Also note that lines can be skipped.
```
1 START
10 LOADNUM 12
15 OUT
20 STOP
```
will work just fine. Every line that isn't present will be skipped automatically, so jumping to a non-existant line is possible ans will skip until the next existing line is found.

Empty lines in the file will be ignored together with comments starting the line with ```//```

### Example program

The following program will ask for two values from the user and add them and output the result.

```
1 START

2 IN
// we need to store the first IN because the second IN will override the accumulator with its value
3 STORE 10
4 IN
// ww can add the saved first value in register 10 with the second input that was read into the accumulator
5 ADD 10
6 OUT
8 STOP
```
Note that we only have one accumulator to work with so we need to store the first number in storage so we can use it later.

## Instruction set


| Name        | Operand     | Explanation         |
| ----------- | ----------- | ------------------- |
| START       |             | Starts the program  |
| STOP        |             | Stops the program   |
| IN          | p           | Reads from the I/O Buffer on port p (Leave empty to read from STDIN / user prompt) |
| OUT         | p           | Writes to the I/O Buffer on port p (Leave empty to write on STDOUT) |
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

<hr>

# HAL OS
Create networks of multiple interconnected HAL Interpreters.

You can run the halOS class with an OS Config file to created multiple HAL interpreters thet run simultaneously.

## Config file format 

```json
{
  "hal": [
    {
      "id": "1",
      "program-file": "./p1.txt"
    },
    {
      "id": "2",
      "program-file": "./p2.txt"
    }
  ],
  "connections": [
    {
      "startID": "1",
      "startPort": 3,
      "destID": "2",
      "destPort": 2
    },
    {
      "startID": "STDIN",
      "destID": "1",
      "destPort": 0
    },
    {
      "startID": "2",
      "startPort": 3,
      "destID": "STDOUT"
    }
  ]
}
```

The ```hal``` section describes how many interpreters exist in the network and what program they should run. The ```connections``` section describers the I/O buffers of each interpreter. <br>
The first connection in the example above causes hal interpreter 1 to have an I/O buffer on port 3 that can be written to and hal interpreter 2 to have that I/O buffer on port 2 to read from (these buffers are directional). In hal code this connection could be used as ```OUT 3``` on hal 1 and ```IN 2``` on hal 2. <br>
If a hal interpreter reads from an I/O Buffer that has not been written to it waits until the sending interpreter sends a value to the I/O buffer.<br>
You can use ```STDIN``` as a connection ```startID``` to prompt the user for a console input and ```STDOUT``` as a ```destID``` to write to the console.
