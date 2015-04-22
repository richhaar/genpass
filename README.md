# genpass

A Unix password generator, storing the encrypted passwords locally. Passwords are encrypted with Java's AES/CBC/PKCS5Padding mode via the `[lock-key "1.1.0"]` clojar.

Written with Clojure and managed with Leiningen.

## Installation

Download the repo.

## Usage

Create a jar with leiningen. You can run the jar as follows:

    $ java -jar genpass-0.1.0-standalone.jar [options] action [login]


###### Actions:
       gen <login>      Generate a new password

       get <login>      Get a password

       rem <login>      Remove a user:password mapping

       list             List all users


## Options

-l      Password length         e.g. -l30       (default 20)

-t      Clipboard timeout (ms)  e.g. -t1000     (default 8000)

-v      Verbosity of encryption e.g. -v, -vv, -vvv

-h      Show help

-s      Show password to stdout

How many verbose (-v) flags determine what sets of characters to use for encryption. With no flags the password will consist of uppers. With one flag (-v) the password could contain  uppers and lowers. And (-vv) will include nums into the previous level, and (-vvv) including special characters.

## Examples

The following demonstrates generating a password for `mywebsite` and retrieving it.

```
   $ java -jar genpass-0.1.0-SNAPSHOT-standalone.jar gen mywebsite -l25 -vvv -t3000
   $ enter password>
   $ Copied to clipboard

   $ java -jar genpass-0.1.0-SNAPSHOT-standalone.jar get mywebsite
   $ enter password>
   $ Copied to clipboard

   $ java -jar genpass-0.1.0-SNAPSHOT-standalone.jar list
   $ enter password>
   $ {"mywebsite" "mwyMTA9Jo(xaV0%)NCv3"}

   $ java -jar genpass-0.1.0-SNAPSHOT-standalone.jar get -s mywebsite
   $ enter password>
   $ mwyMTA9Jo(xaV0%)NCv3

   $ java -jar genpass-0.1.0-SNAPSHOT-standalone.jar rem mywebsite
   $ enter password>
   $ User removed
```

To re-generate a password just call gen with the same login. To make the command easier, add an alias for each command.

### Local password encryption

By default, it will generate a .genpass file in your homedirectory (if it doesn't exist). It will encrypt it's contents (With  with a supplied password) using symmetric encryption in AES/CBC/PKCS5Padding mode. If you are sharing the machine, be sure to chmod the .genpass file to allow only the owner (you) read and write permission.

Un-encrypted contents are stored in a CSV format i.e (login1,randpass1\nlogin2randpass2\n).

### Pasting to clipboard

It is possible for the clipboard to be read by unwanted programs, therefore the clipboard is cleared after the specified time (default 8 seconds after a get/set call). It is also possible to disable clipboard reading in certain browsers such as Firefox.
 
### TODO

Add an option to change encryption password.

Add tests.

## License

Copyright © 2015 Richard Haar

Distributed under the Eclipse Public License version 1.0.
