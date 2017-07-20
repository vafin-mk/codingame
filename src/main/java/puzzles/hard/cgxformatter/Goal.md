Your mission: write a program that formats CGX content to make it readable!

Beyond the rules below, the displayed result should not contain any space, tab, or carriage return. No other rule should be added.​

The content of strings of characters must not be modified.

A BLOCK starts on its own line.

The markers at the start and end of a BLOCK are in the same column.

Each ELEMENT contained within a BLOCK is indented 4 spaces from the marker of that BLOCK.

A VALUE_KEY starts on its own line.e.

A PRIMITIVE_TYPE starts on its own line unless it is the value of a VALUE_KEY.

INPUT:

Line 1: The number N of CGX lines to be formatted

The N following lines: The CGX content. Each line contains maximum 1000 characters. All the characters are ASCII.

OUTPUT:

The formatted CGX content

CONSTRAINTS :

The CGX content supplied will always be valid.

The strings of characters do not include the character '

0 < N < 10000