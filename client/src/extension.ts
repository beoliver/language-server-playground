// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as path from "path";
import { commands, ExtensionContext, window } from "vscode";
import {
  Executable,
  LanguageClient,
  LanguageClientOptions,
  ServerOptions
} from "vscode-languageclient";

// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export function activate(context: ExtensionContext) {
  const serverPath = context.asAbsolutePath(
    path.join("bin", "language-server.sh")
  );

  const serverExecutable: Executable = {
    command: serverPath,
    args: []
  };

  const serverOptions: ServerOptions = {
    run: serverExecutable,
    debug: serverExecutable
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: [
      {
        scheme: "file",
        language: "plaintext"
      }
    ]
  };

  const client = new LanguageClient(
    "hoverExample",
    "Language Server Hover Example",
    serverOptions,
    clientOptions
  );
  const disposable1 = client.start();
  context.subscriptions.push(disposable1);
  // Use the console to output diagnostic information (console.log) and errors (console.error)
  // This line of code will only be executed once when your extension is activated
  console.log(
    'Congratulations, your extension "language-server-playground" is now active!'
  );
  console.log("context", context);

  // The command has been defined in the package.json file
  // Now provide the implementation of the command with registerCommand
  // The commandId parameter must match the command field in package.json
  const disposable2 = commands.registerCommand("extension.helloWorld", () => {
    // The code you place here will be executed every time your command is executed

    // Display a message box to the user
    window.showInformationMessage("Hello, world!");
  });

  context.subscriptions.push(disposable2);
}

// this method is called when your extension is deactivated
export function deactivate() {}
