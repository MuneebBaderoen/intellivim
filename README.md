IntelliVim [![Build Status](http://img.shields.io/travis/dhleong/intellivim.svg?style=flat)](https://travis-ci.org/dhleong/intellivim)
==========

IntelliVim aims to expose the features from [IntelliJ](https://www.jetbrains.com/idea/) inside of [Vim](http://www.vim.org/),
in the spirit of [Eclim](http://eclim.org).


## Features

While IntelliVim is still in the very early stages of development, and most features
should still be assumed to be at the "proof of concept" stage, it does have a few tricks up its sleeve already.

### Problem detection

Problems are marked with visual signs and added to the buffer's location list. Sometimes
they can be fixed with `:FixProblem`.

### Autocomplete

Autocomplete is bound to omnifunc (`<c-x><c-o>`) and should work with [YouCompleteMe](https://github.com/Valloric/YouCompleteMe) out of the box.

#### Parameter Hints

If [VeryHint](https://github.com/dhleong/vim-veryhint) is installed, parameter "hints" are shown inline.

### Commands

`:FindImplementations`  Populate and open the quickfix buffer with locations of
implementations of the element under the cursor. If there was a single result,
it is opened directly.

`:FindUsages`  Populate and open the quickfix buffer with locations of
usages for the element under the cursor. If there was a single result,
it is opened directly.

`:FixProblem`  Provide options for fixing the problem under the cursor.
Press `enter` on the desired fix to attempt it. "Import Class" usually works,
but other fixes are not thoroughly tested yet.

`:GotoDeclaration`  Jump to the declaration of the element under the cursor.
A split is opened if the declaration is in another file

`:GetDocumentation`  Display the documentation for the element under the cursor
in a preview window.

`:Implement`  Generate method implementations/overrides. 
Press `enter` on a method to implement it, and `q` to quit. 
Visual selection to implement multiple at a time is supported.

`:JavaNew <type> <name>`  Create a new Java type (`class`, `@interface`, etc.)
with the given name. If provided eg `org.intellivim.Test` the class will be
created in that package. Otherwise, IntelliVim will attempt to creat it in
the same package as the current file (if any).

`:JavaOptimizeImports`  Attempts to automatically add imports and organize them.
Prompts for disambiguation if necessary. As the name implies, this one is currently
Java only. Future work could let this pick the right implementation based on filetype
(as `RunTest` does).

`:Locate [type]` Opens a search window for locating files. Optionally pass the type
of thing to locate: 
 - `file` Search by file name/path (default)
 - `class` Search by class name

`:Rename [newName]` Rename the element under the cursor. If a new name isn't passed
to this command, a small buffer is opened so you can edit the name with all your
usual vim bindings. Pressing `enter` in this buffer will commit the change; `ctrl-c`
or `q` will cancel. When pressing `enter`, if the value has not changed, or if it is
empty, nothing will happen.

`:RunProject [config]` Builds and runs the current project, opening a split to contain
the output. Closing the split will terminate the execution, as will calling 
`:Terminate` from inside that window. Optionally pass `config` to specify which
configuration to launch. Tab-completion is supported.

`:RunList` List run configurations and their types.

`:RunTest` Builds and runs the test case under the cursor, opening a split to contain
the output with fancy test status visualization. Currently supports JUnit tests (See #5)

#### Mappings

With the exception of setting the `omnifunc` per-buffer, IntelliVim does not come with
any mappings by default. Feel free to use mine:

```vim
" 'java implement'
nnoremap <leader>ji :Implement<cr>
" 'java correct'
nnoremap <leader>jc :FixProblem<cr>
" 'fix imports'
nnoremap <leader>fi :JavaOptimizeImports<cr>
" muscle memory from eclim ("ProjectRun")
nnoremap <leader>pr :RunProject<cr>
" muscle memory from vim-fireplace
nnoremap cpr :RunTest<cr>
nnoremap gd :GotoDeclaration<cr>
nnoremap K :GetDocumentation<cr>
```

## Building

IntelliVim builds with [Gradle](https://gradle.org). It is probably not at a point
yet where we could upload it to the IntelliJ plugin repository, but you can build
it yourself to play around with by cloning or forking this repo to your local machine
and running:

    ./gradlew clean buildPlugin

Gradle will assemble an installable .zip file in the `build/distributions/` directory.

## Other Editors

There is no plan to offer first-party support for any editor besides Vim. That said,
any editor that can make HTTP requests should be able to integrate with the server
component that runs inside IntelliJ. Commands are `POST`'d to the server as a JSON
object that gets inflated directly into an instance of `ICommand` and `execute()`'d.
The result is returned as a JSON object with an `error` key if anything went wrong,
else a `result` key containing whatever expected result for the command.

Some commands make use of or require asynchronous execution, and so must have
client-specific code. This is facilitated through the use of `@Inject`'d interfaces,
with implementations annotated with `@ClientSpecific`. For an example, see `RunCommand`,
its `AsyncRunner` interface, and and the `VimAsyncRunner` implementation.
