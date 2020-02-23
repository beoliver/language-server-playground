# language-server-playground

The [Language Server Protocol](https://microsoft.github.io/language-server-protocol/specifications/specification-current/) (LSP) provdes a way for editors to speak to servers - think code completion, static analysis of programs etc.

This repo serves as starting point for getting such a server up an running. The one caveat being that the server is written in clojure.

While language servers using the protocol can speak with different editors, the goal of this project is to set up a language server and client that can be used with [Visual Studio Code](https://code.visualstudio.com/) (VSCode).

The interesting aspect is that we are not going to use JavaScript (or TypeScript) to create the server. This is important as VSCode runs using [Node.js](https://nodejs.org/). Insetead, we will be implementing the server in clojure, and use a shell file `server.sh` that sets up the communication with our server.

## The Client

A simnple client has been included in this repo. It should be enough to get things up an running. Just open the client directory in VSCode (yarn install if needed) and use the play button that in the debug menu.

### Lets assume the bottom and start from the top

For now we will assume that we have already written a server that communicates using sockets.
Our **language server** is just a shell script `language-server.sh` that we keep in the `bin` directory located in the `client` (VSCode) directory. By doing this you should not have to make any changes to the client in order for things to work out of the box.

The `language-server.sh` uses netcat (`nc`) to redirect `stdin` to port `9999` and redirect responses to `stdout`. From the perspective of the VSCode client it is communicating with the _server_ using stdin and stdout.

```bash
#!/bin/bash
ADDR=localhost
PORT=9999

nc $ADDR $PORT
```

To install the client, you need to `cd` into the `client` directory and run `yarn install`.
This will pull down the dependencies for the client (VSCode javascript stuff).

### Note

For now, this means that our server must be started by us manually and set to listen on this port.

## Communication

The Language Server Protocol (from here on LSP) uses something called [JSON-RPC](https://www.jsonrpc.org/specification) to pass messages. `JSON` refers to `J`ava`S`cript `o`bject `n`otation, while `RPC` is the acronym for `R`emote `P`rocedure `C`all.

Messages are just JSON encoded objects passed along with a header that have the following structure

```
Content-Length: <number>\n\r
Content-Type: jsonrpc
\n\r
<JSON>
```

The `RPC` part comes from the fact that the JSON payload can have a `method` key and a `parameters` key.
