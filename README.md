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

# Build and Test
To build and test, open the project using IntelliJ IDEA, Netbeans or other Java IDE and run the program

# How does it work
On the first start the program will create a config.cfg file to save the settings and will create 3 folders Fall_Into_Oblivion, Trashed, Restored.

The folders Trashed and restored will be inside the Fall_Into_Oblivion folder

Once these folders are created and the software is running you can move the files into the root of the folder "Fall_Into_Oblivion" and not the "Trashed" folder, the software will then sign and encrypt, with a random four digit pin unknown to the user, the file and will create a hash of the encrypted file.

The unencrypted file is now deleted and inside the trashed folder you have another folder with the file name and extension, inside that folder there should be four files. filename.ext.aescbc filename.ext.sig filename.ext.pk filename.ext.aescbc.hash

These files contain the encrypted file, the signature, the public key and the hash of the encrypted file

Upon decryption the software first check the integrity of the encrypted file if it's ok it tries tries to decrypt the file.

If the pin is incorrect the software will search for the files .filename.ext.lock, .lock.sig and lock.pk if none of these files are not found the software will create them.

The file filename.ext.lock contains the hash of the attempt and filename

After three attempts the file is deleted

If there is only one file missing lock.sig, lock.pk or .filename.ext.lock the system deletes the file

# Developers

André Ricardo

Miguel Brandões

Pedro Cavaleiro

Raúl Barbosa

Ricardo Cardoso
