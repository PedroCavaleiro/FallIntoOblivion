# Introduction 
This project is a prototype of a encrypted recycle bin.

All files are encrypted with an random pin and they're also signed digitally.

The pin must not be outputed to the user nor saved anywere on the computer.

The user, to recover the file, has 3 attempts to guess the pin of the file, if it fails to guess the pin the file gets deleted.

The software also checks for errors on the encrypted file upon the decryption process

# Available Cyphers
So far there is only one cypher implemented, AES-CBC

# Available HASH
So far there is only on hash algorithm, SHA256 being used to generate the secure key and to create a hash for the encrypted file

# Commands available
restorefile

validatefile

setcypher

sethash

setenabled

showconfig

help

exit


# Restore a file
restorefile [options]

-l               lists the encrypted files

-p <pin> <file>  tries to decrypt the file using the given pin

This command can only contain one of the options


# Validate a file
validatefile [file]

The file can be an absolute path or if the file it's in the restored folder there's only the need to type the file name

The Signature (.sig) and Public Key (.pk) must be in the same directory of the file being checked

# Settings
setchypher [cypher] [keysize]

The keysize for now it's allways 16 as there is a problem with java that gives allways Ilegal Key Size even when the sizes are 24 or 32 (valid key sizes for AES-CBC)


sethash [hash]

Sets the hash algorithm that will be used to generate the hash of the encrypted file


setenabled [boolean]

Setting this setting to true or false will enable or disable the encryption process


# Other Commands
help

This show in app help menu explaining all the commands


exit

Terminates the program and all it's threads


# TODO!
CHECK FILE Helpers.java line 223          (follow instructions in TODO tag)

CHECK FILE Helpers.java line 258          (follow instructions in TODO tag)

Implement the decryption check on line 269 on file Helpers.java


# Build and Test
To build and test, open the project using IntelliJ IDEA, Netbeans or other Java IDE and run the program