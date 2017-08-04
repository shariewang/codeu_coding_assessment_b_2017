// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import com.google.codeu.mathlang.core.tokens.*;
import com.google.codeu.mathlang.parsing.TokenReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {

  private String source;
  private StringBuilder builder;
  private int curr;
  private static final Set<String> SYMBOLS = new HashSet<>(Arrays.asList("-", "=", "+"));

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).
    this.source = source;
    this.curr = 0;
    this.builder = new StringBuilder();
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.

    //skip all leading whitespace
    while (remaining() > 0 && Character.isWhitespace(peek())) {
      read();
    }
    if (remaining() <= 0) {
      return null;
    } else if (peek() == ';') {
      return new SymbolToken(read());
    } else if (peek() == '"') {
      return readQuotedToken();
    } else {
      return readNonQuotedToken();
    }
  }

  private int remaining() {
    return source.length() - curr;
  }

  private char peek() throws IOException {
    if (curr < source.length()) {
      return source.charAt(curr);
    } else {
      throw new IOException("No more characters");
    }
  }

  private char read() throws IOException {
    curr++;
    return peek();
  }

  private Token readQuotedToken() throws IOException {
    builder.setLength(0); //clear
    read(); // opening quote
    while (peek() != '"') {
      builder.append(read());
    }
    read(); // closing quote
    return new StringToken(builder.toString());
  }

  private Token readNonQuotedToken() throws IOException {
    builder.setLength(0); //clear

    while(peek() != ' ') {
      builder.append(read());
      // handling cases in which no more should be added to current token
      char next = peek();
      if (next == ';' || SYMBOLS.contains(Character.toString(next)) || Character.isDigit(next)) {
        break;
      }
    }

    String word = builder.toString();
    try {
      return new NumberToken(Double.parseDouble(word));
    } catch (Exception ignored) {}

    if (SYMBOLS.contains(word)) {
      return new SymbolToken(word.charAt(0));
    } else if (word.charAt(0) >= 65 && word.charAt(0) <= 122) {
      return new NameToken(word);
    } else {
      throw new IOException("Invalid input");
    }
  }
}
