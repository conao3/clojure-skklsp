# clojure-skklsp

An LSP (Language Server Protocol) server for SKK Japanese input method, written in Clojure.

## Overview

clojure-skklsp provides SKK-style Japanese input capabilities through the Language Server Protocol. It handles romaji-to-kana conversion using the SKK input method, enabling Japanese text input in any LSP-compatible editor.

## Features

- LSP-compliant server implementation using JSON-RPC
- Romaji to hiragana conversion with SKK-style rules
- Non-blocking I/O using Java NIO for efficient connection handling
- Per-session state management for multiple concurrent clients

## Requirements

- Java 11 or later
- Clojure 1.12.0 or later

## Installation

Clone the repository and install dependencies:

```bash
git clone https://github.com/conao3/clojure-skklsp.git
cd clojure-skklsp
```

## Usage

Start the LSP server:

```bash
clojure -M -m skklsp.core
```

The server listens on a TCP socket and accepts connections from LSP clients.

### Editor Integration

Configure your editor's LSP client to connect to the skklsp server. The server implements the `workspace/executeCommand` capability with the `inputKey` command for handling key input.

## Development

Start a REPL with development tools:

```bash
clojure -M:dev
```

Run tests:

```bash
clojure -M:test
```

## Architecture

- `skklsp.core` - Server entry point and NIO-based connection handling
- `skklsp.handler` - LSP method handlers and JSON-RPC dispatch
- `skklsp.kana` - Romaji to kana conversion rules
- `skklsp.subr` - Utility functions for serialization and parsing

## License

MIT
